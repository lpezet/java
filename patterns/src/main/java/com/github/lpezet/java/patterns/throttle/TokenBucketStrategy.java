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

import com.github.lpezet.java.patterns.util.Assert;

/**
 * @author Luc Pezet
 *
 */
public abstract class TokenBucketStrategy extends ThrottleStrategySupport {
	
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
	
	@Override
	public synchronized long getWaitTime(long n) {
		// if n > Capacity, what to do?
		return Math.min(0, mNextRefillTime - System.currentTimeMillis());
	}
	
	public long getRefillIntervalInMillis() {
		return mRefillStrategy.getIntervalInMillis();
	}
	
	public long getTokens() {
		return mTokens;
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
