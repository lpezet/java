/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

/**
 * @author luc
 *
 */
public interface IWorker<W extends IWork, R extends IResult> {

	public R perform(W pWork) throws Exception;
	
}
