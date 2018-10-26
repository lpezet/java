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
package com.github.lpezet.java.patterns.throttle;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

/**
 * See also https://github.com/spring-projects/spring-batch-admin/blob/master/spring-batch-admin-manager/src/main/java/org/springframework/batch/admin/util/ThrottledTaskExecutor.java
 * 
 * @author lucpezet
 *
 */
public class ThreadSafeThrottle<T> extends Throttle<T> {
	
	private final Semaphore mSemaphore;
	
	public ThreadSafeThrottle(IThrottleStrategy pStrategy) {
		super( pStrategy );
		mSemaphore = new Semaphore(1, true);
	}
	
	@Override
	public T throttleWithException(Callable<T> pCallable) throws Exception {
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw e;
		}
		
		try {
			return super.throttleWithException(pCallable);
		} finally {
			mSemaphore.release();
		}
		// Problem: if .call() is a really long operation, next thread will stay stuck waiting for a while.
		// Ideally (?), semaphore should be released AS SOON AS .call() is called.
		// Here's a problem is we release semaphore BEFORE .call():
		// 1. Thread1 acquires semaphore
		// 2. Thread2 waiting to acquire semaphore
		// 3. Thread3 waiting to acquire semaphore
		// 4. Thread1 not (or no longer) throttled.
		// 5. Thread1 releases semaphore (before calling .call())
		// 6. Thread2 acquires semaphore. Not (or no longer throttled). Release semaphore
		// 7. Thread3 acquires semaphore. Not (or no longer throttled). Release semaphore.
		// 8. We have 4 threads about to call .call() around the same time, defeating the purpose of the throttle.
		// Conclusion: best to release semaphore AFTER calling .call()
	}
	
}
