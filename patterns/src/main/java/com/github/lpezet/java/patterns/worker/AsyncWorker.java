/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.concurrent.ExecutorService;

/**
 * @author luc
 *
 */
public class AsyncWorker<W, R> implements IWorker<W, IAsyncResult<R>> {

	private ExecutorService mExecutorService;
	private IWorker<W, R> mImpl;
	
	public AsyncWorker(ExecutorService pExecutorService, IWorker<W, R> pImpl) {
		mExecutorService = pExecutorService;
		mImpl = pImpl;
	}
	
	@Override
	public IAsyncResult<R> perform(W pWork) throws Exception {
		return new AsyncResult<R>(mExecutorService, mImpl, pWork);
	}
}
