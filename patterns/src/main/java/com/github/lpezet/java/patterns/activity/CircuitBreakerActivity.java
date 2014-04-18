/**
 * 
 */
package com.github.lpezet.java.patterns.activity;

import java.util.concurrent.Callable;

import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;

/**
 * @author luc
 *
 */
public class CircuitBreakerActivity<T> implements IActivity<T> {

	private IActivity<T> mImpl;
	private ICircuitBreakerStrategy mCircuitBreakerStrategy;
	
	public CircuitBreakerActivity(IActivity<T> pImpl, ICircuitBreakerStrategy pCircuitBreakerStrategy) {
		mImpl = pImpl;
		mCircuitBreakerStrategy = pCircuitBreakerStrategy;
	}
	
	@Override
	public T start(final IContext pContext) throws Exception {
		return mCircuitBreakerStrategy.executeAndTrip(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return mImpl.start(pContext);
			}
		});
	}
}
