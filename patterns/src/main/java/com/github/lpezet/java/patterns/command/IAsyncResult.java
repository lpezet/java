/**
 * 
 */
package com.github.lpezet.java.patterns.command;

import java.util.concurrent.TimeUnit;

/**
 * @author luc
 *
 */
public interface IAsyncResult<T> {

	
	/**
	 * Set "method" to be called when result is available.
	 * Even if set AFTER result is available, the pCallback.callback() will be called.
	 * 
	 * @param pCallback
	 * @throws Exception
	 */
	public void setCallback(Callback<T> pCallback) throws Exception;
	
	/**
	 * Wait for result to be available and return them. 
	 * Same as Future<T>.get().
	 * 
	 * @return
	 * @throws Exception
	 */
	public T get() throws Exception;
	
	/**
	 * Wait for result to be available up until timeout specified.
	 * Same as Future<T>.get(long, TimeUnit). 
	 * 
	 * @param pTimeout
	 * @param pTimeUnit
	 * @return
	 * @throws Exception
	 */
	public T get(long pTimeout, TimeUnit pTimeUnit) throws Exception;
}
