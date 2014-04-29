/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.Collection;

/**
 * @author luc
 *
 */
public interface IResultMerger<T> {

	public T merge(Collection<T> pSource);
	
}
