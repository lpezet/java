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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.github.lpezet.java.cache.ProactiveCacheAdapter.CommandFactory;

/**
 * @author luc
 * 
 */
@Ignore
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
			try {
				mCache.put(mItem);
				Thread.sleep(160);
				CacheItem<T> oValue = mCache.get(mRandomKey);
				System.out.println(Thread.currentThread().getName()
						+ ": value=" + oValue);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	static class MyCommandFactory implements CommandFactory<String> {

		@Override
		public CommandAugmentedItem<String> newCommand(
				final CacheItem<String> pItem) {
			Command<String> oHelloWorldCommand = new Command<String>() {

				@Override
				public String execute() {
					if (ExtendedCacheItem.class.isInstance(pItem)) {
						// use extra parameters to do something in the
						// command...
					}
					System.out.println(Thread.currentThread().getName() + ": ["
							+ pItem.getKey() + "] executing...");
					return pItem.getValue() + System.currentTimeMillis();
				}
			};
			return new BasicCommandAugmentedItem<String>(pItem.getValue(),
					oHelloWorldCommand);
		}
	}

	static class ExtendedCacheItem<T> extends BasicCacheItem<T> {
		private String mId1;
		private String mId2;

		public ExtendedCacheItem(String pKey, T pValue, String pId1, String pId2) {
			super(pKey, pValue);
			mId1 = pId1;
			mId2 = pId2;
		}

		public String getId1() {
			return mId1;
		}

		public String getId2() {
			return mId2;
		}
	}
	
	static class SimpleCache implements Cache<String> {
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
			if (v == null)
				System.out.println("[" + pKey + "] MISS");
			else
				System.out.println("[" + pKey + "] HIT: " + v);
			return new BasicCacheItem<String>(pKey, v);
		}
		
		@Override
		public void clear() {
			mCache.clear();
		}
		
		@Override
		public boolean isEmpty() {
			return mCache.isEmpty();
		}
		
		@Override
		public int size() {
			return mCache.size();
		}
	}
	
	/**
	 * Using ProactiveCache directly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void directUse() throws Exception {
		ProactiveCachePolicy oPolicy = new ProactiveCachePolicy();
		oPolicy.setTimeToLiveInMillis(300);
		ProactiveCache<String> oCache = new ProactiveCache<String>(oPolicy);
		oCache.setImpl(new SimpleCache());
		
		CacheItem<CommandAugmentedItem<String>> oCacheItem = oCache.get("missing_key");
		assertNull( oCacheItem );
		//System.out.println(oCacheItem.getValue());
	}

	/**
	 * This case is when the client cache has been "adapted" to provide pro-active functionality.
	 * The client code keeps using the client cache as if nothing changed.
	 * 
	 * This test shows how a new cache item is updated over time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void clientCacheUse() throws Exception {
		// Actual cache
		Cache<String> oInMemoryCache = new SimpleCache();

		// Augmentation
		ProactiveCachePolicy oPolicy = new ProactiveCachePolicy();
		oPolicy.setTimeToLiveInMillis(100);
		ProactiveCache<String> oCache = new ProactiveCache<String>(oPolicy);
		oCache.setImpl(oInMemoryCache);

		// Command Factory
		CommandFactory<String> oCommandFactory = new MyCommandFactory();

		// Adapter for actual cache clients
		ProactiveCacheAdapter<String> oAdatper = new ProactiveCacheAdapter<String>( oCache, oCommandFactory );
		
		String oKey = "efjasfhfsfhhf";
		CacheItem<String> oValue = oInMemoryCache.get(oKey);
		assertNotNull( oValue );
		assertNull( oValue.getValue() );
		
		oAdatper.put(new ExtendedCacheItem<String>(oKey, "Hello", "somethinghere", "somethingthere"));
		oValue = oInMemoryCache.get(oKey);
		assertNotNull( oValue );
		assertNotNull( oValue.getValue() );
		Thread.sleep(200);
		
		oValue = oInMemoryCache.get(oKey);
		assertNotNull( oValue );
		assertNotNull( oValue.getValue() );
		Thread.sleep(200);
		
		oValue = oInMemoryCache.get(oKey);
		assertNotNull( oValue );
		assertNotNull( oValue.getValue() );
		
	}

	/**
	 * This example shows how to augment an existing cache with commands to
	 * refresh values. Client code will submit items to be cached and commands
	 * rely either solely on the key/value of the item to cache or the extra
	 * fields from a custom implementation of CacheItem&lt;T&gt; (like here).
	 * 
	 * @throws Exception
	 */
	@Test
	public void adapter() throws Exception {
		// Actual cache
		Cache<String> oInMemoryCache = new SimpleCache();

		// Augmentation
		ProactiveCachePolicy oPolicy = new ProactiveCachePolicy();
		oPolicy.setTimeToLiveInMillis(100);
		ProactiveCache<String> oCache = new ProactiveCache<String>(oPolicy);
		oCache.setImpl(oInMemoryCache);

		// Command Factory
		CommandFactory<String> oCommandFactory = new MyCommandFactory();

		// Adapter for actual cache clients
		ProactiveCacheAdapter<String> oAdatper = new ProactiveCacheAdapter<String>(
				oCache, oCommandFactory);
		String oPreviousKey = "fake";
		List<Thread> oThreads = new ArrayList<Thread>();
		for (int i = 0; i < 10; i++) {
			String oKey = nextString();
			String oValue = nextString();
			CacheItem<String> oItem = new ExtendedCacheItem<String>(oKey,
					oValue, "somethinghere", "somethingthere");
			Producer<String> oProducer = new Producer<String>(oAdatper, oItem,
					oPreviousKey);
			oThreads.add(oProducer);
			oPreviousKey = oKey;
		}

		for (Thread t : oThreads)
			t.start();

		// oCache.put(oCI);

		Thread.sleep(500);
	}

	private SecureRandom random = new SecureRandom();

	public String nextString() {
		return new BigInteger(130, random).toString(32);
	}
}
