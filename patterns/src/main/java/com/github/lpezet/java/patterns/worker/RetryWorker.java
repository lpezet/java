/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.concurrent.Callable;

import com.github.lpezet.java.patterns.retry.IRetryStrategy;

/**
 * @author luc
 *
 */
public class RetryWorker<W, R> implements IWorker<W, R> {

	private IWorker<W, R> mImpl;
	private IRetryStrategy mRetryStrategy;
	
	public RetryWorker(IWorker<W, R> pImpl, IRetryStrategy pRetryStrategy) {
		mImpl = pImpl;
		mRetryStrategy = pRetryStrategy;
	}
	
	@Override
	public R perform(final W pWork) throws Exception {
		return mRetryStrategy.executeAndRetry(new Callable<R>() {
			public R call() throws Exception {
				return mImpl.perform(pWork);
			};
		});
	}
}
