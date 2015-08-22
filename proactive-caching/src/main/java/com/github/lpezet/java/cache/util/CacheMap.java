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
/**
 * 
 */
package com.github.lpezet.java.cache.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.github.lpezet.java.cache.Cache;
import com.github.lpezet.java.cache.CacheItem;

/**
 * @author Luc Pezet
 *
 */
public abstract class CacheMap<V> implements Map<String, V>, Cache<V> {
	
	private Cache<V> mCacheImpl;
	private Object mLock = new Object();
	//private static final Object DUMMY = new Object();
	
	public CacheMap(Cache<V> pCacheImpl) {
		mCacheImpl = pCacheImpl;
	}

	@Override
	public void put(CacheItem<V> pItem) {
		synchronized (mLock) {
			mCacheImpl.put(pItem);
		}
	}

	@Override
	public CacheItem<V> get(String pKey) {
		return mCacheImpl.get(pKey);
	}

	@Override
	public void remove(String pKey) {
		synchronized (mLock) {
			mCacheImpl.remove(pKey);
		}
	}

	@Override
	public int size() {
		return mCacheImpl.size();
	}

	@Override
	public boolean isEmpty() {
		return mCacheImpl.isEmpty();
	}

	@Override
	public boolean containsKey(Object pKey) {
		//return mCacheImpl.containsKey(pKey == null ? null : pKey.toString());
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object pValue) {
		//return false; // Not supported
		throw new UnsupportedOperationException();
	}

	@Override
	public V get(Object pKey) {
		if (pKey == null) return null;
		synchronized (mLock) {
			CacheItem<V> oCached = mCacheImpl.get(pKey.toString());
			if (oCached != null) return oCached.getValue();
		}
		return null;
	}

	@Override
	public V put(String pKey, V pValue) {
		synchronized (mLock) {
			CacheItem<V> oCI = createCacheItem(pKey, pValue);
			if (oCI != null) {
				mCacheImpl.put(oCI);
			}
		}
		return pValue;
	}

	@Override
	public V remove(Object pKey) {
		if (pKey == null) return null;
		V oValue = get(pKey);
		synchronized (mLock) {
			mCacheImpl.remove(pKey.toString());
		}
		return oValue;
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> pMap) {
		synchronized (mLock) {
			for (Entry<? extends String, ? extends V> e : pMap.entrySet()) {
				CacheItem<V> oCI = createCacheItem(e.getKey(), e.getValue());
				mCacheImpl.put(oCI);
			}
		}
	}

	@Override
	public void clear() {
		mCacheImpl.clear();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
	
	protected abstract CacheItem<V> createCacheItem(String pKey, V pValue);
}
