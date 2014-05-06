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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author luc
 *
 */
public class SingleTryCircuitBreakerStrategyTest {

	@Test(expected=CircuitBreakerOpenException.class)
	public void stillOpen() throws Exception {
		SingleTryCircuitBreakerStrategy oStg = new SingleTryCircuitBreakerStrategy();
		oStg.setOpenToHalfOpenWaitTimeInMillis(500);
		ICircuitBreaker oBreaker = Mockito.mock(ICircuitBreaker.class);
		when(oBreaker.getLastStateChangedDateUTC()).thenReturn(DateTime.now());
		
		oStg.handleOpen(oBreaker, null);
	}
	
	@Test
	public void openToHalfOpenToClosed() throws Exception {
		SingleTryCircuitBreakerStrategy oStg = new SingleTryCircuitBreakerStrategy();
		oStg.setOpenToHalfOpenWaitTimeInMillis(500);
		ICircuitBreaker oBreaker = Mockito.mock(ICircuitBreaker.class);
		when(oBreaker.getLastStateChangedDateUTC()).thenReturn(DateTime.now().minusDays(1));
		Callable oCallable = Mockito.mock(Callable.class);
		when(oCallable.call()).thenReturn("HELLO");
		oStg.handleOpen(oBreaker, oCallable);
		
		verify(oBreaker, times(1)).halfOpen();
		verify(oBreaker, times(1)).reset();
	}
	
	@Test(expected=IOException.class)
	public void openToHalfOpen() throws Exception {
		SingleTryCircuitBreakerStrategy oStg = new SingleTryCircuitBreakerStrategy();
		oStg.setOpenToHalfOpenWaitTimeInMillis(500);
		ICircuitBreaker oBreaker = Mockito.mock(ICircuitBreaker.class);
		when(oBreaker.getLastStateChangedDateUTC()).thenReturn(DateTime.now().minusDays(1));
		Callable oCallable = Mockito.mock(Callable.class);
		when(oCallable.call()).thenThrow(new IOException());
		try {
			oStg.handleOpen(oBreaker, oCallable);
		} finally {
			verify(oBreaker, times(1)).halfOpen();
			verify(oBreaker, never()).reset();
			verify(oBreaker, times(1)).trip(any(Exception.class));
		}
	}
}
