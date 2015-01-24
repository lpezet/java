/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

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
public class TakeCareOfMeTest {

	@Autowired
	private ITakeCareOfMe mTest;
	
	@Test
	public void doIt() throws Exception {
		makeItWait(1000);
		assertFalse( doSomething() );
		System.out.println("################# Waiting for CB to set to Open.... #########");
		Thread.sleep(500); // open time for CB...
		makeItWait(50);
		assertTrue( doSomething() );
		assertTrue( doSomething() );
	}

	private void makeItWait(final int pTimeInMillis) {
		mTest.setBehavior(new IBehavior() {	
			@Override
			public void behave() throws Exception {
				try {
					Thread.sleep(pTimeInMillis);
				} catch (InterruptedException e) {
					System.out.println("## Uh...I've been interrupted!");
					throw e;
					//e.printStackTrace();
				}
			}
		});
	}

	private boolean doSomething() {
		try {
			return mTest.doSomething();
		} catch (Throwable t) {}
		return false;
	}
}
