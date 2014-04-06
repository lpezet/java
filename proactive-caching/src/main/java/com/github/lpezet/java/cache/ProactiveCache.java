/**
 * 
 */
package com.github.lpezet.java.cache;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;

import org.joda.time.Instant;
import org.joda.time.Interval;

/**
 * @author luc
 *
 */
public class ProactiveCache<T> implements Cache<CommandAugmentedItem<T>> {
	
	private static class CacheEntryComparator<T> implements Comparator<CacheEntry<T>> {

		@Override
		public int compare(CacheEntry<T> pO1, CacheEntry<T> pO2) {
			if (pO1 == null && pO2 != null) return -1;
			if (pO1 != null && pO2 == null) return 1;
			if (pO1.getKey().equals(pO2.getKey())) {
				return pO1.getLastRefreshed().compareTo(pO2.getLastRefreshed());
			}
			return pO1.getKey().compareTo(pO2.getKey());
		}
		
	}
	
	private static class CacheEntries<T> {
		
		private Map<String, CacheEntry<T>> mKeyToCacheEntryMap = new HashMap<String, CacheEntry<T>>();
		private Set<CacheEntry<T>> mCacheEntries = new ConcurrentSkipListSet<ProactiveCache.CacheEntry<T>>(new CacheEntryComparator<T>());
		
		public synchronized void add(CacheEntry<T> pEntry) {
			CacheEntry<T> oAddedAlready = mKeyToCacheEntryMap.get(pEntry.getKey());
			if (oAddedAlready != null) {
				// either replace or remove.
				// here: do nothing.
			} else {
				mKeyToCacheEntryMap.put(pEntry.getKey(), pEntry);
				mCacheEntries.add(pEntry);
			}
		}
		
		public void remove(String pKey) {
			CacheEntry<T> oMapped = mKeyToCacheEntryMap.get(pKey);
			if (oMapped == null) return;
			mCacheEntries.remove(oMapped);
		}
		
		public Set<CacheEntry<T>> getCacheEntries() {
			return mCacheEntries;
		}
		
	}
	
	private static class CacheEntry<T> {
		
		private Instant mLastRefreshed;
		private volatile boolean mRefreshing = false;
		private Command<T> mCommand;
		private String mKey;
		
		public CacheEntry(String pKey, Command<T> pCommand) {
			mLastRefreshed = Instant.now();
			mCommand = pCommand;
			mKey = pKey;
		}
		
		public Command<T> getCommand() {
			return mCommand;
		}
		
		public Instant getLastRefreshed() {
			return mLastRefreshed;
		}
		
		public void setLastRefreshed(Instant pLastRefreshed) {
			mLastRefreshed = pLastRefreshed;
		}
		
		public String getKey() {
			return mKey;
		}
		
		public void setRefreshing(boolean pRefreshing) {
			mRefreshing = pRefreshing;
		}
		
		public boolean isRefreshing() {
			return mRefreshing;
		}
		
	}
	
	private class ProActiveCachingTask extends TimerTask {
		@Override
		public void run() {
			Instant oNow = Instant.now();
			// Go through data to be expired
			for (CacheEntry<T> e : mCacheEntries.getCacheEntries()) {
				Interval i = new Interval(e.getLastRefreshed(), oNow);
				if (i.toDurationMillis() <= mPolicy.getTimeToLiveInMillis()) {
					break; // Set is ordered so as soon as we get to an entry that has not expired, we quit.
				}
				// If it's already running
				if (e.isRefreshing()) continue;
				e.setRefreshing(true);
				Command<T> oCmd = e.getCommand();
				// Synchronous fashion
				T oNewValue = oCmd.execute();
				e.setLastRefreshed(Instant.now());
				doPut(e.getKey(), oNewValue);
				e.setRefreshing(false);
			}
		}
	}
	
	private ProactiveCachePolicy mPolicy;
	private Cache<T> mImpl;
	private Timer mTimer;
	private CacheEntries<T> mCacheEntries = new CacheEntries<T>();
	
	public ProactiveCache(ProactiveCachePolicy pPolicy) {
		mTimer = new Timer(true);
		mTimer.schedule(new ProActiveCachingTask(), 0, 100);
		mPolicy = pPolicy;
	}
	
	public void setImpl(Cache<T> pImpl) {
		mImpl = pImpl;
	}

	public void put(CacheItem<CommandAugmentedItem<T>> pItem) {
		CacheEntry<T> oEntry = new CacheEntry<T>(pItem.getKey(), pItem.getValue().getCommand());
		mCacheEntries.add(oEntry);
		doPut(pItem.getKey(), pItem.getValue().getValue());
	}

	private void doPut(String pKey, T pValue) {
		mImpl.put(new BasicCacheItem<T>(pKey, pValue));
	}

	public CacheItem<CommandAugmentedItem<T>> get(String pKey) {
		CacheItem<T> oCached = mImpl.get(pKey);
		return new BasicCacheItem<CommandAugmentedItem<T>>(pKey, new BasicCommandAugmentedItem<T>(oCached.getValue(), null));
	}

	public void remove(String pKey) {
		mImpl.remove(pKey);
	}

}
