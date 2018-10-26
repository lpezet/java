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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Test;

/**
 * @author lucpezet
 *
 */
public class BurstableFixedRateThrottleStrategyTest {
	
	private static final boolean DEBUG = false;
	
	/**
	 * Max 1 request/second with bursts allowed up to 3/sec. In the end then, we end up with a max of 4/sec.
	 * With "|" being un-throttled requests and "." throttled ones, the pattern over 4ish seconds then is:
	 * ||||.||||.||||.||||.
	 * 
	 * @throws Exception
	 */
	@Test
	public void burstWaitingFor1RequestPerSecond() throws Exception {
		FixedRefillStrategy oRefill = new FixedRefillStrategy(10, TimeUnit.MILLISECONDS);
		BurstableFixedRateThrottleStrategy oThrottling = new BurstableFixedRateThrottleStrategy(1, oRefill, 3);
		
		List<Double> oThroughputs = new ArrayList<>();
		
		long oStart = System.currentTimeMillis();
		long oEnd = 0;
		double oElapsedInSeconds = 0.0;
		long oElapsed = 0;
		StringBuffer oInputs = new StringBuffer();
		//StringBuffer oOutputs = new StringBuffer();
		for (int i = 1; i <= 20; i++) {
			if ( oThrottling.isThrottled(1) ) {
				//long oWaitTime = oThrottling.getNextTime(1) - System.currentTimeMillis();
				long oWaitTime = oThrottling.getWaitTime(1);
				if (DEBUG) System.out.println("Waiting: " + oWaitTime);
				Thread.sleep(oWaitTime);
				oInputs.append(".");
			} else {
				oInputs.append("|");
			}
			
			oEnd = System.currentTimeMillis() + 1;
			oElapsed = (oEnd - oStart);
			oElapsedInSeconds = oElapsed/1000.0;
			double oThroughput = (i/oElapsedInSeconds);
			
			
			if (DEBUG) System.out.println( oInputs.toString() );
			
			
			oThroughputs.add( oThroughput );
		}
		double[] oDoubles = new double[ oThroughputs.size() ];
		for (int i = 0; i < oThroughputs.size(); i++) oDoubles[i] = oThroughputs.get(i);
		if (DEBUG) System.out.println("Time elapsed = " + (System.currentTimeMillis() - oStart)/1000.0 + "s, Average throughput = " + StatUtils.geometricMean(oDoubles) + "/s");
		
		assertEquals("||||.||||.||||.||||.", oInputs.toString());
	}
	
	/**
	 * Max 1 request/second with no bursts allowed.
	 * With "|" being un-throttled requests and "." throttled ones, the pattern over 10ish seconds then is:
	 * |.|.|.|.|.|.|.|.|.|.
	 * 
	 * @throws Exception
	 */
	@Test
	public void noBurstWaitingFor1RequestPerSecond() throws Exception {
		FixedRefillStrategy oRefill = new FixedRefillStrategy(10, TimeUnit.MILLISECONDS);
		BurstableFixedRateThrottleStrategy oThrottling = new BurstableFixedRateThrottleStrategy(1, oRefill, 0);
		
		List<Double> oThroughputs = new ArrayList<>();
		
		long oStart = System.currentTimeMillis();
		long oEnd = 0;
		double oElapsedInSeconds = 0.0;
		long oElapsed = 0;
		StringBuffer oInputs = new StringBuffer();
		//StringBuffer oOutputs = new StringBuffer();
		for (int i = 1; i <= 20; i++) {
			if ( oThrottling.isThrottled(1) ) {
				//long oWaitTime = oThrottling.getNextTime(1) - System.currentTimeMillis();
				long oWaitTime = oThrottling.getWaitTime(1);
				if (DEBUG) System.out.println("Waiting: " + oWaitTime);
				Thread.sleep(oWaitTime);
				oInputs.append(".");
			} else {
				oInputs.append("|");
			}
			
			oEnd = System.currentTimeMillis() + 1;
			oElapsed = (oEnd - oStart);
			oElapsedInSeconds = oElapsed/1000.0;
			double oThroughput = (i/oElapsedInSeconds);
			
			
			if (DEBUG) System.out.println( oInputs.toString() );
			
			
			oThroughputs.add( oThroughput );
		}
		double[] oDoubles = new double[ oThroughputs.size() ];
		for (int i = 0; i < oThroughputs.size(); i++) oDoubles[i] = oThroughputs.get(i);
		if (DEBUG) System.out.println("Time elapsed = " + (System.currentTimeMillis() - oStart)/1000.0 + "s, Average throughput = " + StatUtils.geometricMean(oDoubles) + "/s");
		
		assertEquals("|.|.|.|.|.|.|.|.|.|.", oInputs.toString());
	}
	
	/**
	 * Max 1 request/second with no bursts allowed. This time, the caller controls requests to at least 1/1001ms, which means it should not be throttled at all.
	 * With "|" being un-throttled requests and "." throttled ones, the pattern over 20ish seconds then is:
	 * ||||||||||||||||||||
	 * 
	 * @throws Exception
	 */
	@Test
	public void noBurstNotWaitingFor1RequestPerSecond() throws Exception {
		FixedRefillStrategy oRefill = new FixedRefillStrategy(10, TimeUnit.MILLISECONDS);
		BurstableFixedRateThrottleStrategy oThrottling = new BurstableFixedRateThrottleStrategy(1, oRefill, 0);
		
		List<Double> oThroughputs = new ArrayList<>();
		
		long oStart = System.currentTimeMillis();
		long oEnd = 0;
		double oElapsedInSeconds = 0.0;
		long oElapsed = 0;
		StringBuffer oInputs = new StringBuffer();
		//StringBuffer oOutputs = new StringBuffer();
		for (int i = 1; i <= 20; i++) {
			if ( oThrottling.isThrottled(1) ) {
				fail("Must not be waiting if we control input to more than expected.");
				/*
				//long oWaitTime = oThrottling.getNextTime(1) - System.currentTimeMillis();
				long oWaitTime = oThrottling.getWaitTime(1);
				System.out.println("Waiting: " + oWaitTime);
				Thread.sleep(oWaitTime);
				oInputs.append(".");
				*/
			} else {
				oInputs.append("|");
			}
			
			oEnd = System.currentTimeMillis() + 1;
			oElapsed = (oEnd - oStart);
			oElapsedInSeconds = oElapsed/1000.0;
			double oThroughput = (i/oElapsedInSeconds);
			
			
			if (DEBUG) System.out.println( oInputs.toString() );
			
			
			oThroughputs.add( oThroughput );
			
			Thread.sleep(11);
		}
		double[] oDoubles = new double[ oThroughputs.size() ];
		for (int i = 0; i < oThroughputs.size(); i++) oDoubles[i] = oThroughputs.get(i);
		if (DEBUG) System.out.println("Time elapsed = " + (System.currentTimeMillis() - oStart)/1000.0 + "s, Average throughput = " + StatUtils.geometricMean(oDoubles) + "/s");
		
		assertEquals("||||||||||||||||||||", oInputs.toString());
	}
}
