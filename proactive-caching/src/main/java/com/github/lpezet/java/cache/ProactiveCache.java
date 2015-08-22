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
		
		public CacheEntry<T> get(String pKey) {
			return mKeyToCacheEntryMap.get(pKey);
		}
		
		public synchronized void remove(String pKey) {
			CacheEntry<T> oMapped = mKeyToCacheEntryMap.get(pKey);
			if (oMapped == null) return;
			mCacheEntries.remove(oMapped);
		}
		
		public synchronized Set<CacheEntry<T>> getCacheEntries() {
			return mCacheEntries;
		}
		
		public synchronized void clear() {
			mKeyToCacheEntryMap.clear();
			mCacheEntries.clear();
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
				if (!oNow.isAfter( e.getLastRefreshed())) break;
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
		CacheEntry<T> oEntry = mCacheEntries.get( pKey );
		CacheItem<T> oCached = mImpl.get(pKey);
		if (oEntry != null && oCached != null) {
			return new BasicCacheItem<CommandAugmentedItem<T>>(pKey, new BasicCommandAugmentedItem<T>(oCached.getValue(), oEntry.getCommand()));
		} else {
			//TODO: Log!!!!!
			return null;
		}
		// why???
		//CacheItem<T> oCached = mImpl.get(pKey);
		//return new BasicCacheItem<CommandAugmentedItem<T>>(pKey, new BasicCommandAugmentedItem<T>(oCached.getValue(), null));
	}

	public void remove(String pKey) {
		mImpl.remove(pKey);
		mCacheEntries.remove(pKey);
	}
	
	@Override
	public void clear() {
		mImpl.clear();
		mCacheEntries.clear();
	}
	
	@Override
	public boolean isEmpty() {
		return mImpl.isEmpty();
	}
	
	@Override
	public int size() {
		return mImpl.size();
	}

}
