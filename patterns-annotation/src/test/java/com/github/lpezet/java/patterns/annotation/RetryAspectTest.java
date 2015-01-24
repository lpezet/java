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
public class RetryAspectTest {

	@Autowired
	private IRetryMe mTest;
	
	@Test
	public void doIt() throws Exception {
		assertEquals(0, mTest.timesExecuted());
		assertFalse( doSomething() );
		assertEquals(1, mTest.timesExecuted());
		assertFalse( doSomething() );
		assertEquals(2, mTest.timesExecuted());
		
		assertTrue( doSomething() );
		assertEquals(3, mTest.timesExecuted());
		assertFalse( doSomething() );
		assertEquals(4, mTest.timesExecuted());
		
	}
	
	private boolean doSomething() {
		try {
			mTest.doSomething();
			return true;
		} catch (Throwable t) {}
		return false;
	}
}
