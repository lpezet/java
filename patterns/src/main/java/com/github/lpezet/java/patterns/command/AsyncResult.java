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
public class AsyncResult<T> implements IAsyncResult<T> {

	private static class ResultOrException<T> {
		private T mResult;
		private Exception mException;
		
		public ResultOrException(T pResult) {
			mResult = pResult;
		}
		public ResultOrException(Exception pException) {
			mException = pException;
		}
		public Exception getException() {
			return mException;
		}
		public T getResult() {
			return mResult;
		}
	}
	private Object mLock = new Object();
	private Callback<T> mCallback;
	private boolean mCalledback = false;
	private Future<ResultOrException<T>> mFutureResult;
	
	/**
	 * Executes command with custom callable.
	 */
	public AsyncResult(ExecutorService pExecutorService, final ICommand<T> pImpl) {
		mFutureResult = pExecutorService.submit(new Callable<ResultOrException<T>>() {
			@Override
			public ResultOrException<T> call() throws Exception {
				Exception oError = null;
				T oResults = null;
				try {
					oResults = pImpl.execute();
				} catch (Exception e) {
					oError = e;
				}
				synchronized (mLock) {
					if (mCallback != null && !mCalledback) {
						mCalledback = true;
						if (oError == null) mCallback.onResult(oResults);
						else mCallback.onException(oError);
					}
				}
				return oError == null ? new ResultOrException<T>(oResults) : new ResultOrException<T>(oError);		
			}
		});
	}

	@Override
	public void setCallback(Callback<T> pCallback) throws Exception {
		synchronized (mLock) {
			mCallback = pCallback;
			if (mFutureResult.isDone() && !mCalledback) {
				mCalledback = true;
				ResultOrException<T> oROE = mFutureResult.get();
				if (oROE.getException() != null) pCallback.onException(oROE.getException());
				else pCallback.onResult(oROE.getResult());
			}
		}
	}

	@Override
	public T get() throws Exception {
		ResultOrException<T> oROE = mFutureResult.get();
		if (oROE.getException() != null) throw oROE.getException();
		else return oROE.getResult();
	}
	
	@Override
	public T get(long pTimeout, TimeUnit pTimeUnit) throws Exception {
		ResultOrException<T> oROE = mFutureResult.get(pTimeout, pTimeUnit);
		if (oROE.getException() != null) throw oROE.getException();
		else return oROE.getResult();
	}

}
