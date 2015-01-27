/**
 * The MIT License
 * Copyright (c) 2015 Luc Pezet
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
