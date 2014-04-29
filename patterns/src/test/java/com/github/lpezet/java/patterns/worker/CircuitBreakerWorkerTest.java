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
package com.github.lpezet.java.patterns.worker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.github.lpezet.java.patterns.circuitbreaker.BaseCircuitBreakerStrategy;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreaker;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerHandler;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;
import com.github.lpezet.java.patterns.circuitbreaker.InMemoryCircuitBreaker;

/**
 * @author luc
 * 
 */
public class CircuitBreakerWorkerTest {
	
	private ICircuitBreaker mCircuitBreaker;
	private ICircuitBreakerHandler mCircuitBreakerHandler;
	private ICircuitBreakerStrategy mCircuitBreakerStrategy;
	
	@Before
	public void setup() {
		mCircuitBreaker = new InMemoryCircuitBreaker();
		mCircuitBreakerHandler = new ICircuitBreakerHandler() {
			@Override
			public <T> T handleOpen(ICircuitBreaker pCircuitBreaker, Callable<T> pCallable) throws Exception {
				try {
					T oResult = pCallable.call();
					pCircuitBreaker.reset();
					return oResult;
				} finally {
				}
			}
		};
		mCircuitBreakerStrategy = new BaseCircuitBreakerStrategy(mCircuitBreaker, mCircuitBreakerHandler);
	}
	

	@Test
	public void noTrip() throws Exception {
		IWorker<Void, Void> oTestWorker = new IWorker<Void, Void>() {
			@Override
			public Void perform(Void pWork) throws Exception {
				return null;
			}
		};

		CircuitBreakerWorker<Void, Void> oWorker = new CircuitBreakerWorker<Void, Void>(oTestWorker, mCircuitBreakerStrategy);
		
		oWorker.perform(null);
		assertTrue(mCircuitBreaker.isClosed());
	}
	
	@Test
	public void tripThenReset() throws Exception {
		final AtomicInteger oExecutions = new AtomicInteger(0);
		IWorker<Void, Void> oTestWorker = new IWorker<Void, Void>() {
			@Override
			public Void perform(Void pWork) throws Exception {
				int oExecs = oExecutions.incrementAndGet();
				if (oExecs <= 2) throw new Exception("Throwing exception under 3 executions. This is for testing purposes.");
				return null;
			}
		};

		CircuitBreakerWorker<Void, Void> oWorker = new CircuitBreakerWorker<Void, Void>(oTestWorker, mCircuitBreakerStrategy);
		
		try {
			oWorker.perform(null);
		} catch (Throwable t) {}
		assertFalse(mCircuitBreaker.isClosed());
		try {
			oWorker.perform(null);
		} catch (Throwable t) {}
		assertFalse(mCircuitBreaker.isClosed());
		oWorker.perform(null);
		assertTrue(mCircuitBreaker.isClosed());
		
	}
}
