/**
 * 
 */
package com.github.lpezet.java.patterns.retry;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author luc
 *
 */
public class BaseRetryStrategyTest {

	@Test
	public void noRetry() throws Exception {
		IRetryCondition oCond = Mockito.mock(IRetryCondition.class);
		IBackoffStrategy oBackoff = Mockito.mock(IBackoffStrategy.class);
		BaseRetryStrategy oStg = new BaseRetryStrategy(oCond, oBackoff);
		oStg.executeAndRetry(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 1;
			}
		});
		verify(oCond, never()).shouldRetry(any(Callable.class), anyInt(), any(Exception.class));
		verify(oBackoff, never()).pauseBeforeNextRetry(any(Callable.class), anyInt(), any(Exception.class));
	}
	
	@Test
	public void retry() throws Exception {
		IRetryCondition oCond = Mockito.mock(IRetryCondition.class);
		IBackoffStrategy oBackoff = Mockito.mock(IBackoffStrategy.class);
		when(oCond.shouldRetry(any(Callable.class), anyInt(), any(Exception.class))).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock pInvocation) throws Throwable {
				int oExecs = (Integer) pInvocation.getArguments()[1];
				if (oExecs < 3) return true;
				return false;
			}
		});
		BaseRetryStrategy oStg = new BaseRetryStrategy(oCond, oBackoff);
		try {
			oStg.executeAndRetry(new Callable<Integer>() {
				private int mExecs = 0;
				@Override
				public Integer call() throws Exception {
					mExecs++;
					if (mExecs < 3) throw new IOException();
					return mExecs;
				}
			});
		} catch (Exception e) {}
		verify(oCond, times(2)).shouldRetry(any(Callable.class), anyInt(), any(Exception.class));
		verify(oBackoff, times(2)).pauseBeforeNextRetry(any(Callable.class), anyInt(), any(Exception.class));
	}
}
