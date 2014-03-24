/**
 * 
 */
package com.github.lpezet.java.patterns.command;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author luc
 *
 */
public class BasicResultHolder<T> implements IAsyncResult<T> {

	private Object mLock = new Object();
	private Callback<T> mCallback;
	private boolean mCalledback = false;
	private Future<T> mFutureResult;
	
	/**
	 * Executes command with custom callable.
	 */
	public BasicResultHolder(ExecutorService pExecutorService, final ICommand<T> pImpl) {
		mFutureResult = pExecutorService.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				T oResults = pImpl.execute();
				synchronized (mLock) {
					if (mCallback != null && !mCalledback) {
						mCalledback = true;
						mCallback.callback(oResults);
					}
				}
				return oResults;		
			}
		});
	}

	@Override
	public void setCallback(Callback<T> pCallback) throws Exception {
		synchronized (mLock) {
			mCallback = pCallback;
			if (mFutureResult.isDone() && !mCalledback) {
				mCalledback = true;
				pCallback.callback(mFutureResult.get());
			}
		}
	}

	@Override
	public T get() throws Exception {
		return mFutureResult.get();
	}
	
	@Override
	public T get(long pTimeout, TimeUnit pTimeUnit) throws Exception {
		return mFutureResult.get(pTimeout, pTimeUnit);
	}

}
