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
package com.github.lpezet.java.patterns.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.lpezet.java.patterns.supervisor.TimeoutException;

/**
 * @author luc
 *
 */
public class SupervisorCommandTest {

	@Test
	public void succeed() throws Exception {
		ICommand<Integer> oCmd = new BaseCommand<Integer>() {
			@Override
			public Integer execute() throws Exception {
				return 987;
			}
		};
		SupervisorCommand<Integer> s = new SupervisorCommand<Integer>(oCmd, 100, TimeUnit.MILLISECONDS);
		Integer oActual = s.execute();
		assertNotNull(oActual);
		assertEquals(987, oActual.intValue());
	}
	
	@Test(expected=TimeoutException.class)
	public void failFast() throws Exception {
		ICommand<Void> oCmd = new BaseCommand<Void>() {
			@Override
			public Void execute() throws Exception {
				Thread.sleep(100);
				return null;
			}
		};
		SupervisorCommand<Void> s = new SupervisorCommand<Void>(oCmd, 50, TimeUnit.MILLISECONDS);
		s.execute();
	}
}
