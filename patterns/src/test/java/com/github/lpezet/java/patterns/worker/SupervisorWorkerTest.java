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
/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.lpezet.java.patterns.supervisor.TimeoutException;

/**
 * @author luc
 *
 */
public class SupervisorWorkerTest {
	
	private static class IntegerResult {
		private int mResult;
		public IntegerResult(int pResult) {
			mResult = pResult;
		}
		public int getResult() {
			return mResult;
		}
	}
	
	@Test(expected=TimeoutException.class)
	public void failFast() throws Exception {
		IWorker<Void, Void> oWorker = new IWorker<Void, Void>() {
			@Override
			public Void perform(Void pWork) throws Exception {
				Thread.sleep(100);
				return null;
			}
		};
		SupervisorWorker<Void, Void> s = new SupervisorWorker<Void, Void>(oWorker, 50, TimeUnit.MILLISECONDS);
		s.perform(null);
	}
	
	@Test
	public void succeed() throws Exception {
		IWorker<Void, IntegerResult> oWorker = new IWorker<Void, IntegerResult>() {
			@Override
			public IntegerResult perform(Void pWork) throws Exception {
				return new IntegerResult(987);
			}
		};
		SupervisorWorker<Void, IntegerResult> s = new SupervisorWorker<Void, IntegerResult>(oWorker, 100, TimeUnit.MILLISECONDS);
		IntegerResult oActual = s.perform(null);
		assertNotNull(oActual);
		assertEquals(987, oActual.getResult());
	}

}
