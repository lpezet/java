/**
 * 
 */
package com.github.lpezet.java.cache;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author luc
 * 
 */
public class ProactiveCacheTest {

	static class Producer<T> extends Thread {

		private Cache<T> mCache;
		private CacheItem<T> mItem;
		private String mRandomKey;

		public Producer(Cache<T> pCache, CacheItem<T> pItem, String pRandomKey) {
			mCache = pCache;
			mItem = pItem;
			mRandomKey = pRandomKey;
		}

		@Override
		public void run() {
			mCache.put(mItem);
			mCache.get(mRandomKey);
		}
	}

	@Test
	public void doIt() throws Exception {
		Cache<String> oInMemoryCache = new Cache<String>() {

			private Map<String, String> mCache = new HashMap<String, String>();

			@Override
			public void remove(String pKey) {
				mCache.remove(pKey);
			}

			@Override
			public void put(CacheItem<String> pItem) {
				mCache.put(pItem.getKey(), pItem.getValue());
			}

			@Override
			public CacheItem<String> get(String pKey) {
				String v = mCache.get(pKey);
				if (v == null) System.out.println("[" + pKey + "] MISS");
				else System.out.println("[" + pKey + "] HIT");
				return new BasicCacheItem<String>(pKey, v);
			}
		};
		ProactiveCachePolicy oPolicy = new ProactiveCachePolicy();
		oPolicy.setTimeToLiveInMillis(500);
		ProactiveCache<String> oCache = new ProactiveCache<String>(oPolicy);
		oCache.setImpl(oInMemoryCache);

		//List<BasicCacheItem<CommandAugmentedItem<String>>> oRandomItems = new ArrayList<BasicCacheItem<CommandAugmentedItem<String>>>();
		String oPreviousKey = "fake";
		List<Thread> oThreads = new ArrayList<Thread>();
		for (int i = 0; i < 200; i++) {
			String oKey = nextString();
			String oValue = nextString();
			BasicCacheItem<CommandAugmentedItem<String>> oItem = newItem(oKey, oValue);
			//oRandomItems.add(oItem);	
			Producer oProducer = new Producer(oCache, oItem, oPreviousKey);
			oThreads.add(oProducer);
			oPreviousKey = oKey;
		}
		
		for (Thread t : oThreads) t.start();
		
		// oCache.put(oCI);

		Thread.sleep(5000);
	}

	private SecureRandom random = new SecureRandom();

	public String nextString() {
		return new BigInteger(130, random).toString(32);
	}

	private static BasicCacheItem<CommandAugmentedItem<String>> newItem(
			final String pKey, final String pValue) {
		Command<String> oHelloWorldCommand = new Command<String>() {

			@Override
			public String execute() {
				System.out.println(Thread.currentThread().getName()
						+ ": [" + pKey +  "] executing...");
				return pValue + System.currentTimeMillis();
			}
		};
		BasicCommandAugmentedItem<String> oBCI = new BasicCommandAugmentedItem<String>(
				pValue, oHelloWorldCommand);
		return new BasicCacheItem<CommandAugmentedItem<String>>(pKey, oBCI);
	}
}
