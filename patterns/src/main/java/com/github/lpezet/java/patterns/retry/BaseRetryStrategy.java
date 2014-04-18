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

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luc
 *
 */
public class BaseRetryStrategy implements IRetryStrategy {
	
	private Logger mLogger = LoggerFactory.getLogger(BaseRetryStrategy.class);
	
	private IRetryCondition mRetryCondition;
	private IBackoffStrategy mBackoffStrategy;
	
	public BaseRetryStrategy(IRetryCondition pRetryCondition, IBackoffStrategy pBackoffStrategy) {
		mRetryCondition = pRetryCondition;
		mBackoffStrategy = pBackoffStrategy;
	}
	
	@Override
	public <T> T executeAndRetry(Callable<T> pCallable) throws Exception {
		int oExecutions = 0;
		while(true) {
			oExecutions++;
			try {
				if (mLogger.isTraceEnabled() && oExecutions > 1) mLogger.trace("Retry #{}", oExecutions-1);
				return pCallable.call();
			} catch (Exception e) {
				if (mLogger.isTraceEnabled()) mLogger.trace("Got an exception: {}...", e.getMessage());
				if (!mRetryCondition.shouldRetry(pCallable, oExecutions, e)) {
					if (mLogger.isTraceEnabled()) mLogger.trace("...no retrying.");
					throw e;
				}
				if (mLogger.isTraceEnabled()) mLogger.trace("...retrying. Pausing before next retry...");
				mBackoffStrategy.pauseBeforeNextRetry(pCallable, oExecutions, e);
			}
		}
	}

}
