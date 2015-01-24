/**
 * The MIT License
 * Copyright (c) 2014 Luc Pezet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.lpezet.java.patterns.worker.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.patterns.worker.IResultMerger;
import com.github.lpezet.java.patterns.worker.IWorkSplitter;
import com.github.lpezet.java.patterns.worker.IWorker;
import com.github.lpezet.java.patterns.worker.SimpleSPMWorker;
import com.github.lpezet.java.patterns.worker.Splitters;

public class SearchWorkerSample {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchWorkerSample.class);
	
	/**
	 * Merger of domain results
	 */
	private static class SearchMerger implements IResultMerger<SearchResults> {
		@Override
		public SearchResults merge(Collection<SearchResults> pSource) {
			List<Integer> oResults = new ArrayList<Integer>();
			for (SearchResults r : pSource) {
				oResults.addAll(r.getResults());
			}
			return new SearchResults(oResults);
		}
	}
	
	/**
	 * Splitter of domain parameter/work.
	 */
	private static class SearchSplitter implements IWorkSplitter<SearchParameters> {
		
		private int mChunkSize = 10;
		
		@Override
		public Collection<SearchParameters> split(SearchParameters pWork) {
			List<SearchParameters> oResult = new ArrayList<SearchParameters>();
			int oAmountOfWork = count(pWork); 
			if (oAmountOfWork < mChunkSize) {
				LOGGER.debug("Not splitting work: too small.");
				oResult.add(pWork);
			} else {
				int n = (int) Math.ceil( oAmountOfWork / (double) mChunkSize );
				LOGGER.debug("Dividing up work into " + n + " chunks.");
				int oTo, oFrom;
				for (int i = 0; i < n; i++) {
					oFrom = pWork.getFrom() + i * mChunkSize;
					oTo = Math.min(pWork.getTo(), oFrom + mChunkSize - 1);
					oResult.add(new SearchParameters(oFrom, oTo, pWork.getFilters()));
				}
			}
			return oResult;
		}
		private int count(SearchParameters pWork) {
			return pWork.getTo() - pWork.getFrom();
		}
	}
	
	/**
	 * Wrapper on domain searcher.
	 */
	private static class SearchWorker implements IWorker<SearchParameters, SearchResults> {
		
		private Searcher mSearcher;
		public SearchWorker(Searcher pSearcher) {
			mSearcher = pSearcher;
		}
		
		@Override
		public SearchResults perform(SearchParameters pWork) throws Exception {
			return mSearcher.search(pWork);
		}
	}
	
	/**
	 * Running a search using domain classes. No workers involved here.
	 * 
	 * @throws Exception
	 */
	@Test
	public void domain() throws Exception {
		Searcher s = new Searcher();
		long oStart = System.currentTimeMillis();
		SearchResults oResults = s.search(new SearchParameters(1, 50, "something here"));
		long oEnd = System.currentTimeMillis();
		LOGGER.info("Execution time: " + (oEnd - oStart) + "ms.");
		assertNotNull(oResults);
		assertNotNull(oResults.getResults());
		assertEquals(50, oResults.getResults().size());
	}
	
	/**
	 * Using new Search Worker to run a search with domain classes. No splitting or merging here since the work is considered too small to split (as specified in SearchSplitter).
	 * 
	 * @throws Exception
	 */
	@Test
	public void searchWithWorker() throws Exception {
		ExecutorService oES = Executors.newCachedThreadPool();
		Searcher s = new Searcher();
		
		SearchSplitter oSP = new SearchSplitter();
		SearchMerger oSM = new SearchMerger();
		SearchWorker oSW = new SearchWorker(s);
		
		assertWorks(oES, oSP, oSM, oSW, new SearchParameters(1, 5, "something here"));
	}
	
	/**
	 * Using Search Worker to run a search with domain classes. Here the work will be split and the results merged. And this is without changing how the search gets called.
	 * A ISplitter and IMerger needs to be specified here.
	 * 
	 * @throws Exception
	 */
	@Test
	public void searchWithSPMWorkers() throws Exception {
		ExecutorService oES = Executors.newCachedThreadPool();
		Searcher s = new Searcher();
		
		SearchSplitter oSP = new SearchSplitter();
		SearchMerger oSM = new SearchMerger();
		SearchWorker oSW = new SearchWorker(s);
		assertWorks(oES, oSP, oSM, oSW, new SearchParameters(13, 65, "something here"));
	}

	
	
	/**
	 * Same as searchWithSPMWorkers but using provided utility classes like Splitters.
	 * Splitters provides fully tested logic to help split work.
	 * 
	 * @throws Exception
	 */
	@Test
	public void searchWithSPMWorkersUsingSplitters() throws Exception {
		ExecutorService oES = Executors.newCachedThreadPool();
		Searcher s = new Searcher();
		
		Splitters.ISimpleSplitter<SearchParameters> oSplitter = new Splitters.ISimpleSplitter<SearchParameters>() {
			@Override
			public int count(SearchParameters pWork) {
				return pWork.getTo() - pWork.getFrom();
			}
			@Override
			public SearchParameters newSplit(int pFrom, int pTo, SearchParameters pWork) {
				return new SearchParameters(pWork.getFrom() + pFrom - 1, Math.min(pTo + pWork.getFrom() - 1, pWork.getTo()), pWork.getFilters());
			}
		};
		
		IWorkSplitter<SearchParameters> oSP = null;
		SearchMerger oSM = new SearchMerger();
		SearchWorker oSW = new SearchWorker(s);
		
		LOGGER.info("Splitting by chunk size...");
		oSP = Splitters.splitByChunkSize(10, oSplitter);
		assertWorks(oES, oSP, oSM, oSW, new SearchParameters(13, 65, "something here"));
		
		LOGGER.info("Splitting by chunks...");
		oSP = Splitters.splitByChunks(3, 10, oSplitter);
		assertWorks(oES, oSP, oSM, oSW, new SearchParameters(13, 65, "something here"));
	}
	
	/**
	 * This is to test the performance based on number of threads available in ExecutorService.
	 * 
	 * @throws Exception
	 */
	@Test
	public void searchWithSPMWorkersUsingSplittersFixedThreads() throws Exception {
		Searcher s = new Searcher();
		IWorkSplitter<SearchParameters> oSP = Splitters.splitByChunkSize(10, new Splitters.ISimpleSplitter<SearchParameters>() {
			@Override
			public int count(SearchParameters pWork) {
				return pWork.getTo() - pWork.getFrom();
			}
			@Override
			public SearchParameters newSplit(int pFrom, int pTo, SearchParameters pWork) {
				return new SearchParameters(pWork.getFrom() + pFrom - 1, Math.min(pTo + pWork.getFrom() - 1, pWork.getTo()), pWork.getFilters());
			}
		});
		SearchMerger oSM = new SearchMerger();
		SearchWorker oSW = new SearchWorker(s);
		
		for (int i = 1; i <= 6; i++) {
			ExecutorService oES = Executors.newFixedThreadPool(i);
			LOGGER.info("SPM with " + i + " threads...");
			assertWorks(oES, oSP, oSM, oSW, new SearchParameters(13, 65, "something here"));
		}
	}
	
	private void assertWorks(ExecutorService pExecutorService, IWorkSplitter<SearchParameters> pSplitter, SearchMerger pMerger, SearchWorker pWorker, SearchParameters pSearchParameters) throws Exception {
		SimpleSPMWorker<SearchParameters, SearchResults> oSPMWorker = new SimpleSPMWorker<SearchParameters, SearchResults>(pExecutorService, pSplitter, pMerger, pWorker);
		long oStart = System.currentTimeMillis();
		SearchResults oResults = oSPMWorker.perform(pSearchParameters);
		long oEnd =  System.currentTimeMillis();
		int oExpectedTotalResults = pSearchParameters.getTo() - pSearchParameters.getFrom() + 1; 
		LOGGER.info("Execution time for " + oResults.getResults().size() + " results: " + (oEnd - oStart) + "ms.");
		assertNotNull(oResults);
		assertNotNull(oResults.getResults());
		assertEquals(oExpectedTotalResults, oResults.getResults().size());
		
		
	}
}
