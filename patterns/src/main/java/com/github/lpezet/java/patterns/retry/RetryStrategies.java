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

/**
 * @author luc
 * 
 */
public class RetryStrategies {

	private static final IBackoffStrategy DEFAULT_BACKOFF_STRATEGY = new ExponentialBackoffStrategy();
	private static final IRetryCondition DEFAULT_RETRY_CONDITION = new BasicRetryCondition(IOException.class, 3);

	public static IRetryStrategy defaultBackoffIORetryStrategy() {
		return new BaseRetryStrategy(DEFAULT_RETRY_CONDITION, DEFAULT_BACKOFF_STRATEGY);
	}
	
	public static IRetryStrategy simple(IRetryCondition pCondition, IBackoffStrategy pBackoffStrategy) {
		return new BaseRetryStrategy(pCondition, pBackoffStrategy);
	}

}
