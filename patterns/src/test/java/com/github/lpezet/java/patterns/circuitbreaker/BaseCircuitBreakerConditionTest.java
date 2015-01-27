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
