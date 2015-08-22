/**
 * 
 */
package com.github.lpezet.java.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Luc Pezet
 *
 */
public class InMemoryCache<T> implements Cache<T> {
	
	private Map<String, T> mStore = new HashMap<String, T>();

	@Override
	public void put(CacheItem<T> pItem) {
		mStore.put( pItem.getKey(), pItem.getValue() );
	}

	@Override
	public CacheItem<T> get(String pKey) {
		T v = mStore.get(pKey);
		/*
		if (v == null)
			System.out.println("[" + pKey + "] MISS");
		else
			System.out.println("[" + pKey + "] HIT: " + v);
			*/
		return new BasicCacheItem<T>(pKey, v);
	}

	@Override
	public void remove(String pKey) {
		mStore.remove( pKey );
	}

	@Override
	public void clear() {
		mStore.clear();
	}

	@Override
	public int size() {
		return mStore.size();
	}

	@Override
	public boolean isEmpty() {
		return mStore.isEmpty();
	}

	
}
