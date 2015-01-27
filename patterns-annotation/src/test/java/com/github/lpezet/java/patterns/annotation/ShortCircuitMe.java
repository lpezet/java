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
