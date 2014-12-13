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
package com.github.lpezet.java.patterns.circuitbreaker;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luc
 * 
 * Reference: http://msdn.microsoft.com/en-us/library/dn589784.aspx
 *
 */
public class SingleTryCircuitBreakerStrategy implements ICircuitBreakerHandler {
	
	private Logger mLogger = LoggerFactory.getLogger(SingleTryCircuitBreakerStrategy.class);
	
	private Lock mLock = new ReentrantLock();
	private long mOpenToHalfOpenWaitTimeInMillis = 500;

	public long getOpenToHalfOpenWaitTimeInMillis() {
		return mOpenToHalfOpenWaitTimeInMillis;
	}
	
	public void setOpenToHalfOpenWaitTimeInMillis(long pValue) {
		mOpenToHalfOpenWaitTimeInMillis = pValue;
	}
	
	@Override
	public <T> T handleOpen(ICircuitBreaker pCircuitBreaker, Callable<T> pCallable) throws Exception {
		Duration oDuration = new Duration(pCircuitBreaker.getLastStateChangedDateUTC(), DateTime.now(DateTimeZone.UTC));
		if (oDuration.getMillis() < mOpenToHalfOpenWaitTimeInMillis) {
			if (mLogger.isTraceEnabled()) mLogger.trace("Open timeout not yet expired. Throwing CircuitBreakerOpenException.");
			// The Open timeout has not yet expired. Throw a CircuitBreakerOpen exception to
			// inform the caller that the caller that the call was not actually attempted, 
			// and return the most recent exception received.
			throw new CircuitBreakerOpenException(pCircuitBreaker.getLastException());
		}
		if (mLogger.isTraceEnabled()) mLogger.trace("Open timeout expired.");
		// Limit the number of threads to be executed when the breaker is HalfOpen.
		boolean oLockAcquired = mLock.tryLock();
		if (!oLockAcquired) throw new CircuitBreakerOpenException();
		if (mLogger.isTraceEnabled()) mLogger.trace("Setting circuit breaker to " + CircuitBreakerState.HalfOpen + "...");
		pCircuitBreaker.halfOpen();
		if (mLogger.isTraceEnabled()) mLogger.trace("Lock acquired: trying operation again to close circuit upon success...");
		// Lock acquired, we'll try the operation again
		try {
			T oResult = pCallable.call();
			if (mLogger.isTraceEnabled()) mLogger.trace("Success. Resetting circuit breaker.");
			// Operation succeeded so we reset the state to closed
			pCircuitBreaker.reset();
			return oResult;
		} catch (Exception e) {
			if (mLogger.isTraceEnabled()) mLogger.trace("Failed. Still getting exception from callable: {}. Tripping circuit breaker and re-throwing.", e.getMessage());
			// If there is still an exception, trip the breaker again immediately.
			pCircuitBreaker.trip(e);
			mLogger.error("Got exception. Re-throwing...", e);
			// Throw the exception so that the caller knows which exception occurred.
            throw e;
		} finally {
			mLock.unlock();
		}
	}

}
