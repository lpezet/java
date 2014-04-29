/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

/**
 * @author luc
 *
 */
public interface IWorker<W, R> {

	public R perform(W pWork) throws Exception;
	
}
