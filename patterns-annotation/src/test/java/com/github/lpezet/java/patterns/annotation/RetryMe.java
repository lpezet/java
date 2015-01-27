/**
 * The MIT License
 * Copyright (c) 2015 Luc Pezet
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
package com.github.lpezet.java.patterns.annotation;

/**
 * @author Luc Pezet
 *
 */
public class RetryMe implements IRetryMe {
	
	private int mExecutions = 0;
	private IExceptionThrower<IRetryMe> mExceptionThrower = new IExceptionThrower<IRetryMe>() {
		
		@Override
		public boolean shouldThrow(IRetryMe pContext) {
			return pContext.timesExecuted() % 3 == 0;
		}
	};

	@Retry(exceptions=ArrayIndexOutOfBoundsException.class, maxExecutions=1)
	@Override
	public boolean doSomething() {
		mExecutions++;
		//System.out.println("## I'm doing something..." + mExecutions);
		if (mExceptionThrower.shouldThrow(this))
			throw new ArrayIndexOutOfBoundsException();
		//System.out.println("## ...I've done something!");
		return true;
	}
	
	@Override
	public int timesExecuted() {
		return mExecutions;
	}
}
