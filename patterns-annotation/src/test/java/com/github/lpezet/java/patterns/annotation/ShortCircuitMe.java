/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

/**
 * @author Luc Pezet
 *
 */
public class ShortCircuitMe implements IShortCircuitMe {

	private int mTimesExecuted = 0;
	
	@Override
	@CircuitBreaker(triper=ArrayIndexOutOfBoundsException.class, exceptionsToTrip=1)
	public void doSomething() {
		mTimesExecuted++;
		System.out.println("## I'm doing something...");
		throwOnlyOnce();
		System.out.println("## I've done something!");
	}
	
	private void throwOnlyOnce() {
		if (mTimesExecuted == 1) 
			throw new ArrayIndexOutOfBoundsException("I did something wrong !");
	}

	public int timesExecuted() {
		return mTimesExecuted;
	}
}
