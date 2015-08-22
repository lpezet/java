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
package com.github.lpezet.java.cache.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ch.qos.logback.core.net.server.Client;

import com.github.lpezet.java.cache.BasicCacheItem;
import com.github.lpezet.java.cache.Cache;
import com.github.lpezet.java.cache.CacheItem;
import com.github.lpezet.java.cache.ProactiveCachePolicy;

/**
 * @author Luc Pezet
 *
 */
public class ProactiveCacheHandlerTest {
	
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
	
	//Test
	public void doIt2() throws Exception {
		MyClient c = new MyClient();
		System.out.println("Saying hello....");
		c.sayHello("Bob");
		System.out.println("Calling void function....");
		c.dontCacheMe("Bobby");
		
		System.out.println("Saying hello...again...");
		c.sayHello("Bob");
	}
	
	@Test
	public void handle() throws Throwable {
		MyClient c = new MyClient();
		Method oSayHelloMethod = c.getClass().getMethod("sayHello", String.class);
		
		SimpleCache oBackend = new SimpleCache();
		ProactiveCachePolicy oPolicy = new ProactiveCachePolicy();
		oPolicy.setTimeToLiveInMillis(100);
		com.github.lpezet.java.cache.ProactiveCache<String> oCache = new com.github.lpezet.java.cache.ProactiveCache<String>(oPolicy);
		oCache.setImpl(oBackend);
		
		ProactiveCacheHandler oHandler = new ProactiveCacheHandler( oCache );
		
		Object oResult = oHandler.invoke(c, oSayHelloMethod, new Object[] { "George" });
		assertNotNull( oResult );
		Object oResult2 = oHandler.invoke(c, oSayHelloMethod, new Object[] { "George" });
		assertEquals(oResult, oResult2); // because it's cached!
		
		Thread.sleep(500);
		oResult2 = oHandler.invoke(c, oSayHelloMethod, new Object[] { "George" });
		assertNotNull( oResult2 );
		assertTrue( oResult2.toString().startsWith("Hello George"));
		
		assertFalse( oResult.equals( oResult2 ));
		
	}
}
