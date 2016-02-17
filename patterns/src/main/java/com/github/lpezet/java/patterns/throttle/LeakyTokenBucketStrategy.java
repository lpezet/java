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
public class LeakyTokenBucketStrategy extends TokenBucketStrategy {

	protected final long mLeaked;
	protected final long mLeakInterval;
	private long mLastTime = 0;
	
	public LeakyTokenBucketStrategy(long pCapacity, IRefillStrategy pRefillStrategy, // long pRefillInterval, TimeUnit pRefillIntervalUnit, 
			long pTokensLeaked, long pLeakInterval, TimeUnit pLeakIntervalTimeUnit) {
		super(pCapacity, pRefillStrategy); //pRefillInterval, pRefillIntervalUnit);
		
		Assert.isTrue( pLeakInterval >= 0, "Leak interval must be positive.");
        Assert.isTrue( pTokensLeaked >= 0, "Number of tokens leaked must be positive.");
        
		mLeaked = pTokensLeaked;
		mLeakInterval = pLeakIntervalTimeUnit.toMillis( pLeakInterval );
	}

	@Override
	protected void updateTokens() {
		long oNow = System.currentTimeMillis();

		if( oNow >= mNextRefillTime ){
        	mLastTime = oNow;
            mTokens = mCapacity;
            mNextRefillTime = mRefillStrategy.nextRefill( oNow );
            return;
        }
        
        long oElapsedTime = oNow - mLastTime;
        long oRate = oElapsedTime / mLeakInterval;
        long oTokens = mTokens - ( oRate * mLeaked );
        
        //System.out.println("Now = " + oNow + ", LastTime = " + mLastTime + ", oElapsedTime = " + oElapsedTime + ", oRate = " + oRate + ", oTokens = " + oTokens);
        
        mTokens = Math.max(0, oTokens);
        
        /*
        // calculate max tokens possible till end
        long oTimeToNextRefill = mNextRefillTime - oNow;
        long oRate = oTimeToNextRefill / mLeakInterval;
        long oTokensToRemove = oRate * mLeaked;
        
        System.out.println("oTimeToNextRefill = " + oTimeToNextRefill + ", oRate = " + oRate + ", oMaxPossibleTokens = " + oMaxPossibleTokens);
        // edge case, if current time not at edge of step
        if(( oTimeToNextRefill % mLeakInterval ) > 0) {
        	System.out.println("## Modulo = " + ( oTimeToNextRefill % mLeakInterval ));
        	oMaxPossibleTokens += mLeaked;
        }
        // tokens must be lesser of current and max possible tokens
        if(oMaxPossibleTokens < mTokens) {
        	System.out.println("## Lesser!!!");
        	mTokens = oMaxPossibleTokens;
        }
        */
        
	}

}
