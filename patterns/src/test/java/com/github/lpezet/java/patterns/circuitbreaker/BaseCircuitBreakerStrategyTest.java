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
package com.github.lpezet.java.patterns.circuitbreaker;

import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author luc
 *
 */
public class BaseCircuitBreakerStrategyTest {

	@Test
	public void closed() throws Exception {
		ICircuitBreaker oBreaker = Mockito.mock(ICircuitBreaker.class);
		ICircuitBreakerHandler oHandler = Mockito.mock(ICircuitBreakerHandler.class);
		BaseCircuitBreakerStrategy oStg = new BaseCircuitBreakerStrategy(oBreaker, oHandler);
		Callable oCallable = Mockito.mock(Callable.class);
		when(oBreaker.isClosed()).thenReturn(true);
		when(oCallable.call()).thenReturn("Hello");
		oStg.executeAndTrip(oCallable);
	}
	
	@Test
	public void open() throws Exception {
		ICircuitBreaker oBreaker = Mockito.mock(ICircuitBreaker.class);
		ICircuitBreakerHandler oHandler = Mockito.mock(ICircuitBreakerHandler.class);
		BaseCircuitBreakerStrategy oStg = new BaseCircuitBreakerStrategy(oBreaker, oHandler);
		Callable oCallable = Mockito.mock(Callable.class);
		when(oBreaker.isClosed()).thenReturn(false);
		when(oHandler.handleOpen(oBreaker, oCallable)).thenReturn("Hello");
		oStg.executeAndTrip(oCallable);
	}
}
