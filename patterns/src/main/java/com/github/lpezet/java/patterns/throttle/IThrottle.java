/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

import java.util.concurrent.Callable;

/**
 * @author Luc Pezet
 *
 */
public interface IThrottle<T> {

	public T throttle(final Callable<T> pCallable) throws Exception;
	
}
