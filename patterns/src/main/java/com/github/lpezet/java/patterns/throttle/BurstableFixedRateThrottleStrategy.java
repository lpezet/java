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
/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.patterns.util.Assert;

/**
 * @author lucpezet
 *
 */
public class BurstableFixedRateThrottleStrategy extends ThrottleStrategySupport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BurstableFixedRateThrottleStrategy.class);
	
	protected final long mCapacity;
	protected final IRefillStrategy mRefillStrategy;
	protected volatile long mNextRefillTime = 0;
	protected volatile long mLastTime;
	protected double mOututRate;
	protected long mBurstsCapacity;
	protected volatile long mRemainingBursts;
	
	public BurstableFixedRateThrottleStrategy(long pCapacity, IRefillStrategy pRefillStrategy, long pBursts) {
		Assert.isTrue( pCapacity > 0, "Capacity must be greater than 0.");
		Assert.isTrue( pRefillStrategy != null, "Must specify refill strategy.");
		
		mCapacity = pCapacity;
		mRefillStrategy = pRefillStrategy;
		mLastTime = 0;//System.currentTimeMillis();
		mOututRate = pCapacity / (double) pRefillStrategy.getIntervalInMillis();
		mNextRefillTime = pRefillStrategy.nextRefill( mLastTime );
		mBurstsCapacity = pBursts;
		mRemainingBursts = pBursts;
	}
	
	@Override
	public synchronized boolean isThrottled(long n) {
		if ( mLastTime == 0 ) {
			mLastTime = System.currentTimeMillis();
			return n > mCapacity; // Is that right?
		}
		
		refresh( n );
		
		long oNow = System.currentTimeMillis();
		double oInputRate = n / (double) (oNow - mLastTime + 1);
		if ( LOGGER.isTraceEnabled() ) LOGGER.trace("Ri = {} vs Ro = {}.", new Object[] { String.format("%.5f", oInputRate), String.format("%.5f", mOututRate) });
		if ( oInputRate > mOututRate ) {
			return burst(n);
		} else {
			//System.out.println("Conforming rate: Ri = " + String.format("%.5f", oInputRate) + " vs. Ro = " + String.format("%.5f", mOututRate));
			// conforming rate.
			mLastTime = oNow;
			return false;
		}
	}


	private boolean burst(long n) {
		if ( mRemainingBursts >= n) {
			mRemainingBursts -= n;
			if ( LOGGER.isTraceEnabled() ) LOGGER.trace("Burst (remaining={}).", new Object[] { mRemainingBursts });
			return false;
		} else {
			return true;
		}
	}
	
	protected void refresh(long n) {
		long oCurrentTime = System.currentTimeMillis();
        if( oCurrentTime < mNextRefillTime ) return;

        refill();
        
        mNextRefillTime = mRefillStrategy.nextRefill( oCurrentTime );// oCurrentTime + mRefillInterval;
	}
	
	protected void refill() {
		if ( LOGGER.isTraceEnabled() ) LOGGER.trace("Refilled.");
		mRemainingBursts = mBurstsCapacity;
	}
	
	public long getNextTime(long n) {
		return Math.round( n / mOututRate ) + mLastTime + 1;
	}
	
	@Override
	public synchronized long getWaitTime(long n) {
		return getNextTime(n) - System.currentTimeMillis();
	}
	
	public long getNextRefillTime() {
		return mNextRefillTime;
	}

}
