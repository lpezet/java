/**
 * The MIT License
 * Copyright (c) 2014 Luc Pezet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
public class Supervisor<T> implements ISupervisor<T> {
	
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
		if (mLogger.isTraceEnabled()) mLogger.trace("supervise(" + pCallable + "), timeout=" + mTimeout + ", unit=" + mTimeoutUnit);
		final CountDownLatch oCountdown = new CountDownLatch(1);
		Future<T> f = mExecutorService.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				try {
					T oResult = pCallable.call();
					oCountdown.countDown();
					if (mLogger.isTraceEnabled()) mLogger.trace("Thread interrupted? " + Thread.interrupted() + ". Returning result...");
					return oResult;
				} catch (Exception e) {
					throw e;
				}
			}
		});
		try {
			boolean oTimedout = ! oCountdown.await(mTimeout, mTimeoutUnit);
			if (mLogger.isTraceEnabled()) mLogger.trace("Timedout={}, call done={}", new Object[] {oTimedout, f.isDone()});
			if (oTimedout && !f.isDone()) {
				cancel(f, pCallable);
				throw new TimeoutException();
			} else {
				if (mLogger.isTraceEnabled()) mLogger.trace("Returning result.");// + f.isDone());
				return f.get();
			}
		} catch (InterruptedException e) {
			if (mLogger.isInfoEnabled()) mLogger.info("Got interrupted. Will try to abort callable then re-throw exception.");
			cancel(f, pCallable);
			throw e;
		}
		
	}

	public void cancel(Future pFuture, Callable<T> pCallable) {
		if (mLogger.isTraceEnabled()) mLogger.trace("Cancelling task...");
		pFuture.cancel(true);
		if (mLogger.isTraceEnabled()) mLogger.trace("Task canceled.");
		
		if (!(pCallable instanceof IAbortable)) return;
		if (mLogger.isTraceEnabled()) mLogger.trace("Aborting callable...");
		IAbortable oAC = (IAbortable) pCallable;
		oAC.abort();
	}
	
	
}
