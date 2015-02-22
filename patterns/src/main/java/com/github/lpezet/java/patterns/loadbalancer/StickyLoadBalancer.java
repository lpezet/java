/**
 * 
 */
package com.github.lpezet.java.patterns.loadbalancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Luc Pezet
 *
 */
public class StickyLoadBalancer<S,T> extends BaseLoadBalancer<T> {

	private ThreadLocal<S> mParametersHolder;
	private ILoadBalancer<T> mLoadBalancer;
	private int mNumberOfHashGroups = 64 * 1024;
	private final Map<Object, T> mStickyMap;// = ;
	
	public StickyLoadBalancer(ILoadBalancer<T> pLoadBalancer, ThreadLocal<S> pParametersHolder, Map<Object, T> pStickyMap) {
		mLoadBalancer = pLoadBalancer;
		mParametersHolder = pParametersHolder;
		mStickyMap = (pStickyMap == null) ? new HashMap<Object, T>() : pStickyMap;
	}
	
	@Override
	protected T pickResource() {
		T oResult = null;
		S oParams = mParametersHolder.get();
		Object oKey = getKey(oParams);
		synchronized (mStickyMap) {
			oResult = mStickyMap.get( oKey );
			if (oResult == null) {
				oResult = mLoadBalancer.next();
				mStickyMap.put(oKey, oResult);
			}
		}
		return oResult;
	}
	
	protected Object getKey(S pParams) {
		 int oResult = 37;
		 if (pParams != null) {
			 oResult = pParams.hashCode();
		 }
		 if (mNumberOfHashGroups > 0) {
			 oResult = oResult % mNumberOfHashGroups;
		 }
		 return oResult;
	}
	
	@Override
	public boolean hasNext() {
		return mLoadBalancer.hasNext();
	}
	
	public int getNumberOfHashGroups() {
		return mNumberOfHashGroups;
	}
	
	public void setNumberOfHashGroups(int pNumberOfHashGroups) {
		mNumberOfHashGroups = pNumberOfHashGroups;
	}
	
	
}
