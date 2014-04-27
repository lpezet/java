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
	
	private static class WorkerInvocationHandler<S extends IWorker<W, R>, W extends IWork, R extends IResult> implements InvocationHandler {
		
		private static final String EXECUTE = "perform";
		private IWorker<W, R> mDelegate;
		private S mSource; 
		
		public WorkerInvocationHandler(S pSource, IWorker<W, R> pDelegate) {
			mDelegate = pDelegate;
			mSource = pSource;
		}
		
		@Override
		public Object invoke(Object pProxy, Method pMethod, Object[] pArgs) throws Throwable {
			if (EXECUTE.equalsIgnoreCase(pMethod.getName()) && pArgs.length == 1 && (pArgs[0] instanceof IWork) ) {
				if (LOGGER.isTraceEnabled()) LOGGER.trace(pProxy.getClass().getName() + "." + pMethod.getName() + "(" + pArgs + ") --> " + mDelegate + ".perform(" + pArgs + ").");
				return mDelegate.perform((W) pArgs[0]);
			} else {
				if (LOGGER.isTraceEnabled()) LOGGER.trace(pProxy.getClass().getName() + "." + pMethod.getName() + "(" + pArgs + ") --> " + mSource + "." + pMethod.getName() + "(" + pArgs + ").");
				return pMethod.invoke(mSource, pArgs);
			}
		}
	}

	public static <S extends IWorker<W,R>, W extends IWork, R extends IResult> S retry(S pSource, IRetryStrategy pRetryStrategy) {
		RetryWorker<W, R> r = new RetryWorker<W, R>(pSource, pRetryStrategy);
		WorkerInvocationHandler<S, W, R> oIH = new WorkerInvocationHandler<S, W, R>(pSource, r);
		S oResult = (S) Proxy.newProxyInstance(Workers.class.getClassLoader(), getAllInterfaces(pSource.getClass()), oIH);
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

	public static <S extends IWorker<W,R>, W extends IWork, R extends IResult> S supervise(S pSource, long pTimeout, TimeUnit pUnit) {
		SupervisorWorker<W, R> s = new SupervisorWorker<W, R>(pSource, pTimeout, pUnit);
		WorkerInvocationHandler<S, W, R> oIH = new WorkerInvocationHandler<S, W, R>(pSource, s);
		S oResult = (S) Proxy.newProxyInstance(Commands.class.getClassLoader(), getAllInterfaces(pSource.getClass()), oIH);
		if (LOGGER.isTraceEnabled()) LOGGER.trace("Created proxy " + oResult.getClass().getName() + " out of " + pSource.getClass().getName() + " using delegate " + s.getClass().getName());
		return oResult;
	}
	
	public static <S extends IWorker<W,R>, W extends IWork, R extends IResult> S circuitBreaker(S pSource, ICircuitBreakerStrategy pStrategy) {
		CircuitBreakerWorker<W, R> c = new CircuitBreakerWorker<W, R>(pSource, pStrategy);
		WorkerInvocationHandler<S, W, R> oIH = new WorkerInvocationHandler<S, W, R>(pSource, c);
		S oResult = (S) Proxy.newProxyInstance(Commands.class.getClassLoader(), getAllInterfaces(pSource.getClass()), oIH);
		if (LOGGER.isTraceEnabled()) LOGGER.trace("Created proxy " + oResult.getClass().getName() + " out of " + pSource.getClass().getName() + " using delegate " + c.getClass().getName());
		return oResult;
	}
}
