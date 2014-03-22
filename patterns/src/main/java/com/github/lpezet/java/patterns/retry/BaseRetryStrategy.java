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

import com.github.lpezet.java.patterns.command.ICommand;

/**
 * @author luc
 *
 */
public class BaseRetryStrategy implements IRetryStrategy {
	
	private IRetryCondition mRetryCondition;
	private IBackoffStrategy mBackoffStrategy;
	
	public BaseRetryStrategy(IRetryCondition pRetryCondition, IBackoffStrategy pBackoffStrategy) {
		mRetryCondition = pRetryCondition;
		mBackoffStrategy = pBackoffStrategy;
	}
	
	@Override
	public <T> T executeAndRetry(ICommand<T> pCommand) throws Exception {
		int oExecutions = 0;
		while(true) {
			oExecutions++;
			try {
				return pCommand.execute();
			} catch (Exception e) {
				if (!mRetryCondition.shouldRetry(pCommand, oExecutions, e)) throw e;
				mBackoffStrategy.pauseBeforeNextRetry(pCommand, oExecutions, e);
			}
		}
	}

}
