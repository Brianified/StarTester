package edu.cpp.cs585.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.cpp.cs585.client.GithubService;
import edu.cpp.cs585.shared.TestDiff;

public class GithubServiceImpl extends RemoteServiceServlet implements GithubService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5358936177977792231L;
	private GitHubAPI service;
	@Override
	public Map<String, Integer> getAsserts(String url)
			throws IllegalArgumentException {
		
		Map<String, TestDiff> testList = null;
		Map<String, Integer> sortTester = null;
		if(url.matches("https://github.com/[^/]*/[^/]*/commits/.*")){
			String[] split = url.split("/");
			String owner = split[3];
			String repo = split[4];
			service = new GitHubAPI(owner, repo);
			testList = new HashMap<String, TestDiff>();
			try {
				List<String> commits = service.getAllCommits();
				for(String sha : commits){
					List<TestDiff> diffs = service.getFiles(sha);
					for(TestDiff diff : diffs){
						String email = diff.getEmail();
						int numAsserts = diff.getAsserts();
						if(testList.containsKey(email)){
							int asserts = diff.getAsserts();
							asserts= asserts + numAsserts;
							TestDiff test = new TestDiff();
							test.setEmail(email);
							test.setAsserts(asserts);
							testList.put(email, test);
						}else{
							TestDiff test = new TestDiff();
							test.setEmail(email);
							test.setAsserts(numAsserts);
							testList.put(email, test);
							//testList.put(email, numAsserts);
							//testerList.add(diff);
						}
					}
				}
				List<TestDiff> sortedList = new ArrayList<TestDiff>(testList.values());
				//System.out.println("");
				Collections.sort(sortedList);
				sortTester = new HashMap<>();
				for(int i = 0; i < sortedList.size() && i < 10; i++){
					String email = sortedList.get(i).getEmail();
					int asserts = sortedList.get(i).getAsserts();
					sortTester.put(email, asserts);
				}
		       
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new IllegalArgumentException("Error in Github APi: "+ e.toString());
			}
			
			
		}else{
			throw new IllegalArgumentException("Link is not a valid Github commit History");
		}
		
		return sortTester;
	}
	


}
