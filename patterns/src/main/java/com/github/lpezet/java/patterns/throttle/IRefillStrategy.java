/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

/**
 * @author Luc Pezet
 *
 */
public interface IRefillStrategy {

	public long nextRefill(long pNowInMillis);
	
	public long getIntervalInMillis();
	
}
