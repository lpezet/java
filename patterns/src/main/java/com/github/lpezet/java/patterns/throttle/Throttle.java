/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import java.util.concurrent.Callable;

/**
 * @author Luc Pezet
 *
 */
public abstract class Throttle<T> implements IThrottle<T> {
	
	private IThrottleStrategy mStrategy;
	
	public Throttle(IThrottleStrategy pStrategy) {
		mStrategy = pStrategy;
	}
	
	@Override
	public T throttle(Callable<T> pCallable) throws Exception {
		//TODO
		String oKey = pCallable.toString();
		
		return pCallable.call();
	}
	
	
}
