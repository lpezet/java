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
package com.github.lpezet.java.patterns.command;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;

/**
 * @author luc
 *
 */
public class Commands {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class); 
	
	private static class CommandInvocationHandler<S extends ICommand<T>, T> implements InvocationHandler {
		
		private static final String EXECUTE = "execute";
		private ICommand<T> mDelegate;
		private S mSource; 
		
		public CommandInvocationHandler(S pSource, ICommand<T> pDelegate) {
			mDelegate = pDelegate;
			mSource = pSource;
		}
		
		@Override
		public Object invoke(Object pProxy, Method pMethod, Object[] pArgs) throws Throwable {
			if (EXECUTE.equalsIgnoreCase(pMethod.getName()) || "call".equalsIgnoreCase(pMethod.getName())) {
				if (LOGGER.isTraceEnabled()) LOGGER.trace(pProxy.getClass().getName() + "." + pMethod.getName() + "(" + pArgs + ") --> " + mDelegate + ".execute().");
				return mDelegate.execute();
			} else {
				if (LOGGER.isTraceEnabled()) LOGGER.trace(pProxy.getClass().getName() + "." + pMethod.getName() + "(" + pArgs + ") --> " + mSource + "." + pMethod.getName() + "(" + pArgs + ").");
				return pMethod.invoke(mSource, pArgs);
			}
		}
	}

	public static <S extends ICommand<T>, T> S retry(S pSource, IRetryStrategy pRetryStrategy) {
		RetryCommand<T> r = new RetryCommand<T>(pSource, pRetryStrategy);
		CommandInvocationHandler<S, T> oCIH = new CommandInvocationHandler<S, T>(pSource, r);
		S oResult = (S) Proxy.newProxyInstance(Commands.class.getClassLoader(), getAllInterfaces(pSource.getClass()), oCIH);
		if (LOGGER.isTraceEnabled()) LOGGER.trace("Created proxy " + oResult.getClass().getName() + " out of " + pSource.getClass().getName() + " using delegate " + r.getClass().getName());
		return oResult;
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

	public static <S extends ICommand<T>, T> S supervise(S pSource, long pTimeout, TimeUnit pUnit) {
		SupervisorCommand<T> s = new SupervisorCommand<T>(pSource, pTimeout, pUnit);
		CommandInvocationHandler<S, T> oCIH = new CommandInvocationHandler<S, T>(pSource, s);
		S oResult = (S) Proxy.newProxyInstance(Commands.class.getClassLoader(), getAllInterfaces(pSource.getClass()), oCIH);
		if (LOGGER.isTraceEnabled()) LOGGER.trace("Created proxy " + oResult.getClass().getName() + " out of " + pSource.getClass().getName() + " using delegate " + s.getClass().getName());
		return oResult;
	}
	
	public static <S extends ICommand<T>, T> S circuitBreaker(S pSource, ICircuitBreakerStrategy pStrategy) {
		CircuitBreakerCommand<T> c = new CircuitBreakerCommand<T>(pSource, pStrategy);
		CommandInvocationHandler<S, T> oCIH = new CommandInvocationHandler<S, T>(pSource, c);
		S oResult = (S) Proxy.newProxyInstance(Commands.class.getClassLoader(), getAllInterfaces(pSource.getClass()), oCIH);
		if (LOGGER.isTraceEnabled()) LOGGER.trace("Created proxy " + oResult.getClass().getName() + " out of " + pSource.getClass().getName() + " using delegate " + c.getClass().getName());
		return oResult;
	}
	
	
}
