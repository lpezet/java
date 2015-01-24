/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

/**
 * @author Luc Pezet
 *
 */
public interface ISuperviseMe {

	public boolean doSomething();
	
	public int timesExecuted();
	
	public void timeToWaitInMillis(long pTime);
}
