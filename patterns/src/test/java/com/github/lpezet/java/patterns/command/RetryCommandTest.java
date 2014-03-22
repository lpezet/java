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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.github.lpezet.java.patterns.retry.BaseRetryStrategy;
import com.github.lpezet.java.patterns.retry.IBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IRetryCondition;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;

/**
 * @author luc
 *
 */
public class RetryCommandTest {
	
	private static class NoOpBackoffStrategy implements IBackoffStrategy {
		@Override
		public <T> void pauseBeforeNextRetry(ICommand<T> pCommand, int pExecutions, Throwable pLastException) {
			// nop
		}
	}
	
	private static class RetryCondition implements IRetryCondition {
		
		private Class<? extends Throwable> mRetryExceptionClass;
		private int mMaxExecutions;
		
		public RetryCondition(int pMaxExecutions, Class<? extends Throwable> pRetryExceptionClass) {
			mMaxExecutions = pMaxExecutions;
			mRetryExceptionClass = pRetryExceptionClass;
		}
		
		@Override
		public <T> boolean shouldRetry(ICommand<T> pCommand, int pExecutions, Throwable pException) {
			return pExecutions <= mMaxExecutions && mRetryExceptionClass.isAssignableFrom(pException.getClass());
		}
	}
	
	@Test
	public void noRetry() throws Exception {
		final AtomicInteger oExecutions = new AtomicInteger(0);
		ICommand<Void> oTestCommand = new ICommand<Void>() {
			@Override
			public Void execute() throws Exception {
				oExecutions.incrementAndGet();
				return null;
			}
		};
		RetryCondition oRetryCondition = new RetryCondition(3, IOException.class);
		IBackoffStrategy oBackoffStrategy = new NoOpBackoffStrategy();
		IRetryStrategy oRetryStrategy = new BaseRetryStrategy(oRetryCondition, oBackoffStrategy);
		
		RetryCommand<Void> oRetry = new RetryCommand<Void>(oTestCommand, oRetryStrategy);
		
		oRetry.execute();
		
		assertEquals(1, oExecutions.get());
	}

	@Test
	public void retry() throws Exception {
		final AtomicInteger oExecutions = new AtomicInteger(0);
		ICommand<Void> oTestCommand = new ICommand<Void>() {
			@Override
			public Void execute() throws Exception {
				int oExecs = oExecutions.incrementAndGet();
				if (oExecs <= 2) throw new IOException();
				return null;
			}
		};
		
		RetryCondition oRetryCondition = new RetryCondition(3, IOException.class);
		IBackoffStrategy oBackoffStrategy = new NoOpBackoffStrategy();
		IRetryStrategy oRetryStrategy = new BaseRetryStrategy(oRetryCondition, oBackoffStrategy);
		
		RetryCommand<Void> oRetry = new RetryCommand<Void>(oTestCommand, oRetryStrategy);
		
		oRetry.execute();
		
		assertEquals(3, oExecutions.get());
	}
	
	@Test(expected=IOException.class)
	public void retryAndFail() throws Exception {
		final AtomicInteger oExecutions = new AtomicInteger(0);
		ICommand<Void> oTestCommand = new ICommand<Void>() {
			@Override
			public Void execute() throws Exception {
				oExecutions.incrementAndGet();
				throw new IOException(); 
			}
		};
		
		RetryCondition oRetryCondition = new RetryCondition(3, IOException.class);
		IBackoffStrategy oBackoffStrategy = new NoOpBackoffStrategy();
		IRetryStrategy oRetryStrategy = new BaseRetryStrategy(oRetryCondition, oBackoffStrategy);
		
		RetryCommand<Void> oRetry = new RetryCommand<Void>(oTestCommand, oRetryStrategy);
		try {
			oRetry.execute();
		} finally {
			assertEquals(4, oExecutions.get());
		}
	}

}
