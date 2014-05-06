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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.lpezet.java.patterns.supervisor.Supervisor;

/**
 * @author luc
 *
 */
public class SupervisorWorker<W, R> implements IWorker<W, R> {

	private IWorker<W, R> mImpl;
	private Supervisor<R> mSupervisor;
	
	
	public SupervisorWorker(IWorker<W, R> pImpl, long pTimeout, TimeUnit pTimeoutUnit) {
		this(pImpl, Executors.newCachedThreadPool(), pTimeout, pTimeoutUnit);
	}
	
	public SupervisorWorker(IWorker<W, R> pImpl, ExecutorService pExecutorService, long pTimeout, TimeUnit pTimeoutUnit) {
		mSupervisor = new Supervisor<R>(pExecutorService, pTimeout, pTimeoutUnit);
		mImpl = pImpl;
	}
	
	@Override
	public R perform(final W pWork) throws Exception {
		return mSupervisor.supervise(new Callable<R>() {
			@Override
			public R call() throws Exception {
				return mImpl.perform(pWork);
			}
		});
	}
}
