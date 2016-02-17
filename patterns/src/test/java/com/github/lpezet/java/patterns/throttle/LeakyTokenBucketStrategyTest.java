/**
 * 
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
