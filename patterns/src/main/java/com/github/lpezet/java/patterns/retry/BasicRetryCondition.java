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
	private Class<? extends Exception>[] mExceptions = new Class[] { Exception.class };
	
	public BasicRetryCondition() {
	}
	
	public BasicRetryCondition(int pMaxExecutions, Class<? extends Exception>... pExceptions) {
		mExceptions = pExceptions;
		mMaxExecutions = pMaxExecutions;
	}
	
	public void setExceptions(Class<? extends Exception>... pExceptions) {
		mExceptions = pExceptions;
	}
	
	public void setMaxExecutions(int pMaxExecutions) {
		mMaxExecutions = pMaxExecutions;
	}
	
	public Class<? extends Exception>[] getExceptions() {
		return mExceptions;
	}
	
	public int getMaxExecutions() {
		return mMaxExecutions;
	}
	
	@Override
	public <T> boolean shouldRetry(Callable<T> pCallable, int pExecutions, Throwable pException) {
		return
				pExecutions <= mMaxExecutions && 
				(
						valid(pException.getClass()) 
						|| 
						(pException.getCause() != null && valid(pException.getCause().getClass()))
				);
	}

	private boolean valid(Class<? extends Throwable> pClass) {
		for (Class<? extends Exception> c : mExceptions) {
			if (c.isAssignableFrom(pClass)) return true;
		}
		return false;
	}
}