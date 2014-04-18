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
