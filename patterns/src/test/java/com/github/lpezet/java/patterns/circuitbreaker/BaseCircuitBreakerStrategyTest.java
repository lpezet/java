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
