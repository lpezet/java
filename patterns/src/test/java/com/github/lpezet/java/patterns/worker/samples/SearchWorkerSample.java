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
	 * Wrapping domain parameter into IWork
	 */
	private static class SearchWork {
		private SearchParameters mParameters;
		public SearchWork(SearchParameters pParameters) {
			mParameters = pParameters;
		}
		public SearchParameters getParameters() {
			return mParameters;
		}
	}
	
	/**
	 * Wrapping domain result into IResult
	 */
	private static class SearchResult {
		private SearchResults mResults;
		public SearchResult(SearchResults pResults) {
			mResults = pResults;
		}
		public SearchResults getResults() {
			return mResults;
		}
	}
	
	/**
	 * Merger of domain results
	 */
	private static class SearchMerger implements IResultMerger<SearchResult> {
		@Override
		public SearchResult merge(Collection<SearchResult> pSource) {
			List<Integer> oResults = new ArrayList<Integer>();
			for (SearchResult r : pSource) {
				oResults.addAll(r.getResults().getResults());
			}
			return new SearchResult(new SearchResults(oResults));
		}
	}
	
	/**
	 * Splitter of domain parameter/work.
	 */
	private static class SearchSplitter implements IWorkSplitter<SearchWork> {
		
		private int mChunkSize = 10;
		
		@Override
		public Collection<SearchWork> split(SearchWork pWork) {
			List<SearchWork> oResult = new ArrayList<SearchWork>();
			int oAmountOfWork = count(pWork); 
			if (oAmountOfWork < mChunkSize) {
				LOGGER.debug("Not splitting work: too small.");
				oResult.add(pWork);
			} else {
				int n = (int) Math.ceil( oAmountOfWork / (double) mChunkSize );
				LOGGER.debug("Dividing up work into " + n + " chunks.");
				int oTo, oFrom;
				for (int i = 0; i < n; i++) {
					oFrom = pWork.getParameters().getFrom() + i * mChunkSize;
					oTo = Math.min(pWork.getParameters().getTo(), oFrom + mChunkSize - 1);
					oResult.add(new SearchWork(new SearchParameters(oFrom, oTo, pWork.getParameters().getFilters())));
				}
			}
			return oResult;
		}
		private int count(SearchWork pWork) {
			return pWork.getParameters().getTo() - pWork.getParameters().getFrom();
		}
	}
	
	/**
	 * Wrapper on domain searcher.
	 */
	private static class SearchWorker implements IWorker<SearchWork, SearchResult> {
		
		private Searcher mSearcher;
		public SearchWorker(Searcher pSearcher) {
			mSearcher = pSearcher;
		}
		
		@Override
		public SearchResult perform(SearchWork pWork) throws Exception {
			return new SearchResult( mSearcher.search(pWork.getParameters()) );
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
	 * Using ne Search Worker to run a search with domai classes. Here the work will be split and the results merged. And this is without changing how the search gets called.
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
		
		Splitters.ISimpleSplitter<SearchWork> oSplitter = new Splitters.ISimpleSplitter<SearchWork>() {
			@Override
			public int count(SearchWork pWork) {
				return pWork.getParameters().getTo() - pWork.getParameters().getFrom();
			}
			@Override
			public SearchWork newSplit(int pFrom, int pTo, SearchWork pWork) {
				return new SearchWork(new SearchParameters(pWork.getParameters().getFrom() + pFrom - 1, Math.min(pTo + pWork.getParameters().getFrom() - 1, pWork.getParameters().getTo()), pWork.getParameters().getFilters()));
			}
		};
		
		IWorkSplitter<SearchWork> oSP = Splitters.splitByChunkSize(10, oSplitter);
		SearchMerger oSM = new SearchMerger();
		SearchWorker oSW = new SearchWorker(s);
		
		LOGGER.info("Splitting by chunk size...");
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
		IWorkSplitter<SearchWork> oSP = Splitters.splitByChunkSize(10, new Splitters.ISimpleSplitter<SearchWork>() {
			@Override
			public int count(SearchWork pWork) {
				return pWork.getParameters().getTo() - pWork.getParameters().getFrom();
			}
			@Override
			public SearchWork newSplit(int pFrom, int pTo, SearchWork pWork) {
				return new SearchWork(new SearchParameters(pWork.getParameters().getFrom() + pFrom - 1, Math.min(pTo + pWork.getParameters().getFrom() - 1, pWork.getParameters().getTo()), pWork.getParameters().getFilters()));
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
	
	private void assertWorks(ExecutorService pExecutorService, IWorkSplitter<SearchWork> pSplitter, SearchMerger pMerger, SearchWorker pWorker, SearchParameters pSearchParameters) throws Exception {
		SimpleSPMWorker<SearchWork, SearchResult> oSPMWorker = new SimpleSPMWorker<SearchWork, SearchResult>(pExecutorService, pSplitter, pMerger, pWorker);
		SearchWork oWork = new SearchWork(pSearchParameters);
		long oStart = System.currentTimeMillis();
		SearchResult oResults = oSPMWorker.perform(oWork);
		long oEnd =  System.currentTimeMillis();
		int oExpectedTotalResults = pSearchParameters.getTo() - pSearchParameters.getFrom() + 1; 
		LOGGER.info("Execution time for " + oResults.getResults().getResults().size() + " results: " + (oEnd - oStart) + "ms.");
		assertNotNull(oResults);
		assertNotNull(oResults.getResults());
		assertNotNull(oResults.getResults().getResults());
		assertEquals(oExpectedTotalResults, oResults.getResults().getResults().size());
		
		
	}
}
