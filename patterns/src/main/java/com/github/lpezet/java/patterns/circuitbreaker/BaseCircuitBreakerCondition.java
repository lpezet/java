/**
 * 
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
