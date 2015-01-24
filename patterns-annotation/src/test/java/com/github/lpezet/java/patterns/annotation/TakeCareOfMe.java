/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.util.concurrent.TimeUnit;

import com.github.lpezet.java.patterns.supervisor.TimeoutException;

/**
 * @author Luc Pezet
 *
 */
public class TakeCareOfMe implements ITakeCareOfMe {
	
	private volatile IBehavior mBehavior;

	@ShortCircuit(exceptionsToTrip=1)
	@Retry(exception=TimeoutException.class)
	@Supervise(timeout=100, timeunit=TimeUnit.MILLISECONDS)
	public boolean doSomething() throws Exception {
		mBehavior.behave();
		return true;
	}
	
	@Override
	public void setBehavior(IBehavior pBehavior) {
		mBehavior = pBehavior;
	}
}
