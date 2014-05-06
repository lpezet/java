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
package com.github.lpezet.java.patterns.retry;

import java.util.concurrent.Callable;

public class BasicRetryCondition implements IRetryCondition {
	
	private int mMaxExecutions = 3;
	private Class mException;
	
	public BasicRetryCondition(Class pException, int pMaxExecutions) {
		mException = pException;
		mMaxExecutions = pMaxExecutions;
	}
	
	
	@Override
	public <T> boolean shouldRetry(Callable<T> pCallable, int pExecutions, Throwable pException) {
		return pExecutions <= mMaxExecutions
				&& (mException.isAssignableFrom(pException.getClass()) 
						|| (pException.getCause() != null && mException.isAssignableFrom(pException.getCause().getClass())));
	}
}