/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.util.concurrent.TimeUnit;

/**
 * @author Luc Pezet
 *
 */
public class SuperviseMe implements ISuperviseMe {

	private int mExecutions = 0;
	private volatile long mTimeToWaitInMillis = 100;
	
	@Supervise(threads=1, timeout=200, timeunit=TimeUnit.MILLISECONDS)
	@Override
	public boolean doSomething() {
		mExecutions++;
		try {
			Thread.sleep(mTimeToWaitInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public int timesExecuted() {
		return mExecutions;
	}
	
	@Override
	public void timeToWaitInMillis(long pTime) {
		mTimeToWaitInMillis = pTime;
	}
	
}
