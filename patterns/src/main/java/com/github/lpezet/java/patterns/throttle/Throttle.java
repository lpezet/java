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
package com.github.lpezet.java.patterns.throttle;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luc Pezet
 *
 */
public class Throttle<T> implements IThrottle<T> {
	
	protected Logger mLogger = LoggerFactory.getLogger( this.getClass() );
	
	protected IThrottleStrategy mStrategy;
	private boolean mRethrow = false;
	
	public Throttle(IThrottleStrategy pStrategy) {
		mStrategy = pStrategy;
	}
	
	@Override
	public T throttleWithException(Callable<T> pCallable) throws Exception {
		while( mStrategy.isThrottled() ) {
			//System.out.println("[" + Thread.currentThread().getName() + "] Sleeping for " + mStrategy.getWaitTime() + "ms...");
			Thread.sleep(mStrategy.getWaitTime() + 1);
		}
		return pCallable.call();
	}
	
	@Override
	public T throttle(Callable<T> pCallable) {
		try {
			return throttleWithException(pCallable);
		} catch (Exception e) {
			mLogger.error("Unexpected error throttling callable.", e);
			if (mRethrow) {
				throw new RuntimeException( e.getCause() );
			} else {
				return null;
			}
		}
	}
	
	public void setRethrow(boolean pRethrow) {
		mRethrow = pRethrow;
	}
	
	public boolean isRethrow() {
		return mRethrow;
	}
	
}
