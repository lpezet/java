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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author luc
 *
 */
public class InMemoryCircuitBreaker implements ICircuitBreaker {

	private volatile CircuitBreakerState mState = CircuitBreakerState.Closed;
	private Throwable mLastException;
	private DateTime mLastStateChanged;
	
	@Override
	public CircuitBreakerState getState() {
		return mState;
	}

	@Override
	public Throwable getLastException() {
		return mLastException;
	}

	@Override
	public DateTime getLastStateChangedDateUTC() {
		return mLastStateChanged;
	}

	@Override
	public void trip(Throwable pException) {
		mLastException = pException;
		mLastStateChanged = DateTime.now(DateTimeZone.UTC);
		mState = CircuitBreakerState.Open;
	}

	@Override
	public void reset() {
		mState = CircuitBreakerState.Closed;
	}

	@Override
	public void halfOpen() {
		mState = CircuitBreakerState.HalfOpen;
	}

	@Override
	public boolean isClosed() {
		return mState == CircuitBreakerState.Closed;
	}

}
