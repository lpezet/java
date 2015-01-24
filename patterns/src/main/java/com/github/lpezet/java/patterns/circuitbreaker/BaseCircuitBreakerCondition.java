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
	
	private Class<? extends Exception> mTriper = Exception.class;
	private int mExceptionsToTrip = 1;
	private AtomicInteger mCurrentErrors = new AtomicInteger(0);
	
	public BaseCircuitBreakerCondition() {
	}
	
	public BaseCircuitBreakerCondition(Class<? extends Exception> pTriper) {
		mTriper = pTriper;
	}

	public BaseCircuitBreakerCondition(int pExceptionsToTrip) {
		mExceptionsToTrip = pExceptionsToTrip;
	}
	
	public BaseCircuitBreakerCondition(Class<? extends Exception> pTriper, int pExceptionsToTrip) {
		mTriper = pTriper;
		mExceptionsToTrip = pExceptionsToTrip;
	}
	
	public void setExceptionsToTrip(int pExceptionsToTrip) {
		mExceptionsToTrip = pExceptionsToTrip;
	}
	
	public void setTriper(Class<? extends Exception> pTriper) {
		mTriper = pTriper;
	}
	
	public int getExceptionsToTrip() {
		return mExceptionsToTrip;
	}
	
	public Class<? extends Exception> getTriper() {
		return mTriper;
	}
	
	@Override
	public boolean shouldTrip(Throwable e) {
		boolean oTrip = false;
		synchronized (mCurrentErrors) {
			if (mTriper.isAssignableFrom(e.getClass()) && mCurrentErrors.incrementAndGet() >= mExceptionsToTrip) {
				mCurrentErrors.set(0);
				oTrip = true;
			}
		}
		if (mLogger.isTraceEnabled()) mLogger.trace("Exception = [" + e + "], # errors = [" + mCurrentErrors.get() + "]. Should trip ? " + oTrip);
		return oTrip;
	}
}
