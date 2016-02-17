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
public class CyclicExponentialBackoffRefillStrategyTest {
	
	private static class CustomBackoffRefillStrategy extends CyclicExponentialBackoffRefillStrategy {

		private final int mStep;
		
		public CustomBackoffRefillStrategy(int pBase, int pMaxN, TimeUnit pBackoffTimeUnit, int pStep) {
			super(pBase, pMaxN, pBackoffTimeUnit);
			mStep = pStep;
		}
		
		@Override
		protected void updateBackoff() {
			int oStepped = n / mStep;
			mNextRefillInterval = (long) (Math.pow(mBase, oStepped) - 1);
			n = (n+1) % mMaxN;
		}
	}
	
	@Test
	public void custom() {
		IRefillStrategy s = new CustomBackoffRefillStrategy(3, 10, TimeUnit.SECONDS, 3);
		// 1st = 0
		// 2nd = 0
		// 3rd = 0
		// 4th = 2 (3^1 - 1 = 2)
		// 5th = 2 (3^1 - 1 = 2)
		// 6th = 2 (3^1 - 1 = 2)
		// 7th = 8 (3^2 - 1 = 7)
		assertEquals(0, s.nextRefill(0));
		assertEquals(0, s.nextRefill(0));
		assertEquals(0, s.nextRefill(0));
		
		assertEquals(2 * 1000, s.nextRefill(0));
		assertEquals(2 * 1000, s.nextRefill(0));
		assertEquals(2 * 1000, s.nextRefill(0));
		
		assertEquals(8 * 1000, s.nextRefill(0));
		assertEquals(8 * 1000, s.nextRefill(0));
		assertEquals(8 * 1000, s.nextRefill(0));
	}

	@Test
	public void nextRefillTime() {
		int oCycle = 5;
		// This means:
		// 1st call, backoff = 0
		// 2nd call, 1
		// 3rd call, 3
		// 4th call, 7
		// 5th call, 15
		// 6th call, 0
		// ...
		
		CyclicExponentialBackoffRefillStrategy s = new CyclicExponentialBackoffRefillStrategy( 2, oCycle, TimeUnit.SECONDS );
		for (int i = 0; i < 100; i++) {
			long oNow = System.currentTimeMillis();
			long oBackoff = (long) Math.pow(2, i % oCycle) - 1;
			assertEquals("i = " + i, oNow + oBackoff * 1000, s.nextRefill( oNow ));
		}
	}
}
