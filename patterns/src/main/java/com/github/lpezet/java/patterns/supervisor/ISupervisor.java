/**
 * 
 */
package com.github.lpezet.java.patterns.supervisor;

import java.util.concurrent.Callable;

/**
 * @author Luc Pezet
 *
 */
public interface ISupervisor<T> {

	public T supervise(final Callable<T> pCallable) throws Exception;
	
}
