/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import com.github.lpezet.java.patterns.util.Assert;

/**
 * @author Luc Pezet
 *
 */
public abstract class TokenBucketStrategy implements IThrottleStrategy {
	
	protected final long mCapacity;
	protected final IRefillStrategy mRefillStrategy;
	protected volatile long mNextRefillTime = 0;
	protected volatile long mTokens = 0;
	
	public TokenBucketStrategy(long pCapacity, IRefillStrategy pRefillStrategy) {
		Assert.isTrue( pCapacity > 0, "Capacity must be greater than 0.");
		Assert.isTrue( pRefillStrategy != null, "Must specify refill strategy.");
		
		mCapacity = pCapacity;
		mRefillStrategy = pRefillStrategy;
	}
	
	public long getCapacity() {
		return mCapacity;
	}
	
	public long getNextRefillTime() {
		return mNextRefillTime;
	}
	
	public long getRefillIntervalInMillis() {
		return mRefillStrategy.getIntervalInMillis();
	}
	
	public long getTokens() {
		return mTokens;
	}

	@Override
	public synchronized boolean isThrottled() {
		return isThrottled( 1 );
	}
	
	@Override
	public synchronized boolean isThrottled(long n) {
		Assert.isTrue( n > 0, "n must be greater than 0.");
		Assert.isTrue( n <= mCapacity, "n must be less than bucket capacity.");
		
		if (getCurrentTokens() < n) return true;
		
		mTokens -= n;
		
		return false;
	}

	public synchronized long getCurrentTokens() {
		updateTokens();
		return mTokens;
	}
	
	 protected abstract void updateTokens();
}
