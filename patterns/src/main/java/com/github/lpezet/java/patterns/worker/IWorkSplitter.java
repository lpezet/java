/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.Collection;

/**
 * @author luc
 *
 */
public interface IWorkSplitter<T> {

	public Collection<T> split(T pWork);
	
}
