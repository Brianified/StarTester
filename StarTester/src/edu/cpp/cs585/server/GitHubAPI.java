package edu.cpp.cs585.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cpp.cs585.shared.TestDiff;

/**
 * @author Brian
 *
 */
public class GitHubAPI {

	private final String USER_AGENT = "Mozilla/5.0";
	private String gitHubURL;
	
	public static void main(String[] args) throws Exception {
 
		GitHubAPI http = new GitHubAPI("Brianified", "ibox");
		List<TestDiff> commits = http.getFiles("61a33847a5b537da647cd4dc8dc63c0c29a8bfe3");
		//System.out.println("# commits "+ commits.size());
		for(TestDiff diff : commits){
			System.out.println("email :"+ diff.getEmail() + " filename : "+ diff.getFilename() +"asserts: "+diff.getAsserts());
			
		}
		//System.out.println("Testing 1 - Send Http GET request");
		//String url = " https://api.github.com/repos/Brianified/ibox/commits/61a33847a5b537da647cd4dc8dc63c0c29a8bfe3";
		//http.sendGet(url);
 
		//System.out.println("\nTesting 2 - Send Http POST request");
		//http.sendPost();
 
	}
 
	public GitHubAPI(String owner, String repo){
		gitHubURL = "https://api.github.com/repos/"+owner+"/"+repo+"/";
	}
	
	/**
	 * Get all the commits of a single repository. Owner and repo
	 * must be instantiated beforehand
	 * @return a List of sha values associated to each commit
	 * @throws Exception 
	 */
	public List<String> getAllCommits() throws Exception{
		String url = gitHubURL + "commits";
		String responce = sendGet(url);
		String lastsha = "";
		String current = "";
		List<String> commits = new ArrayList<String>();
		boolean same = false;
		do {
			List<String> currentCommits  = parseCommits(responce);
			lastsha = currentCommits.get(currentCommits.size()-1);
			if(!current.contentEquals(lastsha)){
				commits.addAll(currentCommits);
				current = commits.get(commits.size()-1);
				commits.remove(current);
				responce = sendGet(url + "?per_page=100&sha="+current);
			}else{
				commits.add(lastsha);
				same = true;
			}
		} while (!same);
		
		return commits;
	}
	
	/**
	 * input the sha code of a commit to find all 
	 * the files modified by the commit
	 * @param sha 
	 * @return filename changed within the commit
	 * @throws Exception 
	 */
	public List<TestDiff> getFiles(String sha) throws Exception{
		String url = gitHubURL + "commits/" +sha; 
		String responce = sendGet(url);
		List<TestDiff> files =  parseFiles(responce);
		return files;
	}
	
	private List<TestDiff> parseFiles(String responce) {
		// TODO Auto-generated method stub
		String[] split = responce.split("diff --git");
		String fromRegex = "From:.*<(.*)>";
		String addRegex = "\\+.*(assert.*;|verify.*;)";
		String fileRegex = "a/(.*) b/(.*)";
		Pattern fromPattern = Pattern.compile(fromRegex);
		Pattern addPattern = Pattern.compile(addRegex);
		Pattern.compile(fileRegex);
		
		Matcher fromMatcher = fromPattern.matcher(split[0]);
		Matcher addMatcher;
		String from = null;
		if(fromMatcher.find()){
			from = fromMatcher.group(1);
		}
		List<TestDiff> testDiffs = new ArrayList<TestDiff>();
		for(int i = 1; i < split.length; i++){
			addMatcher = addPattern.matcher(split[i]);
			String diffDetail = split[i];
			String filename = diffDetail.substring(diffDetail.indexOf("a/")+2, diffDetail.indexOf("b/"));
			
			if(filename.lastIndexOf('/')>=0){
				filename = filename.substring(filename.lastIndexOf('/')+1);
			}
			if(filename.contains("Test.java")){
				TestDiff diff = new TestDiff();
				diff.setEmail(from);
				diff.setFilename(filename);
				addMatcher = addPattern.matcher(split[i]);
				int assertCount = 0;
				while(addMatcher.find()){
					assertCount++;
				}
				//System.out.println(filename);
				//System.out.println(assertCount);
				diff.setAsserts(assertCount);
				testDiffs.add(diff);
			}
				
		}
		return testDiffs;
	}

	private List<String> parseCommits(String inputLine) {
		// TODO Auto-generated method stub
		String sectionRegex = "\\{.*?\\]\\}";
		String shaRegex = "\"sha\":\".*?\"";
		
		Pattern sectionPattern = Pattern.compile(sectionRegex);
		Pattern shaPattern = Pattern.compile(shaRegex);
		
		Matcher sectMatcher = sectionPattern.matcher(inputLine);
		
		List<String> commits = new ArrayList<String>();
		while(sectMatcher.find()){
			String section = sectMatcher.group(0);
			Matcher shaMatcher = shaPattern.matcher(section);			
			
			if(shaMatcher.find()){
				String commit = shaMatcher.group();
				String[] split = commit.split("[\\[\\s{\":,]+");
				String sha = split[2];
				commits.add(sha);
			}
		}
		return commits;
	}
	
	// HTTP GET request
	private String sendGet(String url) throws Exception {
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
		
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept", "application/vnd.github.patch");
		con.setRequestProperty("Authorization", "token 38b0ac0cec05358b94afb5bd217d91de74170778");
		con.setRequestProperty("X-OAuth-Scopes", "gist, repo, user");
		con.setRequestProperty("X-Accepted-OAuth-Scopes", "user");
 
		//int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine+"\n");
			//parse(inputLine);
		}
		in.close();
 
		//print result
		//System.out.println(response.toString());
		return response.toString();
	 
	}

	
}
