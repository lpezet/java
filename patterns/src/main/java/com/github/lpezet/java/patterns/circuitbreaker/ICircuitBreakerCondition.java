/**
 * 
 */
package com.github.lpezet.java.patterns.circuitbreaker;

/**
 * @author Luc Pezet
 *
 */
public interface ICircuitBreakerCondition {

	public boolean shouldTrip(Throwable e);
	
}
