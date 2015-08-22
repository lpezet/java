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

/**
 * @author Luc Pezet
 *
 */
public class ProactiveCacheAdapter<T> implements Cache<T> {
	
	/*
	private static class IdentityCommand<T> implements Command<T> {
		
		private T mValue;
		
		public IdentityCommand(T pValue) {
			mValue = pValue;
		}
		@Override
		public T execute() {
			return mValue;
		}
	}
	*/
	
	public static interface CommandFactory<T> {
		CommandAugmentedItem<T> newCommand(CacheItem<T> pItem);
	}
	
	private ProactiveCache<T> mImpl;
	private CommandFactory<T> mCommandFactory;
	
	public ProactiveCacheAdapter(ProactiveCache<T> pProactiveCache, CommandFactory<T> pCommandFactory) {
		mImpl = pProactiveCache;
		mCommandFactory = pCommandFactory;
	}
	
	@Override
	public CacheItem<T> get(String pKey) {
		CacheItem<CommandAugmentedItem<T>> oItem = mImpl.get(pKey);
		if (oItem != null) {
			return new BasicCacheItem<T>(oItem.getKey(), oItem.getValue().getValue());
		}
		return null;
	}
	
	@Override
	public void put(CacheItem<T> pItem) {
		CommandAugmentedItem<T> oCommand = newCommand( pItem );
		CacheItem<CommandAugmentedItem<T>> oCacheItem = new BasicCacheItem<CommandAugmentedItem<T>>( pItem.getKey(), oCommand );
		mImpl.put( oCacheItem );
	}
	
	@Override
	public void remove(String pKey) {
		mImpl.remove(pKey);
	}
	
	@Override
	public void clear() {
		mImpl.clear();
	}
	
	@Override
	public boolean isEmpty() {
		return mImpl.isEmpty();
	}
	
	@Override
	public int size() {
		return mImpl.size();
	}
	
	protected CommandAugmentedItem<T> newCommand(CacheItem<T> pItem) {
		return mCommandFactory.newCommand( pItem );
	}

}
