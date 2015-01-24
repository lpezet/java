/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import static org.junit.Assert.assertEquals;

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
public class CircuitBreakerAspectTest {

	@Autowired
	private IShortCircuitMe mTest;
	
	@Test
	public void doIt() throws Exception {
		assertEquals(0, mTest.timesExecuted());
		doSometing();
		assertEquals(1, mTest.timesExecuted());
		doSometing();
		assertEquals(1, mTest.timesExecuted());
		Thread.sleep(700);
		// Back to normal
		doSometing();
		assertEquals(2, mTest.timesExecuted());
	}

	private void doSometing() {
		try {
			mTest.doSomething();
		} catch(Exception e) {};
	}
}
