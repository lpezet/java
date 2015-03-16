/**
 * The MIT License
 * Copyright (c) 2014 Luc Pezet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
