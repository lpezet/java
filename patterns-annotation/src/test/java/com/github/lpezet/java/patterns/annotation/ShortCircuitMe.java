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
public class ShortCircuitMe implements IShortCircuitMe {

	private int mTimesExecuted = 0;
	private IExceptionThrower<IShortCircuitMe> mExceptionThrower = new IExceptionThrower<IShortCircuitMe>() {
		
		@Override
		public boolean shouldThrow(IShortCircuitMe pContext) {
			return pContext.timesExecuted() == 1;
		}
	};
	
	public void setExceptionThrower(IExceptionThrower<IShortCircuitMe> pExceptionThrower) {
		mExceptionThrower = pExceptionThrower;
	}
	
	@Override
	@ShortCircuit(tripers=ArrayIndexOutOfBoundsException.class, exceptionsToTrip=1)
	public boolean doSomething() {
		mTimesExecuted++;
		System.out.println("## I'm doing something...");
		if (mExceptionThrower.shouldThrow(this))
			throw new ArrayIndexOutOfBoundsException("I did something wrong !");
		System.out.println("## I've done something!");
		return true;
	}

	public int timesExecuted() {
		return mTimesExecuted;
	}
}
