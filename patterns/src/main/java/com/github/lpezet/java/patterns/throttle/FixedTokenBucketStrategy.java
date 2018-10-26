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

        refill();
        
        mNextRefillTime = mRefillStrategy.nextRefill( oCurrentTime );// oCurrentTime + mRefillInterval;
	}
	
	protected void refill() {
		mTokens = mCapacity;
	}
	
}
