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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Test;

/**
 * @author lucpezet
 *
 */
public class ThrottleTest {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(ThrottleTest2.class);
	
	private static interface MyAPI {
		
		public void sendSomethingToServer(String pName);
		
	}
	
	private static class MyAPIImpl implements MyAPI {
		private long mLastCallTime = System.currentTimeMillis();
		private List<Double> mTimesBetweenCalls = new ArrayList<>();
		
		
		@Override
		public void sendSomethingToServer(String pName) {
			synchronized (mTimesBetweenCalls) {
				long oNow = System.currentTimeMillis();
				long oTimeElapsed = oNow - mLastCallTime;
				mTimesBetweenCalls.add((double) oTimeElapsed);
				mLastCallTime = oNow;
				//System.out.println(pName + " did something at " + oNow);
			}
		}
		
		public double[] getTimesBetweenCalls() {
			synchronized (mTimesBetweenCalls) {
				double[] oDoubles = new double[ mTimesBetweenCalls.size() ];
				//System.out.println("Size = " + mTimesBetweenCalls.size());
				for (int i = 0; i < mTimesBetweenCalls.size(); i++) oDoubles[i] = mTimesBetweenCalls.get(i);
				return oDoubles;
			}
		}
	}
	
	private static class MyClient implements Callable<Void> {
		private final MyAPI mMyAPI;
		public MyClient(MyAPI pAPI) {
			mMyAPI = pAPI;
		}
		
		public void doSomething() throws Exception {
			mMyAPI.sendSomethingToServer(Thread.currentThread().getName());
		}
		
		@Override
		public Void call() throws Exception {
			//System.out.println(Thread.currentThread().getName() + ".call()...");
			doSomething();
			return null;
		}
	}
	
	private static class MyThrottledAPI implements MyAPI {
		private final MyAPI mDelegate;
		private final IThrottle<Void> mThrottle;
		
		public MyThrottledAPI(MyAPI pDelegate, IThrottle<Void> pThrottle) {
			mDelegate = pDelegate;
			mThrottle = pThrottle;
		}
		
		@Override
		public void sendSomethingToServer(final String pName) {
			mThrottle.throttle(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					mDelegate.sendSomethingToServer( pName );
					return null;
				}
			});
		}
		
	}
	
	
	@Test
	public void notThrottled() throws Exception {
		MyAPIImpl oAPI = new MyAPIImpl();
		
		ExecutorService executor = Executors.newFixedThreadPool(100);//newSingleThreadExecutor();
		Collection<MyClient> oMyClients = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
			oMyClients.add(new MyClient( oAPI ));
		}
		List<Future<Void>> oResults = executor.invokeAll(oMyClients);
		for (Future<Void> f : oResults) f.get();
		double[] oTimesBetweenCallsOrig = oAPI.getTimesBetweenCalls();
		double[] oTimesBetweenCalls = Arrays.copyOfRange(oTimesBetweenCallsOrig, 1, oTimesBetweenCallsOrig.length);
		double oAvgTimeBetweenCallsInMs = StatUtils.geometricMean( oTimesBetweenCalls );
		
		System.out.println("Average time between calls = " + oAvgTimeBetweenCallsInMs + "ms.");
		assertTrue( oAvgTimeBetweenCallsInMs < 1000);
	}
	
	@Test
	public void throttled() throws Exception {
		MyAPIImpl oAPI = new MyAPIImpl();
		
		IRefillStrategy oRefillStrategy = new FixedRefillStrategy(100, TimeUnit.MILLISECONDS);
		BurstableFixedRateThrottleStrategy oThrottleStrategy = new BurstableFixedRateThrottleStrategy(1, oRefillStrategy, 0);
		IThrottle<Void> oThrottle = new ThreadSafeThrottle<>(oThrottleStrategy);
		MyThrottledAPI oThrottledAPI = new MyThrottledAPI(oAPI, oThrottle);
		
		ExecutorService executor = Executors.newFixedThreadPool(100);//newSingleThreadExecutor();
		Collection<MyClient> oMyClients = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
			oMyClients.add(new MyClient( oThrottledAPI ));
		}
		List<Future<Void>> oResults = executor.invokeAll(oMyClients);
		for (Future<Void> f : oResults) f.get();
		double[] oTimesBetweenCallsOrig = oAPI.getTimesBetweenCalls();
		double[] oTimesBetweenCalls = Arrays.copyOfRange(oTimesBetweenCallsOrig, 1, oTimesBetweenCallsOrig.length);
		
		//for (int i = 0; i < oTimesBetweenCalls.length; i++) System.out.println(i + "=" + oTimesBetweenCalls[i]);
		double oAvgTimeBetweenCallsInMs = StatUtils.geometricMean( oTimesBetweenCalls );
		
		System.out.println("Average time between calls = " + oAvgTimeBetweenCallsInMs + "ms.");
		assertTrue( oAvgTimeBetweenCallsInMs >= 100);
	}

}
