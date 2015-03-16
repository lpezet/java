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
package com.github.lpezet.java.patterns.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author luc
 *
 */
public abstract class AbstractSPMWorker<W, R> implements IWorker<W, R> {

	private IWorkSplitter<W> mSplitter;
	private IResultMerger<R> mMerger;
	private ExecutorService mExecutorService;
	
	public AbstractSPMWorker(ExecutorService pExecutorService, IWorkSplitter<W> pSplitter, IResultMerger<R> pMerger) {
		mSplitter = pSplitter;
		mMerger = pMerger;
		mExecutorService = pExecutorService;
	}

	@Override
	public R perform(W pWork) throws Exception {
		Collection<W> oWork = mSplitter.split(pWork);
		Collection<R> oResults = perform(oWork);
		return mMerger.merge(oResults);
	}
	
	protected abstract Callable<R> createCall(W pWork);

	protected Collection<R> perform(Collection<W> pWork) throws Exception {
		List<R> oResults = new ArrayList<R>();
		List<Callable<R>> oTasks = new ArrayList<Callable<R>>();
		for (final W w : pWork) {
			oTasks.add(createCall(w));
		}
		List<Future<R>> oFutures = mExecutorService.invokeAll(oTasks);
		for (Future<R> f : oFutures) oResults.add(f.get());
		return oResults;
	}
}
