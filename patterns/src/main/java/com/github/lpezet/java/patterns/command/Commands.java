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
