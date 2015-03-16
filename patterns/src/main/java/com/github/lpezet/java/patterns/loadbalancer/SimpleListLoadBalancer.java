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
