/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author luc
 *
 */
public abstract class AbstractSPMWorker<W extends IWork, R extends IResult> implements IWorker<W, R> {

	private IWorkSplitter<W> mSplitter;
	private IResultMerger<R> mMerger;
	private ExecutorService mExecutorService;
	
	public AbstractSPMWorker(ExecutorService pExecutorService, IWorkSplitter<W> pSplitter, IResultMerger<R> pMerger) {
		mSplitter = pSplitter;
		mMerger = pMerger;
		mExecutorService = pExecutorService;
	}

	@Override
	public R perform(W pWork) throws Exception {
		Collection<W> oWork = mSplitter.split(pWork);
		Collection<R> oResults = perform(oWork);
		return mMerger.merge(oResults);
	}
	
	protected abstract Callable<R> createCall(W pWork);

	protected Collection<R> perform(Collection<W> pWork) throws Exception {
		List<R> oResults = new ArrayList<R>();
		List<Callable<R>> oTasks = new ArrayList<Callable<R>>();
		for (final W w : pWork) {
			oTasks.add(createCall(w));
		}
		List<Future<R>> oFutures = mExecutorService.invokeAll(oTasks);
		for (Future<R> f : oFutures) oResults.add(f.get());
		return oResults;
	}
}
