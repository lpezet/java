/**
 * 
 */
package com.github.lpezet.java.cache;

/**
 * @author luc
 *
 */
public class BasicCacheItem<T> implements CacheItem<T> {
	
	private String mKey;
	private T mValue;
	
	public BasicCacheItem() {
	}
	
	public BasicCacheItem(String pKey, T pValue) {
		setKey(pKey);
		setValue(pValue);
	}

	public String getKey() {
		return mKey;
	}

	public void setKey(String pKey) {
		mKey = pKey;
	}

	public T getValue() {
		return mValue;
	}

	public void setValue(T pValue) {
		mValue = pValue;
	}

}
