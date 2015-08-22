/**
 * 
 */
package com.github.lpezet.java.cache.util;

/**
 * @author Luc Pezet
 *
 */
public interface IWorker<W,R> {

	public R perform(W pWork);
	
}
