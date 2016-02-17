/**
 * The MIT License
 * Copyright (c) 2014 Luc Pezet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
