/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

/**
 * @author Luc Pezet
 *
 */
public interface IExceptionThrower<T> {

	public boolean shouldThrow(T pContext);
	
}
