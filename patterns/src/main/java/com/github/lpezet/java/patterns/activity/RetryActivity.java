/**
 * 
 */
package com.github.lpezet.java.patterns.activity;

import java.util.concurrent.Callable;

import com.github.lpezet.java.patterns.retry.IRetryStrategy;

/**
 * @author luc
 *
 */
public class RetryActivity<T> implements IActivity<T> {

	private IActivity<T> mImpl;
	private IRetryStrategy mRetryStrategy;
	
	public RetryActivity(IActivity<T> pImpl, IRetryStrategy pRetryStrategy) {
		mImpl = pImpl;
		mRetryStrategy = pRetryStrategy;
	}
	
	@Override
	public T start(final IContext pContext) throws Exception {
		return mRetryStrategy.executeAndRetry(new Callable<T>() {
			public T call() throws Exception {
				return mImpl.start(pContext);
			};
		});
	}

}
