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
package com.github.lpezet.java.patterns.worker;

import java.util.concurrent.TimeUnit;

/**
 * @author luc
 *
 */
public interface IAsyncResult<R> {

	/**
	 * Set "method" to be called when result is available.
	 * Even if set AFTER result is available, the pCallback.callback() will be called.
	 * 
	 * @param pCallback
	 * @throws Exception
	 */
	public void setCallback(Callback<R> pCallback) throws Exception;
	
	/**
	 * Wait for result to be available and return them. 
	 * Same as Future<T>.get().
	 * 
	 * @return
	 * @throws Exception
	 */
	public R get() throws Exception;
	
	/**
	 * Wait for result to be available up until timeout specified.
	 * Same as Future<T>.get(long, TimeUnit). 
	 * 
	 * @param pTimeout
	 * @param pTimeUnit
	 * @return
	 * @throws Exception
	 */
	public R get(long pTimeout, TimeUnit pTimeUnit) throws Exception;
}
