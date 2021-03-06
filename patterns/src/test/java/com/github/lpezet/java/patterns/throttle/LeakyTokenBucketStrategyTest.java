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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Luc Pezet
 *
 */
public class LeakyTokenBucketStrategyTest {
	
	private IRefillStrategy mConstantRefillStrategy;
	
	@Before
	public void setup() {
		mConstantRefillStrategy = new FixedRefillStrategy(100, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected = IllegalStateException.class)
	public void invalidCapacity() {
		new LeakyTokenBucketStrategy(-1, mConstantRefillStrategy, 100, 100, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected = IllegalStateException.class)
	public void invalidRefillInterval() {
		new LeakyTokenBucketStrategy(1, new FixedRefillStrategy( -1, TimeUnit.MILLISECONDS ), 100, 100, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected = IllegalStateException.class)
	public void invalidTokensLeaked() {
		new LeakyTokenBucketStrategy(1, mConstantRefillStrategy, -1, 100, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected = IllegalStateException.class)
	public void invalidLeakInterval() {
		new LeakyTokenBucketStrategy(1, mConstantRefillStrategy, 10, -1, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void leakingTooFast() throws Exception {
		LeakyTokenBucketStrategy oStrategy = new LeakyTokenBucketStrategy(10, new FixedRefillStrategy( 10000, TimeUnit.MILLISECONDS ), 10, 100, TimeUnit.MILLISECONDS);
		// Start it all
		assertFalse( oStrategy.isThrottled() );
		
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			//System.out.println( oStrategy.getCurrentTokens() );
			assertTrue( oStrategy.isThrottled() );
		}
		
	}

	
}
