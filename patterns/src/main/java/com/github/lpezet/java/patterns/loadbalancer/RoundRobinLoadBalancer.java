/**
 * 
 */
package com.github.lpezet.java.patterns.loadbalancer;

/**
 * @author Luc Pezet
 *
 */
public class RoundRobinLoadBalancer<T> extends SimpleListLoadBalancer<T> {

	private int mCounter = -1;
	
	@Override
	protected T pickResource() {
		synchronized (mLock) {
			int oSize = getResources().size();
			if (++mCounter >= oSize) {
				mCounter = 0;
			}
			return getResources().get(mCounter);
		}
	}
}
