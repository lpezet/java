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

/**
 * Simple Split, Process, Merge worker using a single implementation.
 * 
 * NB: Case where pImpl is thread-safe and it is fine to channel all the parallel work through it. 
 * Alternative is using a Factory to create a worker for each work to be performed.
 * 
 * @author luc
 *
 */
public class SimpleSPMWorker<W, R> extends AbstractSPMWorker<W, R> {
	
	private IWorker<W, R> mImpl;
	
	public SimpleSPMWorker(ExecutorService pExecutorService, IWorkSplitter<W> pSplitter, IResultMerger<R> pMerger, IWorker<W, R> pImpl) {
		super(pExecutorService, pSplitter, pMerger);
		mImpl = pImpl;
	}
	
	@Override
	protected Callable<R> createCall(final W pWork) {
		return new Callable<R>() {
			@Override
			public R call() throws Exception {
				return mImpl.perform(pWork);
			}
		};
	}

}
