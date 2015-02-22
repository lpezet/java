/**
 * 
 */
package com.github.lpezet.java.patterns.loadbalancer;

/**
 * @author Luc Pezet
 *
 */
public abstract class BaseLoadBalancer<T> implements ILoadBalancer<T> {

	@Override
	public abstract boolean hasNext();
	
	@Override
	public T next() {
		T oResource = pickResource();
		if (oResource == null)
			throw new IllegalStateException("No resource found to load balance to.");
		return oResource;
	}
	
	protected abstract T pickResource();
}
