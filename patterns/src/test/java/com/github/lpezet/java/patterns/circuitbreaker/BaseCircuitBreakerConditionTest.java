/**
 * 
 */
package com.github.lpezet.java.patterns.circuitbreaker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Luc Pezet
 *
 */
public class BaseCircuitBreakerConditionTest {

	@Test
	public void shouldTripAfterFirstException() {
		BaseCircuitBreakerCondition oCondition = new BaseCircuitBreakerCondition(1, Exception.class);
		assertTrue( oCondition.shouldTrip(new Exception()) );
		oCondition = new BaseCircuitBreakerCondition();
		assertTrue( oCondition.shouldTrip(new Exception()) );
	}
	
	@Test
	public void shouldNotTripAfterFirstException() {
		BaseCircuitBreakerCondition oCondition = new BaseCircuitBreakerCondition(2, Exception.class);
		assertFalse( oCondition.shouldTrip(new Exception()) );
		assertTrue( oCondition.shouldTrip(new Exception()) );
	}
	
	@Test
	public void shouldTripOnlyForSpecificException() {
		BaseCircuitBreakerCondition oCondition = new BaseCircuitBreakerCondition(2, ArrayIndexOutOfBoundsException.class);
		assertFalse( oCondition.shouldTrip(new Exception()) );
		assertFalse( oCondition.shouldTrip(new Exception()) );
		assertFalse( oCondition.shouldTrip(new ArrayIndexOutOfBoundsException()) );
		assertTrue( oCondition.shouldTrip(new ArrayIndexOutOfBoundsException()) );
	}
}
