/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Luc Pezet
 *
 */
public class FixedTokenBucketStrategyTest {
	
	private IRefillStrategy mConstantRefillStrategy;
	
	@Before
	public void setup() {
		mConstantRefillStrategy = new FixedRefillStrategy(1000, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected = IllegalStateException.class)
	public void invalidCapacity() {
		new FixedTokenBucketStrategy(-1, mConstantRefillStrategy);
	}
	
	@Test(expected = IllegalStateException.class)
	public void invalidRefillInterval() {
		new FixedTokenBucketStrategy(1, new FixedRefillStrategy(-1, TimeUnit.MILLISECONDS));
	}
	
	@Test(expected = IllegalStateException.class)
	public void greaterTokensThanCapacity() {
		FixedTokenBucketStrategy oStrategy = new FixedTokenBucketStrategy(10, mConstantRefillStrategy);
		oStrategy.isThrottled( 100 );
	}
	
	@Test
	public void basic() {
		FixedTokenBucketStrategy oStrategy = new FixedTokenBucketStrategy(1, mConstantRefillStrategy);
		assertFalse( oStrategy.isThrottled() );
		assertTrue( oStrategy.isThrottled() );
	}
	
	@Test
	public void unthrottledWhileNonEmpty() {
		FixedTokenBucketStrategy oStrategy = new FixedTokenBucketStrategy(10, mConstantRefillStrategy);
		for (int i = 0; i < 4; i++) assertFalse( oStrategy.isThrottled(2) );
		assertEquals( 2, oStrategy.getCurrentTokens());
	}
	
	@Test
	public void throttledWhileEmpty() {
		long oStart = System.currentTimeMillis();
		FixedTokenBucketStrategy oStrategy = new FixedTokenBucketStrategy(1, new FixedRefillStrategy(500, TimeUnit.MILLISECONDS) );
		assertFalse( oStrategy.isThrottled() ); // now bucket empty
		while ((System.currentTimeMillis() - oStart) < 500)
			assertTrue( oStrategy.isThrottled() );
	}
}
