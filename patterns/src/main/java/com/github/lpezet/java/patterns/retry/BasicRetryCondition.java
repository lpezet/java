package com.github.lpezet.java.patterns.retry;

import java.util.concurrent.Callable;

public class BasicRetryCondition implements IRetryCondition {
	
	private int mMaxExecutions = 3;
	private Class mException;
	
	public BasicRetryCondition(Class pException, int pMaxExecutions) {
		mException = pException;
		mMaxExecutions = pMaxExecutions;
	}
	
	
	@Override
	public <T> boolean shouldRetry(Callable<T> pCallable, int pExecutions, Throwable pException) {
		return pExecutions <= mMaxExecutions
				&& (mException.isAssignableFrom(pException.getClass()) 
						|| (pException.getCause() != null && mException.isAssignableFrom(pException.getCause().getClass())));
	}
}