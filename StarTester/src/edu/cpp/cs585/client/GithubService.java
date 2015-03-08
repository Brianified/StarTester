package edu.cpp.cs585.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("github")
public interface GithubService extends RemoteService{
	Map<String, Integer> getAsserts(String url) throws IllegalArgumentException;
}
