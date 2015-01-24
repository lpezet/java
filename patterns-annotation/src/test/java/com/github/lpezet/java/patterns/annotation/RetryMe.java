/**
 * 
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
			return pContext.timesExecuted() % 3 != 0;
		}
	};

	@Override
	public boolean doSomething() {
		mExecutions++;
		System.out.println("## I'm doing something...");
		if (mExceptionThrower.shouldThrow(this))
			throw new ArrayIndexOutOfBoundsException();
		System.out.println("## ...I've done something!");
		return true;
	}
	
	@Override
	public int timesExecuted() {
		return mExecutions;
	}
}
