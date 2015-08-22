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

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.github.lpezet.java.cache.Cache;
import com.github.lpezet.java.cache.InMemoryCache;
import com.github.lpezet.java.cache.ProactiveCache;
import com.github.lpezet.java.cache.ProactiveCachePolicy;

/**
 * @author Luc Pezet
 *
 */
@Aspect
public class Aspects {
	
	private Map<String, ProactiveCacheHandler> mHandlers = new HashMap<String, ProactiveCacheHandler>();
	
	@Pointcut(value="execution(public void *(..))")
	public void anyVoidMethod() {}
	
	@Pointcut(value="execution(public * *(..))")
	public void anyPublicMethod() {}
	
	@Pointcut(value="execution(@com.github.lpezet.java.cache.annotation.ProactiveCache * *(..))")
	public void anyProactiveAnnotatedMethod() {}
	
	
	//@Pointcut(value="@annotation()")
	//public void proactiveCacheAnnotation() {}
	
	//private Logger mLogger = LoggerFactory.getLogger(this.getClass());
	//private WeakHashMap<String, IWorker<ProceedingJointPointAndArgs, Object>> mWorkers = new WeakHashMap<String, IWorker<ProceedingJointPointAndArgs, Object>>();
	
	//@Around("!anyVoidMethod() && anyPublicMethod() && proactiveCacheAnnotation()")
	//@Around("execution(@com.github.lpezet.java.cache.annotation.ProactiveCache * *(..))")
	//@Around("anyPublicMethod()")
	@Around("!anyVoidMethod() && anyProactiveAnnotatedMethod()")
	public Object handle(final ProceedingJoinPoint pJoinPoint) throws Throwable {
		//System.out.println("######## Proactive Cache Aspect BEFORE!!!! #########");
		
		String oKey = pJoinPoint.toString();
		ProactiveCacheHandler oHandler = mHandlers.get( oKey );
		if (oHandler == null) {
			//TODO: use annotation attributes to customize the following
			Cache<Object> oBackend = new InMemoryCache<Object>();
			ProactiveCachePolicy oPolicy = new ProactiveCachePolicy();
			oPolicy.setTimeToLiveInMillis(100);
			ProactiveCache<Object> oCache = new ProactiveCache<Object>(oPolicy);
			oCache.setImpl(oBackend);
			
			oHandler = new ProactiveCacheHandler( oCache );
			mHandlers.put( oKey, oHandler);
		}
		
		Object oResult = oHandler.handle( pJoinPoint );
		//System.out.println( pJoinPoint.toString() );
		//System.out.println("######## Proactive Cache Aspect AFTER!!!! #########");
		return oResult;
	}

}
