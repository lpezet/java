/**
 * 
 */
package com.github.lpezet.java.cache;

/**
 * @author luc
 *
 */
public interface Cache<T> {

	public void put(CacheItem<T> pItem);
	
	public CacheItem<T> get(String pKey);
	
	public void remove(String pKey);
}
