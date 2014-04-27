/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.concurrent.Callable;

import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;

/**
 * @author luc
 *
 */
public class CircuitBreakerWorker<W extends IWork, R extends IResult> implements IWorker<W, R> {

	private IWorker<W, R> mImpl;
	private ICircuitBreakerStrategy mCircuitBreakerStrategy;
	
	public CircuitBreakerWorker(IWorker<W, R> pImpl, ICircuitBreakerStrategy pCircuitBreakerStrategy) {
		mImpl = pImpl;
		mCircuitBreakerStrategy = pCircuitBreakerStrategy;
	}
	
	@Override
	public R perform(final W pWork) throws Exception {
		return mCircuitBreakerStrategy.executeAndTrip(new Callable<R>() {
			@Override
			public R call() throws Exception {
				return mImpl.perform(pWork);
			}
		});
	}
}
