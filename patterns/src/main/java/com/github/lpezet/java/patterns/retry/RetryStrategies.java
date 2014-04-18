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
package com.github.lpezet.java.patterns.retry;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author luc
 * 
 */
public class RetryStrategies {

	private static final IBackoffStrategy DEFAULT_BACKOFF_STRATEGY = new DefaultBackoffStrategy();
	private static final IRetryCondition DEFAULT_RETRY_CONDITION = new IORetryCondition();

	public static IRetryStrategy getDefaultBackoffIORetryStrategy() {
		return new BaseRetryStrategy(DEFAULT_RETRY_CONDITION,
				DEFAULT_BACKOFF_STRATEGY);
	}

	private static class IORetryCondition implements IRetryCondition {
		
		@Override
		public <T> boolean shouldRetry(Callable<T> pCallable, int pExecutions, Throwable pException) {
			return pExecutions <= 3
					&& (pException instanceof IOException || pException
							.getCause() instanceof IOException);
		}
	}

	private static class DefaultBackoffStrategy implements IBackoffStrategy {
		/** Base sleep time (milliseconds) for general exceptions. **/
		private static final int SCALE_FACTOR = 300;

		/** Maximum exponential back-off time before retrying a request */
		private static final int MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;
		
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

		protected final long delayBeforeNextRetry(Throwable pException,
				int retries) {
			if (retries <= 0) return 0;

			int scaleFactor = SCALE_FACTOR;

			long delay = (1 << retries) * scaleFactor;
			delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);

			return delay;
		}
	}

}
