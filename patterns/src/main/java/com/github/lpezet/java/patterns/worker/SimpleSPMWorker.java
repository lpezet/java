/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Simple Split, Process, Merge worker using a single implementation.
 * 
 * NB: Case where pImpl is thread-safe and it is fine to channel all the parallel work through it. 
 * Alternative is using a Factory to create a worker for each work to be performed.
 * 
 * @author luc
 *
 */
public class SimpleSPMWorker<W extends IWork, R extends IResult> extends AbstractSPMWorker<W, R> {
	
	private IWorker<W, R> mImpl;
	
	public SimpleSPMWorker(ExecutorService pExecutorService, IWorkSplitter<W> pSplitter, IResultMerger<R> pMerger, IWorker<W, R> pImpl) {
		super(pExecutorService, pSplitter, pMerger);
		mImpl = pImpl;
	}
	
	@Override
	protected Callable<R> createCall(final W pWork) {
		return new Callable<R>() {
			@Override
			public R call() throws Exception {
				return mImpl.perform(pWork);
			}
		};
	}

}
