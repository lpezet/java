/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.patterns.circuitbreaker.BaseCircuitBreakerCondition;
import com.github.lpezet.java.patterns.circuitbreaker.BaseCircuitBreakerStrategy;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerCondition;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerHandler;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;
import com.github.lpezet.java.patterns.circuitbreaker.InMemoryCircuitBreaker;
import com.github.lpezet.java.patterns.command.BaseCommand;
import com.github.lpezet.java.patterns.command.ICommand;
import com.github.lpezet.java.patterns.retry.BaseRetryStrategy;
import com.github.lpezet.java.patterns.retry.BasicRetryCondition;
import com.github.lpezet.java.patterns.retry.ExponentialBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IRetryCondition;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;
import com.github.lpezet.java.patterns.worker.CircuitBreakerWorker;
import com.github.lpezet.java.patterns.worker.IWorker;
import com.github.lpezet.java.patterns.worker.RetryWorker;
import com.github.lpezet.java.patterns.worker.SupervisorWorker;

/**
 * @author Luc Pezet
 *
 */
@Aspect
public class AllAspect {

	@Pointcut(value="execution(public * *(..))")
	public void anyPublicMethod() {}
	
	@Pointcut(value="@annotation(com.github.lpezet.java.patterns.annotation.Supervise)")
	public void superviseAnnotation() {}
	
	@Pointcut(value="@annotation(com.github.lpezet.java.patterns.annotation.Retry)")
	public void retryAnnotation() {}
	
	@Pointcut(value="@annotation(com.github.lpezet.java.patterns.annotation.ShortCircuit)")
	public void shortCircuitAnnotation() {}
	
	private static final IWorker<ICommand<Object>, Object> COMMAND_WORKER = new IWorker<ICommand<Object>, Object>() {
		@Override
		public Object perform(ICommand<Object> pWork) throws Exception {
			return pWork.execute();
		}
	};
	private Logger mLogger = LoggerFactory.getLogger(this.getClass());
	private WeakHashMap<String, IWorker<ICommand<Object>, Object>> mWorkers = new WeakHashMap<String, IWorker<ICommand<Object>,Object>>();
	
	@Around("anyPublicMethod() && (superviseAnnotation() || retryAnnotation() || shortCircuitAnnotation())")
	public Object handle(final ProceedingJoinPoint pJoinPoint) throws Throwable {
		//if (mLogger.isTraceEnabled()) mLogger.trace("Supervising " + pJoinPoint.getSignature() + "...");
		if (mLogger.isTraceEnabled()) mLogger.trace("Advising " + pJoinPoint.getSignature() + "...");
		try {
			// Create command out of joint point
			ICommand<Object> oCommand = new BaseCommand<Object>() {
				@Override
				public Object execute() throws Exception {
					try {
						return pJoinPoint.proceed();
					} catch (Exception e) {
						throw e;
					} catch (Throwable t) {
						throw new Exception(t);
					}
				}
			};
			// Get Worker
			String oKey = pJoinPoint.getTarget().toString();
			IWorker<ICommand<Object>, Object> oWorker = mWorkers.get(oKey);
			if (oWorker == null) {
				try {
					Annotation[] oAnnotations = getAnnotations(pJoinPoint);
					oWorker = createWorker(oAnnotations);
				} catch (SecurityException e) {
					oWorker = COMMAND_WORKER;
				} catch (NoSuchMethodException e) {
					oWorker = COMMAND_WORKER;
				}
				mWorkers.put(oKey, oWorker);
			}
			
			return oWorker.perform(oCommand);
		} catch (Exception e) {
			throw e;
		} catch (Throwable t) {
			throw new Exception(t); // wrapping Errors
		}
	}
	
	
	private IWorker<ICommand<Object>, Object> createWorker(Annotation[] pAnnotations) throws Exception {
		IWorker oPreviousWorker = COMMAND_WORKER;
		for (int i = pAnnotations.length - 1; i >= 0; i--) {
			Annotation a = pAnnotations[i];
			IWorker oWorker = newWorker(a, oPreviousWorker);
			if (oWorker != null) oPreviousWorker = oWorker;
		}
		return oPreviousWorker;
	}

