/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import static org.junit.Assert.assertEquals;
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
public class RetryAspectTest {

	@Autowired
	private IRetryMe mTest;
	
	@Test
	public void doIt() throws Exception {
		assertEquals(0, mTest.timesExecuted());
		assertTrue( doSomething() );
		assertEquals(1, mTest.timesExecuted());
		assertTrue( doSomething() );
		assertEquals(2, mTest.timesExecuted());
		assertTrue( doSomething() );
		assertEquals(4, mTest.timesExecuted()); // #3 retried, see ExceptionThrower.
		assertTrue( doSomething() );
		assertEquals(5, mTest.timesExecuted());
		assertTrue( doSomething() );
		assertEquals(7, mTest.timesExecuted()); // #6 retried, see ExceptionThrower.
		
	}
	
	private boolean doSomething() {
		try {
			mTest.doSomething();
			return true;
		} catch (Throwable t) {}
		return false;
	}
}
