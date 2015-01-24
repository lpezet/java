/**
 * 
 */
package com.github.lpezet.java.patterns.circuitbreaker;

/**
 * @author Luc Pezet
 *
 */
public interface ICircuitBreakerLogic {

	public boolean shouldTrip(Throwable e);
	
}
