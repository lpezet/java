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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luc
 *
 */
public class BaseCircuitBreakerStrategy implements ICircuitBreakerStrategy {
	
	private Logger mLogger = LoggerFactory.getLogger(BaseCircuitBreakerStrategy.class);
	
	private ICircuitBreaker mCircuitBreaker;
	private ICircuitBreakerHandler mCircuitBreakerHandler;
	private ICircuitBreakerCondition mCircuitBreakerCondition;
	
	public BaseCircuitBreakerStrategy(ICircuitBreaker pCircuitBreaker, ICircuitBreakerHandler pHandler) {
		mCircuitBreaker = pCircuitBreaker;
		mCircuitBreakerHandler = pHandler;
		mCircuitBreakerCondition = new BaseCircuitBreakerCondition(Exception.class, 1);
	}
	
	public BaseCircuitBreakerStrategy(ICircuitBreaker pCircuitBreaker, ICircuitBreakerHandler pHandler, ICircuitBreakerCondition pCircuitBreakerCondition) {
		mCircuitBreaker = pCircuitBreaker;
		mCircuitBreakerHandler = pHandler;
		mCircuitBreakerCondition = pCircuitBreakerCondition;
	}
	
	@Override
	public <T> T executeAndTrip(Callable<T> pCallable) throws Exception {
		if (!mCircuitBreaker.isClosed()) {
			if (mLogger.isTraceEnabled()) mLogger.trace("Circuit breaker " + mCircuitBreaker.getState() + ". Handling open state...");
			return mCircuitBreakerHandler.handleOpen(mCircuitBreaker, pCallable);
		} else {
			if (mLogger.isTraceEnabled()) mLogger.trace("Circuit breaker " + (mCircuitBreaker.getState()) + ". Using callable.");
			try {
				return pCallable.call();
			} catch (Exception e) {
				if (mCircuitBreakerCondition.shouldTrip(e)) {
					if (mLogger.isTraceEnabled()) mLogger.trace("Got exception: {}. Tripping circuit breaker and re-throwing exception.", e.getMessage());
					mCircuitBreaker.trip(e);
				}
				mLogger.error("Got exception. Re-throwing...", e);
				throw e;
			}
		}
	}

}
