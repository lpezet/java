/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

/**
 * @author lucpezet
 *
 */
public class ThrottleStrategySupport implements IThrottleStrategy {

	@Override
	public synchronized long getWaitTime() {
		return getWaitTime(1);
	}
	
	@Override
	public synchronized long getWaitTime(long n) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public synchronized boolean isThrottled() {
		return isThrottled(1);
	}
	
	@Override
	public synchronized boolean isThrottled(long n) {
		// TODO Auto-generated method stub
		return false;
	}
}
