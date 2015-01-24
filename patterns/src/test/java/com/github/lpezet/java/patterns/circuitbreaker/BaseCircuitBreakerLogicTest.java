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
public class BaseCircuitBreakerLogicTest {
	
	@Test
	public void shouldTripAfterFirstException() {
		BaseCircuitBreakerLogic oLogic = new BaseCircuitBreakerLogic(Exception.class, 1);
		assertTrue( oLogic.shouldTrip(new Exception()) );
		oLogic = new BaseCircuitBreakerLogic();
		assertTrue( oLogic.shouldTrip(new Exception()) );
	}
	
	@Test
	public void shouldNotTripAfterFirstException() {
		BaseCircuitBreakerLogic oLogic = new BaseCircuitBreakerLogic(Exception.class, 2);
		assertFalse( oLogic.shouldTrip(new Exception()) );
		assertTrue( oLogic.shouldTrip(new Exception()) );
	}
	
	@Test
	public void shouldTripOnlyForSpecificException() {
		BaseCircuitBreakerLogic oLogic = new BaseCircuitBreakerLogic(ArrayIndexOutOfBoundsException.class, 2);
		assertFalse( oLogic.shouldTrip(new Exception()) );
		assertFalse( oLogic.shouldTrip(new Exception()) );
		assertFalse( oLogic.shouldTrip(new ArrayIndexOutOfBoundsException()) );
		assertTrue( oLogic.shouldTrip(new ArrayIndexOutOfBoundsException()) );
	}
}
