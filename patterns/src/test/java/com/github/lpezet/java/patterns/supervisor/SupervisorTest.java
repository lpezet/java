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
package com.github.lpezet.java.patterns.supervisor;

import static org.junit.Assert.assertNotNull;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author luc
 *
 */
public class SupervisorTest {

	@Test(expected=TimeoutException.class)
	public void timeout() throws Exception {
		ExecutorService oExec = Executors.newCachedThreadPool();
		Supervisor<Integer> s = new Supervisor<Integer>(oExec, 100, TimeUnit.MILLISECONDS);
		Callable<Integer> oTest = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(200);
				return new Random().nextInt(999);
			}
		};
		s.supervise(oTest);
	}
	
	@Test
	public void ok() throws Exception {
		ExecutorService oExec = Executors.newCachedThreadPool();
		Supervisor<Integer> s = new Supervisor<Integer>(oExec, 100, TimeUnit.MILLISECONDS);
		Callable<Integer> oTest = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(50);
				return new Random().nextInt(999);
			}
		};
		Integer i = s.supervise(oTest);
		assertNotNull(i);
	}
}
