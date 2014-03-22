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
package com.github.lpezet.java.patterns.command;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author luc
 *
 */
public class AsyncCommand<T> implements IAsyncCommand<T> {
	
	private ExecutorService mExecutorService;
	private ICommand<T> mImpl;
	
	public AsyncCommand(ExecutorService pExecutorService, ICommand<T> pImpl) {
		mExecutorService = pExecutorService;
		mImpl = pImpl;
	}

	@Override
	public Future<T> execute(final Callback<T> pCallback) {
		if (pCallback != null) {
			return mExecutorService.submit(new Callable<T>() {
					@Override
					public T call() throws Exception {
						T oResult = doExecute();
						pCallback.callback(oResult);
						return oResult;
					}
			});
		} else {
			return mExecutorService.submit(new Callable<T>() {
				@Override
				public T call() throws Exception {
					return doExecute();
				}
			});
		}
	}
	
	public void $doit() {
		
	}
	
	@Override
	public Future<T> execute() {
		return execute(null);
	}

	protected T doExecute() throws Exception {
		System.out.println(Thread.currentThread().getName() + ": executing impl...");
		return mImpl.execute();
	}

}
