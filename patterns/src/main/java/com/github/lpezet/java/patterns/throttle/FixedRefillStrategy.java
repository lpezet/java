/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import java.util.concurrent.TimeUnit;

import com.github.lpezet.java.patterns.util.Assert;

/**
 * @author Luc Pezet
 *
 */
public class FixedRefillStrategy implements IRefillStrategy {

	private final long mRefillIntervalInMillis;
	
	public FixedRefillStrategy(long pRefillInterval, TimeUnit pRefillIntervalTimeUnit) {
		Assert.isNotNull( pRefillIntervalTimeUnit, "Must provide time unit for interval.");
		Assert.isTrue( pRefillInterval > 0, "Refill interval must be greater than 0.");
		
		mRefillIntervalInMillis = pRefillIntervalTimeUnit.toMillis( pRefillInterval );
	}
	
	@Override
	public long nextRefill(long pNowInMillis) {
		return pNowInMillis + mRefillIntervalInMillis;
	}
	
	@Override
	public long getIntervalInMillis() {
		return mRefillIntervalInMillis;
	}
}
