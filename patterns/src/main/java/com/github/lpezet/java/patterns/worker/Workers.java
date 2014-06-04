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
package com.github.lpezet.java.patterns.worker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;
import com.github.lpezet.java.patterns.command.Commands;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;

/**
 * @author luc
 *
 */
public class Workers {

	private static final Logger LOGGER = LoggerFactory.getLogger(Workers.class); 
	
	private static class WorkerInvocationHandler<S extends IWorker<W, R>, W, R> implements InvocationHandler {
		
		private static final String EXECUTE = "perform";
		private IWorker<W, R> mDelegate;
		private S mSource; 
		
		public WorkerInvocationHandler(S pSource, IWorker<W, R> pDelegate) {
			mDelegate = pDelegate;
			mSource = pSource;
		}
		
		@Override
		public Object invoke(Object pProxy, Method pMethod, Object[] pArgs) throws Throwable {
			if (EXECUTE.equalsIgnoreCase(pMethod.getName()) && pArgs.length == 1 ) {
				if (LOGGER.isTraceEnabled()) LOGGER.trace(pProxy.getClass().getName() + "." + pMethod.getName() + "(" + pArgs + ") --> " + mDelegate + ".perform(" + pArgs + ").");
				return mDelegate.perform((W) pArgs[0]);
			} else {
				if (LOGGER.isTraceEnabled()) LOGGER.trace(pProxy.getClass().getName() + "." + pMethod.getName() + "(" + pArgs + ") --> " + mSource + "." + pMethod.getName() + "(" + pArgs + ").");
				return pMethod.invoke(mSource, pArgs);
			}
		}
	}
	
	private static Class<?>[] getAllInterfaces(Class<?> pClass) {
		Set<Class<?>> oInterfaces = new HashSet<Class<?>>();
		Class c = pClass;
		while (c != null) {
			for (int i = 0; i < c.getInterfaces().length; i++) {
				oInterfaces.add(c.getInterfaces()[i]);
			}
			c = c.getSuperclass();
		}
		return oInterfaces.toArray(new Class[] {});
	}
	
	public static <S extends IWorker<W,R>, W, R> S decorate(IWorker<W, R> pWrapper, S pSource) {
		WorkerInvocationHandler<S, W, R> oIH = new WorkerInvocationHandler<S, W, R>(pSource, pWrapper);
		S oResult = (S) Proxy.newProxyInstance(Commands.class.getClassLoader(), getAllInterfaces(pSource.getClass()), oIH);
		if (LOGGER.isTraceEnabled()) LOGGER.trace("Created proxy " + oResult.getClass().getName() + " out of " + pSource.getClass().getName() + " using delegate " + pWrapper.getClass().getName());
		return oResult;
	}
	
	public static <S extends IWorker<W,R>, W, R> S retry(S pSource, IRetryStrategy pRetryStrategy) {
		return decorate(new RetryWorker<W, R>(pSource, pRetryStrategy), pSource);
	}

	public static <S extends IWorker<W,R>, W, R> S supervise(S pSource, long pTimeout, TimeUnit pUnit) {
		return decorate(new SupervisorWorker<W, R>(pSource, pTimeout, pUnit), pSource);
	}
	
	public static <S extends IWorker<W,R>, W, R> S circuitBreaker(S pSource, ICircuitBreakerStrategy pStrategy) {
		return decorate(new CircuitBreakerWorker<W, R>(pSource, pStrategy), pSource);
	}
}
