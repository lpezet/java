/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Luc Pezet
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ShortCircuitAspectTest {

	@Autowired
	private IShortCircuitMe mTest;
	
	@Test
	public void doIt() throws Exception {
		assertEquals(0, mTest.timesExecuted());
		assertFalse( doSometing() ); // Exception thrown
		assertEquals(1, mTest.timesExecuted());
		assertFalse( doSometing() ); // CircuitBreaker Open
		assertEquals(1, mTest.timesExecuted());
		Thread.sleep(700); // Waiting for Circuit Break to go from Hafl Open to Open.
		// Back to normal
		assertTrue( doSometing() );
		assertEquals(2, mTest.timesExecuted()); // Circuit Break closed.
	}

	private boolean doSometing() {
		try {
			mTest.doSomething();
			return true;
		} catch(Exception e) {};
		return false;
	}
}
