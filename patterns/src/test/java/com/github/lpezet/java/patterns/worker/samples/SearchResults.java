package com.github.lpezet.java.patterns.worker.samples;

import java.util.List;

class SearchResults {
	private List<Integer> mResults;
	public SearchResults(List<Integer> pResults) {
		mResults = pResults;
	}
	public List<Integer> getResults() {
		return mResults;
	}
}