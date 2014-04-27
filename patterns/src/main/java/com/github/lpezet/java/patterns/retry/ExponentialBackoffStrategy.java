package com.github.lpezet.java.patterns.retry;

import java.util.concurrent.Callable;

/**
 * Inspired by: com.amazonaws.retry.PredefinedRetryPolicies
 * 
 * @see <a href="http://grepcode.com/file/repo1.maven.org/maven2/com.amazonaws/aws-java-sdk/1.6.7/com/amazonaws/retry/PredefinedRetryPolicies.java?av=f">PredifinedRetryPolicies @ AWS</a>
 * 
 * @author luc
 *
 */
public class ExponentialBackoffStrategy implements IBackoffStrategy {
	/** Base sleep time (milliseconds) for general exceptions. **/
	private static final int SCALE_FACTOR = 300;

	/** Maximum exponential back-off time before retrying a request */
	private static final int MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;
	
	private int mScaleFactor = SCALE_FACTOR;
	private int mMaxBackoffInMillis = MAX_BACKOFF_IN_MILLISECONDS;
	
	public ExponentialBackoffStrategy() {
	}
	
	public ExponentialBackoffStrategy(int pScaleFactor) {
		mScaleFactor = pScaleFactor;
	}
	
	public ExponentialBackoffStrategy(int pScaleFactor, int pMaxBackoffInMillis) {
		mScaleFactor = pScaleFactor;
		mMaxBackoffInMillis = pMaxBackoffInMillis;
	}
	
	@Override
	public <T> void pauseBeforeNextRetry(Callable<T> pCallable, int pExecutions, Throwable pLastException) {
		int oRetries = pExecutions - 1;
		long oDelay = delayBeforeNextRetry(pLastException, oRetries);
		try {
			Thread.sleep(oDelay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			// throw e; //TODO
		}
	}

	protected final long delayBeforeNextRetry(Throwable pException, int retries) {
		if (retries <= 0) return 0;

		int scaleFactor = mScaleFactor;

		long delay = (1 << retries) * scaleFactor;
		delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);

		return delay;
	}
}