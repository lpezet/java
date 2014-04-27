package com.github.lpezet.java.patterns.worker.samples;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Searcher {
	
	private Logger mLogger = LoggerFactory.getLogger(Searcher.class);
	
	public SearchResults search(SearchParameters pParams) throws Exception {
		mLogger.info("running search(" + pParams + ")...");
		List<Integer> oResults = new ArrayList<Integer>();
		for (int i = pParams.getFrom(); i <= pParams.getTo(); i++) {
			Thread.sleep(i*2);
			oResults.add(i);
		}
		return new SearchResults(oResults);
	}
}