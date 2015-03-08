package edu.cpp.cs585.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GithubServiceAsync {
	void getAsserts(String input, AsyncCallback<Map<String, Integer>> callback)
			throws IllegalArgumentException;
}
