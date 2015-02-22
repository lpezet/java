/**
 * 
 */
package com.github.lpezet.java.patterns.loadbalancer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Luc Pezet
 *
 */
public abstract class SimpleListLoadBalancer<T> extends BaseLoadBalancer<T> {

	private List<T> mResources = new CopyOnWriteArrayList<T>();
	protected Object mLock = new Object();
	
	@Override
	public boolean hasNext() {
		return ! mResources.isEmpty();
	}
	
	public void addResource(T... pResources) {
		synchronized (mLock) {
			for (T r : pResources)
				mResources.add(r);
		}
	}
	
	public void removeResource(T pResource) {
		synchronized (mLock) {
			mResources.remove(pResource);
		}
	}
	
	public List<T> getResources() {
		return mResources;
	}
}
