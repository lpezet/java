/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * @author luc
 *
 */
public class AsyncResult<R> implements IAsyncResult<R> {

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
	private Callback<R> mCallback;
	private boolean mCalledback = false;
	private Future<ResultOrException<R>> mFutureResult;
	
	/**
	 * Executes command with custom callable.
	 */
	public <T> AsyncResult(ExecutorService pExecutorService, final IWorker<T, R> pImpl, final T pWork) {
		mFutureResult = pExecutorService.submit(new Callable<ResultOrException<R>>() {
			@Override
			public ResultOrException<R> call() throws Exception {
				Exception oError = null;
				R oResults = null;
				try {
					oResults = pImpl.perform(pWork);
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
				return oError == null ? new ResultOrException<R>(oResults) : new ResultOrException<R>(oError);		
			}
		});
	}

	@Override
	public void setCallback(Callback<R> pCallback) throws Exception {
		synchronized (mLock) {
			mCallback = pCallback;
			if (mFutureResult.isDone() && !mCalledback) {
				mCalledback = true;
				ResultOrException<R> oROE = mFutureResult.get();
				if (oROE.getException() != null) pCallback.onException(oROE.getException());
				else pCallback.onResult(oROE.getResult());
			}
		}
	}

	@Override
	public R get() throws Exception {
		ResultOrException<R> oROE = mFutureResult.get();
		if (oROE.getException() != null) throw oROE.getException();
		else return oROE.getResult();
	}
	
	@Override
	public R get(long pTimeout, TimeUnit pTimeUnit) throws Exception {
		ResultOrException<R> oROE = mFutureResult.get(pTimeout, pTimeUnit);
		if (oROE.getException() != null) throw oROE.getException();
		else return oROE.getResult();
	}
}
