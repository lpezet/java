/**
 * 
 */
package com.github.lpezet.java.cache;

/**
 * @author luc
 *
 */
public interface CacheItem<T> {

	public String getKey();
	public void setKey(String pKey);
	public T getValue();
	public void setValue(T pValue);
}
