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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.cache.BasicCacheItem;
import com.github.lpezet.java.cache.BasicCommandAugmentedItem;
import com.github.lpezet.java.cache.CacheItem;
import com.github.lpezet.java.cache.Command;
import com.github.lpezet.java.cache.CommandAugmentedItem;
import com.github.lpezet.java.cache.util.IWorker;

/**
 * 
 * Resources:
 * - http://www.onjava.com/pub/a/onjava/2003/08/20/memoization.html?page=last&x-showcontent=text&x-maxdepth=0
 * 
 * @author Luc Pezet
 *
 */
public class ProactiveCacheHandler implements InvocationHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger( ProactiveCacheHandler.class );
	
	/*
	private static class MethodInvocationCommand implements Command<Object> {
		//TODO: Should use WeakReference instead???
		private Object mProxy;
		private Method mMethod;
		private Object[] mArgs;
		public MethodInvocationCommand(Object pProxy, Method pMethod, Object[] pArgs) {
			mProxy = pProxy;
			mMethod = pMethod;
			mArgs = pArgs;
		}
		@Override
		public Object execute() {
			//if (LOGGER.isDebugEnabled()) LOGGER.debug("Calling " + mMethod + " on " + mProxy + " with " + Arrays.asList( mArgs ) + "...");
			if (LOGGER.isDebugEnabled()) LOGGER.debug("Calling " + mMethod + " with " + Arrays.asList( mArgs ) + "...");
			try {
				return mMethod.invoke(mProxy, mArgs);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	*/
	
	private static class WorkerCommand implements Command<Object> {
		private IWorker<Object[], Object> mImpl;
		private Object[] mWork;
		public WorkerCommand(IWorker<Object[], Object> pImpl, Object[] pWork) {
			mImpl = pImpl;
			mWork = pWork;
		}
		@Override
		public Object execute() {
			return mImpl.perform( mWork );
		}
	}
	
	private com.github.lpezet.java.cache.ProactiveCache mCache;
	
	public ProactiveCacheHandler(com.github.lpezet.java.cache.ProactiveCache pCache) {
		mCache = pCache;
	}
	
	public Object invoke(IWorker<Object[], Object> pWorker, Object[] pWork) throws Throwable {
		List<Object> oKey = Arrays.asList( pWork );
		CacheItem<CommandAugmentedItem> oCacheItem = mCache.get( oKey.toString() );
		if (oCacheItem == null) {
			Object oResult = pWorker.perform( pWork );
			if (oResult != null) { //TODO: is that right?
				oCacheItem = new BasicCacheItem<CommandAugmentedItem>(oKey.toString(), new BasicCommandAugmentedItem(oResult, new WorkerCommand(pWorker, pWork)));
				mCache.put( oCacheItem );
			}
		}
		return oCacheItem == null ? null : oCacheItem.getValue().getValue();
		
	}

	@Override
	public Object invoke(final Object pProxy, final Method pMethod, final Object[] pArgs) throws Throwable {
		if (!pMethod.getReturnType().equals(Void.TYPE) && pMethod.isAnnotationPresent(ProactiveCache.class)) {
			return invoke(new IWorker<Object[], Object>() {
				public Object perform(Object[] pWork) {
					try {
						if (LOGGER.isDebugEnabled()) LOGGER.debug("Calling " + pMethod + " with " + Arrays.asList( pWork ) + "...");
						return pMethod.invoke( pProxy, pWork);
					} catch (Exception e) {
						LOGGER.error("Unexpected error.", e);
						return new RuntimeException(e);
					}
				};
			}, pArgs);
		} else {
			return pMethod.invoke( pProxy, pArgs);
		}
	}
	
	public Object handle(final ProceedingJoinPoint pJoinPoint) throws Throwable {
		MethodSignature oMSig = (MethodSignature) pJoinPoint.getSignature();
		final Method oMethod = oMSig.getMethod();
		if (!oMethod.getReturnType().equals(Void.TYPE) && oMethod.isAnnotationPresent(ProactiveCache.class)) {
			return invoke(new IWorker<Object[], Object>() {
				public Object perform(Object[] pWork) {
					try {
						if (LOGGER.isDebugEnabled()) LOGGER.debug("Proceeding with " + oMethod + " with " + Arrays.asList( pWork ) + "...");
						return pJoinPoint.proceed( pWork );
					} catch (Throwable e) {
						return new RuntimeException(e);
					}
				};
			}, pJoinPoint.getArgs());
		} else {
			return pJoinPoint.proceed( pJoinPoint.getArgs() );
		}
	}
}
