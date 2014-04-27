/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.github.lpezet.java.patterns.retry.BaseRetryStrategy;
import com.github.lpezet.java.patterns.retry.IBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IRetryCondition;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;

/**
 * @author luc
 *
 */
public class RetryWorkerTest {

	private static class NoOpBackoffStrategy implements IBackoffStrategy {
		@Override
		public <T> void pauseBeforeNextRetry(Callable<T> pCallable, int pExecutions, Throwable pLastException) {
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
		public <T> boolean shouldRetry(Callable<T> pCallable, int pExecutions, Throwable pException) {
			return pExecutions <= mMaxExecutions && mRetryExceptionClass.isAssignableFrom(pException.getClass());
		}
	}
	
	private IRetryCondition mRetryCondition;
	private IBackoffStrategy mBackoffStrategy;
	private IRetryStrategy mRetryStrategy;
	
	@Before
	public void setup() {
		mRetryCondition = new RetryCondition(3, IOException.class);
		mBackoffStrategy = new NoOpBackoffStrategy();
		mRetryStrategy = new BaseRetryStrategy(mRetryCondition, mBackoffStrategy);
	}
	
	@Test
	public void noRetry() throws Exception {
		final AtomicInteger oExecutions = new AtomicInteger(0);
		IWorker<IWork, IResult> oTestWorker = new IWorker<IWork, IResult>() {
			@Override
			public IResult perform(IWork pWork) throws Exception {
				oExecutions.incrementAndGet();
				return null;
			}
		};
		RetryWorker<IWork, IResult> oRetry = new RetryWorker<IWork, IResult>(oTestWorker, mRetryStrategy);
		oRetry.perform(null);
		assertEquals(1, oExecutions.get());
	}

	@Test
	public void retry() throws Exception {
		final AtomicInteger oExecutions = new AtomicInteger(0);
		IWorker<IWork, IResult> oTestWorker = new IWorker<IWork, IResult>() {
			@Override
			public IResult perform(IWork pWork) throws Exception {
				int oExecs = oExecutions.incrementAndGet();
				if (oExecs <= 2) throw new IOException("Throwing exception under 3 executions. This is for testing purposes.");
				return null;
			}
		};
		RetryWorker<IWork, IResult> oRetry = new RetryWorker<IWork, IResult>(oTestWorker, mRetryStrategy);
		oRetry.perform(null);
		assertEquals(3, oExecutions.get());
	}
	
	@Test(expected=IOException.class)
	public void retryAndFail() throws Exception {
		final AtomicInteger oExecutions = new AtomicInteger(0);
		IWorker<IWork, IResult> oTestWorker = new IWorker<IWork, IResult>() {
			@Override
			public IResult perform(IWork pWork) throws Exception {
				oExecutions.incrementAndGet();
				throw new IOException("Throwing exception all the time. This is for testing purposes."); 
			}
		};
		RetryWorker<IWork, IResult> oRetry = new RetryWorker<IWork, IResult>(oTestWorker, mRetryStrategy);
		try {
			oRetry.perform(null);
		} finally {
			assertEquals(4, oExecutions.get());
		}
	}
}
