/**
 * 
 */
package com.github.lpezet.java.patterns.supervisor;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author luc
 *
 */
public class Supervisor<T> {
	
	private Logger mLogger = LoggerFactory.getLogger(Supervisor.class);

	private ExecutorService mExecutorService;
	private long mTimeout;
	private TimeUnit mTimeoutUnit;
	
	public Supervisor(ExecutorService pExecutorService, long pTimeout, TimeUnit pTimeoutUnit) {
		mTimeout = pTimeout;
		mTimeoutUnit = pTimeoutUnit;
		mExecutorService = pExecutorService;
	}
	
	public T supervise(final Callable<T> pCallable) throws Exception {
		final CountDownLatch oCountdown = new CountDownLatch(1);
		Future<T> f = mExecutorService.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				T oResult = pCallable.call();
				oCountdown.countDown();
				return oResult;
			}
		});
		try {
			boolean oTimedout = ! oCountdown.await(mTimeout, mTimeoutUnit);
			if (mLogger.isTraceEnabled()) mLogger.trace("Timedout={}, call done={}", new Object[] {oTimedout, f.isDone()});
			if (oTimedout && !f.isDone()) {
				abort(pCallable);
				throw new TimeoutException();
			} else {
				if (mLogger.isTraceEnabled()) mLogger.trace("Returning result.");
				return f.get();
			}
		} catch (InterruptedException e) {
			if (mLogger.isInfoEnabled()) mLogger.info("Got interrupted. Will try to abort callable then re-throw exception.");
			abort(pCallable);
			throw e;
		}
		
	}

	public void abort(Callable<T> pCallable) {
		if (!(pCallable instanceof IAbortable)) return;
		if (mLogger.isTraceEnabled()) mLogger.trace("Aborting callable...");
		IAbortable oAC = (IAbortable) pCallable;
		oAC.abort();
	}
	
	
}
