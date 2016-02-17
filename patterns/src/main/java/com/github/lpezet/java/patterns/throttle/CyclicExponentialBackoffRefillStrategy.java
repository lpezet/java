/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import java.util.concurrent.TimeUnit;

/**
 * @author Luc Pezet
 *
 */
public class CyclicExponentialBackoffRefillStrategy implements IRefillStrategy {
	
	protected final int mBase;
	protected final int mMaxN;
	protected final TimeUnit mTimeUnit;
	protected int n = 0;
	protected long mNextRefillInterval = 0;
	
	public CyclicExponentialBackoffRefillStrategy(int pBase, int pMaxN, TimeUnit pBackoffTimeUnit) {
		mBase = pBase;
		mMaxN = pMaxN;
		mTimeUnit = pBackoffTimeUnit;
	}

	@Override
	public synchronized long nextRefill(long pNowInMillis) {
		updateBackoff();
		return pNowInMillis + mTimeUnit.toMillis( mNextRefillInterval );
	}
	
	@Override
	public long getIntervalInMillis() {
		return mNextRefillInterval;
	}
	
	protected void updateBackoff() {
		mNextRefillInterval = (long) ( Math.pow( mBase, n ) - 1 );
		n = ( n+1 ) % mMaxN;
	}
}
