/**
 * 
 */
package com.github.lpezet.java.patterns.activity;

/**
 * @author luc
 *
 */
public interface IActivity<T> {

	public T start(IContext pContext) throws Exception;
	
}
