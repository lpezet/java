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
package com.github.lpezet.java.patterns.circuitbreaker;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luc Pezet
 *
 */
public class BaseCircuitBreakerCondition implements ICircuitBreakerCondition {

private Logger mLogger = LoggerFactory.getLogger(this.getClass());
	
	private Class<? extends Exception>[] mTripers = new Class[] { Exception.class };
	private int mExceptionsToTrip = 1;
	private AtomicInteger mCurrentErrors = new AtomicInteger(0);
	
	public BaseCircuitBreakerCondition() {
	}
	
	public BaseCircuitBreakerCondition(Class<? extends Exception>... pTripers) {
		mTripers = pTripers;
	}
	
	public BaseCircuitBreakerCondition(int pExceptionsToTrip) {
		mExceptionsToTrip = pExceptionsToTrip;
	}
	
	public BaseCircuitBreakerCondition(int pExceptionsToTrip, Class<? extends Exception>... pTripers) {
		mTripers = pTripers;
		mExceptionsToTrip = pExceptionsToTrip;
	}
	
	public void setExceptionsToTrip(int pExceptionsToTrip) {
		mExceptionsToTrip = pExceptionsToTrip;
	}
	
	public void setTripers(Class<? extends Exception>[] pTripers) {
		mTripers = pTripers;
	}
	
	public int getExceptionsToTrip() {
		return mExceptionsToTrip;
	}
	
	public Class<? extends Exception>[] getTripers() {
		return mTripers;
	}
	
	@Override
	public boolean shouldTrip(Throwable e) {
		boolean oTrip = false;
		synchronized (mCurrentErrors) {
			if (isTriper(e.getClass()) && mCurrentErrors.incrementAndGet() >= mExceptionsToTrip) {
				mCurrentErrors.set(0);
				oTrip = true;
			}
		}
		if (mLogger.isTraceEnabled()) mLogger.trace("Exception = [" + e + "], # errors = [" + mCurrentErrors.get() + "]. Should trip ? " + oTrip);
		return oTrip;
	}

	private boolean isTriper(Class<? extends Throwable> pClass) {
		for (Class<? extends Exception> c : mTripers) {
			if (c.isAssignableFrom(pClass)) return true;
		}
		return false;
	}
}
