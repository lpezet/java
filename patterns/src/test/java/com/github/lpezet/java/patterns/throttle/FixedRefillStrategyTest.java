/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author Luc Pezet
 *
 */
public class FixedRefillStrategyTest {

	@Test
	public void nextRefillTime() {
		FixedRefillStrategy s = new FixedRefillStrategy(123, TimeUnit.MILLISECONDS);
		for (int i = 0; i < 100; i++) {
			long oNow = System.currentTimeMillis();
			assertEquals(oNow + 123, s.nextRefill( oNow ));
		}
	}
}
