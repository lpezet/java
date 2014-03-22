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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.github.lpezet.java.patterns.command.ICommand;

/**
 * @author luc
 * 
 * Reference: http://msdn.microsoft.com/en-us/library/dn589784.aspx
 *
 */
public class SingleTryCircuitBreakerStrategy implements ICircuitBreakerHandler {
	
	private Lock mLock = new ReentrantLock();
	private long mOpenToHalfOpenWaitTimeInMillis = 500;

	public long getOpenToHalfOpenWaitTimeInMillis() {
		return mOpenToHalfOpenWaitTimeInMillis;
	}
	
	public void setOpenToHalfOpenWaitTimeInMillis(long pValue) {
		mOpenToHalfOpenWaitTimeInMillis = pValue;
	}
	
	@Override
	public <T> T handleOpen(ICircuitBreaker pCircuitBreaker, ICommand<T> pCommand) throws Exception {
		Duration oDuration = new Duration(pCircuitBreaker.getLastStateChangedDateUTC(), DateTime.now(DateTimeZone.UTC));
		if (oDuration.getMillis() < mOpenToHalfOpenWaitTimeInMillis) {
			// The Open timeout has not yet expired. Throw a CircuitBreakerOpen exception to
			// inform the caller that the caller that the call was not actually attempted, 
			// and return the most recent exception received.
			throw new CircuitBreakerOpenException(pCircuitBreaker.getLastException());
		}
		// Limit the number of threads to be executed when the breaker is HalfOpen.
		boolean oLockAcquired = mLock.tryLock();
		if (!oLockAcquired) throw new CircuitBreakerOpenException();
		pCircuitBreaker.halfOpen();
		// Lock acquired, we'll try the operation again
		try {
			T oResult = pCommand.execute();
			// Operation succeeded so we reset the state to closed
			pCircuitBreaker.reset();
			return oResult;
		} catch (Exception e) {
			// If there is still an exception, trip the breaker again immediately.
			pCircuitBreaker.trip(e);
			// Throw the exception so that the caller knows which exception occurred.
            throw e;
		} finally {
			mLock.unlock();
		}
	}

}
