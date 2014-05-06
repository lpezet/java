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

import java.util.concurrent.Callable;

import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;

/**
 * @author luc
 *
 */
public class CircuitBreakerWorker<W, R> implements IWorker<W, R> {

	private IWorker<W, R> mImpl;
	private ICircuitBreakerStrategy mCircuitBreakerStrategy;
	
	public CircuitBreakerWorker(IWorker<W, R> pImpl, ICircuitBreakerStrategy pCircuitBreakerStrategy) {
		mImpl = pImpl;
		mCircuitBreakerStrategy = pCircuitBreakerStrategy;
	}
	
	@Override
	public R perform(final W pWork) throws Exception {
		return mCircuitBreakerStrategy.executeAndTrip(new Callable<R>() {
			@Override
			public R call() throws Exception {
				return mImpl.perform(pWork);
			}
		});
	}
}
