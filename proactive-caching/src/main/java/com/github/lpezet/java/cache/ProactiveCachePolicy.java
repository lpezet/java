/**
 * 
 */
package com.github.lpezet.java.cache;

/**
 * @author luc
 *
 */
public class ProactiveCachePolicy {

	private long mTimeToLiveInMillis = 1000;
	
	public long getTimeToLiveInMillis() {
		return mTimeToLiveInMillis;
	}
	
	public void setTimeToLiveInMillis(long pTimeToLiveInMillis) {
		mTimeToLiveInMillis = pTimeToLiveInMillis;
	}
}
