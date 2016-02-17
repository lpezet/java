/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;


/**
 * @author Luc Pezet
 *
 */
public class FixedTokenBucketStrategy extends TokenBucketStrategy {

	public FixedTokenBucketStrategy(long pCapacity, IRefillStrategy pRefillStrategy) { // long pRefillInterval, TimeUnit pRefillIntervalUnit) {
		super(pCapacity, pRefillStrategy); //pRefillInterval, pRefillIntervalUnit);
	}

	@Override
	protected void updateTokens() {
		long oCurrentTime = System.currentTimeMillis();
        if( oCurrentTime < mNextRefillTime ) return;

        mTokens = mCapacity;
        mNextRefillTime = mRefillStrategy.nextRefill( oCurrentTime );// oCurrentTime + mRefillInterval;
	}
	
}
