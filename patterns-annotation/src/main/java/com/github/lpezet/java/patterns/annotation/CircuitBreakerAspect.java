/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.patterns.circuitbreaker.BaseCircuitBreakerLogic;
import com.github.lpezet.java.patterns.circuitbreaker.BaseCircuitBreakerStrategy;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerHandler;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerLogic;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;
import com.github.lpezet.java.patterns.circuitbreaker.InMemoryCircuitBreaker;

/**
 * @author Luc Pezet
 *
 */
@Aspect
public class CircuitBreakerAspect {
	
	private final static ICircuitBreakerStrategy DUMMY_CB_STRATEGY = new ICircuitBreakerStrategy() {
		@Override
		public <T> T executeAndTrip(Callable<T> pCallable) throws Exception {
			return pCallable.call();
		}
	};
	
	private Logger mLogger = LoggerFactory.getLogger(this.getClass());
	private WeakHashMap<String, ICircuitBreakerStrategy> mCircuitBreakers = new WeakHashMap<String, ICircuitBreakerStrategy>();
	
	@Pointcut(value="execution(public * *(..))")
	public void anyPublicMethod() {}
	
	@Pointcut(value="@annotation(com.github.lpezet.java.patterns.annotation.CircuitBreaker)")
	public void circuitBreakerAnnotation() {}
	
	
	@Around("anyPublicMethod() && circuitBreakerAnnotation()")
	public Object handle(final ProceedingJoinPoint pJoinPoint) throws Throwable {
		if (mLogger.isTraceEnabled()) mLogger.trace("CircuitBreak-ing " + pJoinPoint.getSignature() + "...");
		try {
			String oKey = pJoinPoint.getTarget().toString();
			ICircuitBreakerStrategy oStgy = mCircuitBreakers.get(oKey);
			if (oStgy == null) {
				MethodSignature oSignature = (MethodSignature) pJoinPoint.getSignature();
				Method oMethod = oSignature.getMethod();
				if (oMethod.getDeclaringClass().isInterface()) {
			        try {
			        	// NB: This is because the joint point might be an interface and not its implementation.
			        	oMethod= pJoinPoint.getTarget().getClass().getDeclaredMethod(pJoinPoint.getSignature().getName(), oMethod.getParameterTypes());
			        	CircuitBreaker oAnnotation = oMethod.getAnnotation(CircuitBreaker.class);
			        	oStgy = createCircuitBreakerStrategy(oAnnotation);
			        } catch (final SecurityException e) {
			            mLogger.error("Could not get method to wrap CircuitBreaker around. Setting up pass-through CircuitBreakerStrategy for " + pJoinPoint.getTarget(), e);
			            oStgy = DUMMY_CB_STRATEGY;
			        } catch (final NoSuchMethodException e) {
			            mLogger.error("Could not find method: " + oMethod + " on " + pJoinPoint.getTarget(), e);
			            oStgy = DUMMY_CB_STRATEGY;
			        }
			    }
				
				mCircuitBreakers.put(oKey, oStgy);
			}
			
			return oStgy.executeAndTrip(new Callable<Object>() {
				public Object call() throws Exception {
					try {
						return pJoinPoint.proceed();
					} catch (Throwable e) {
						if (e instanceof Exception) throw (Exception) e;
						throw new Exception(e); // wrap Errors...
					}
				}
			});
		} catch (Exception e) {
			throw e;
		} catch (Throwable t) {
			mLogger.error("Unexpected error.", t);
			throw t;
		}
	}
	
	
	protected ICircuitBreakerStrategy createCircuitBreakerStrategy(CircuitBreaker pAnnotation) throws Exception {
		ICircuitBreakerLogic oLogic = pAnnotation.logic().newInstance();
		if (oLogic instanceof BaseCircuitBreakerLogic) {
			BaseCircuitBreakerLogic oBaseLogic = (BaseCircuitBreakerLogic) oLogic;
			oBaseLogic.setExceptionsToTrip(pAnnotation.exceptionsToTrip());
			oBaseLogic.setTriper(pAnnotation.triper());
		}
		ICircuitBreakerHandler oOpenHandler = pAnnotation.openHandler().newInstance();
		return new BaseCircuitBreakerStrategy(new InMemoryCircuitBreaker(), oOpenHandler, oLogic);
	}
		 
}
