/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

/**
 * @author Luc Pezet
 *
 */
public interface IThrottleStrategy {
	
	public boolean isThrottled();
	
	public boolean isThrottled(long n);
	
	public long getNextRefillTime();
	
}