	private IWorker newWorker(Annotation pAnnotation, IWorker pWorker) throws Exception {
		if (ShortCircuit.class.isAssignableFrom( pAnnotation.annotationType() )) {
			System.out.println("SC");
			return newShortCircuit((ShortCircuit) pAnnotation, pWorker);
		} else if (Retry.class.isAssignableFrom( pAnnotation.annotationType() )) {
			System.out.println("Retry");
			return newRetry((Retry) pAnnotation, pWorker);
		} else if (Supervise.class.isAssignableFrom( pAnnotation.annotationType() )) {
			System.out.println("Supervise");
			return newSupervisor((Supervise) pAnnotation, pWorker);
		}
		return null;
	}

	private IWorker newShortCircuit(ShortCircuit pAnnotation, IWorker pWorker) throws Exception {
		ICircuitBreakerCondition oCondition = pAnnotation.condition().newInstance();
		if (oCondition instanceof BaseCircuitBreakerCondition) {
			BaseCircuitBreakerCondition oBaseCondition = (BaseCircuitBreakerCondition) oCondition;
			oBaseCondition.setExceptionsToTrip(pAnnotation.exceptionsToTrip());
			oBaseCondition.setTriper(pAnnotation.triper());
		}
		ICircuitBreakerHandler oOpenHandler = pAnnotation.openHandler().newInstance();
		ICircuitBreakerStrategy oStgy = new BaseCircuitBreakerStrategy(new InMemoryCircuitBreaker(), oOpenHandler, oCondition);
		return new CircuitBreakerWorker(pWorker, oStgy);
	}

	private IWorker newRetry(Retry pAnnotation, IWorker pWorker) throws Exception {
		IRetryCondition oCondition = pAnnotation.condition().newInstance();
		if (oCondition instanceof BasicRetryCondition) {
			BasicRetryCondition oBaseCondition = (BasicRetryCondition) oCondition;
			oBaseCondition.setMaxExecutions(pAnnotation.maxExecutions());
			oBaseCondition.setException(pAnnotation.exception());
		}
		IBackoffStrategy oBackoffStrategy = pAnnotation.backoff().newInstance();
		if (oBackoffStrategy instanceof ExponentialBackoffStrategy) {
			ExponentialBackoffStrategy oExpo = (ExponentialBackoffStrategy) oBackoffStrategy;
			oExpo.setMaxBackoffInMillis(pAnnotation.maxBackoffInMillis());
			oExpo.setScaleFactor(pAnnotation.scaleFactor());
		}
		IRetryStrategy oStgy = new BaseRetryStrategy(oCondition, oBackoffStrategy);	
		return new RetryWorker(pWorker, oStgy);
	}

	private IWorker newSupervisor(Supervise pAnnotation, IWorker pWorker) throws Exception {
		IExecutorServiceFactory oFactory = pAnnotation.executorServiceFactory().newInstance();
		if (oFactory instanceof ExecutorServiceFactory) {
			int oThreads = pAnnotation.threads();
			((ExecutorServiceFactory) oFactory).setThreads(oThreads);
		}
		ExecutorService oES = oFactory.newExecutorService();
		long oTimeout = pAnnotation.timeout();
		TimeUnit oTimeUnit = pAnnotation.timeunit();
		return new SupervisorWorker(pWorker, oES, oTimeout, oTimeUnit);
	}

	protected Annotation[] getAnnotations(ProceedingJoinPoint pJoinPoint) throws SecurityException, NoSuchMethodException {
		MethodSignature oSignature = (MethodSignature) pJoinPoint.getSignature();
		Method oMethod = oSignature.getMethod();
		if (oMethod.getDeclaringClass().isInterface()) {
	        try {
	        	// NB: This is because the joint point might be an interface and not its implementation.
	        	oMethod= pJoinPoint.getTarget().getClass().getDeclaredMethod(pJoinPoint.getSignature().getName(), oMethod.getParameterTypes());
	       } catch (final SecurityException e) {
	            mLogger.error("Could not get method to wrap CircuitBreaker around. Setting up pass-through CircuitBreakerStrategy for " + pJoinPoint.getTarget(), e);
	            throw e;
	       } catch (final NoSuchMethodException e) {
	            mLogger.error("Could not find method: " + oMethod + " on " + pJoinPoint.getTarget(), e);
	            throw e;
	       }
	    }
		return oMethod.getAnnotations();
	}
	
	
}
