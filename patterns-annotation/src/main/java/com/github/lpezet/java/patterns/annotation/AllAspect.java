/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
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
import com.github.lpezet.java.patterns.retry.BaseRetryStrategy;
import com.github.lpezet.java.patterns.retry.BasicRetryCondition;
import com.github.lpezet.java.patterns.retry.ExponentialBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IRetryCondition;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;
import com.github.lpezet.java.patterns.worker.CircuitBreakerWorker;
import com.github.lpezet.java.patterns.worker.IResultMerger;
import com.github.lpezet.java.patterns.worker.IWorkSplitter;
import com.github.lpezet.java.patterns.worker.IWorker;
import com.github.lpezet.java.patterns.worker.RetryWorker;
import com.github.lpezet.java.patterns.worker.SimpleSPMWorker;
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
	
	@Pointcut(value="@annotation(com.github.lpezet.java.patterns.annotation.DivideAndConquer)")
	public void divideAndConquerAnnotation() {}
	
	private static final IWorker<ProceedingJointPointAndArgs, Object> PJP_WORKER = new IWorker<ProceedingJointPointAndArgs, Object>() {
		
		@Override
		public Object perform(ProceedingJointPointAndArgs pWork) throws Exception {
			try {
				return pWork.getProceedingJoinPoint().proceed(pWork.getArgs());
			} catch (Exception e) {
				throw e;
			} catch (Throwable e) {
				throw new Exception(e); // wrapping Errors
			}
		}
	};
	
	private Logger mLogger = LoggerFactory.getLogger(this.getClass());
	private WeakHashMap<String, IWorker<ProceedingJointPointAndArgs, Object>> mWorkers = new WeakHashMap<String, IWorker<ProceedingJointPointAndArgs, Object>>();
	
	@Around("anyPublicMethod() && (superviseAnnotation() || retryAnnotation() || shortCircuitAnnotation() || divideAndConquerAnnotation())")
	public Object handle(final ProceedingJoinPoint pJoinPoint) throws Throwable {
		if (mLogger.isTraceEnabled()) mLogger.trace("Advising " + pJoinPoint.getSignature() + "...");
		System.out.println("Args = " + Arrays.asList( pJoinPoint.getArgs() ));
		try {
			ProceedingJointPointAndArgs oParameters = new ProceedingJointPointAndArgs(pJoinPoint, pJoinPoint.getArgs());
			// Get Worker
			String oKey = pJoinPoint.getTarget().toString();
			IWorker<ProceedingJointPointAndArgs, Object> oWorker = mWorkers.get(oKey);
			if (oWorker == null) {
				try {
					Annotation[] oAnnotations = getAnnotations(pJoinPoint);
					oWorker = createWorker(oAnnotations);
				} catch (SecurityException e) {
					oWorker = PJP_WORKER;
				} catch (NoSuchMethodException e) {
					oWorker = PJP_WORKER;
				}
				mWorkers.put(oKey, oWorker);
			}
			return oWorker.perform(oParameters);
		} catch (Exception e) {
			throw e;
		} catch (Throwable t) {
			throw new Exception(t); // wrapping Errors
		}
	}
	
	
	private IWorker<ProceedingJointPointAndArgs, Object> createWorker(Annotation[] pAnnotations) throws Exception {
		IWorker<ProceedingJointPointAndArgs, Object> oPreviousWorker = PJP_WORKER;
		for (int i = pAnnotations.length - 1; i >= 0; i--) {
			Annotation a = pAnnotations[i];
			IWorker<ProceedingJointPointAndArgs, Object> oWorker = newWorker(a, oPreviousWorker);
			if (oWorker != null) oPreviousWorker = oWorker;
		}
		return oPreviousWorker;
	}

	private IWorker<ProceedingJointPointAndArgs, Object> newWorker(Annotation pAnnotation, IWorker<ProceedingJointPointAndArgs, Object> pWorker) throws Exception {
		if (ShortCircuit.class.isAssignableFrom( pAnnotation.annotationType() )) {
			System.out.println("SC");
			return newShortCircuit((ShortCircuit) pAnnotation, pWorker);
		} else if (Retry.class.isAssignableFrom( pAnnotation.annotationType() )) {
			System.out.println("Retry");
			return newRetry((Retry) pAnnotation, pWorker);
		} else if (Supervise.class.isAssignableFrom( pAnnotation.annotationType() )) {
			System.out.println("Supervise");
			return newSupervisor((Supervise) pAnnotation, pWorker);
		} else if (DivideAndConquer.class.isAssignableFrom( pAnnotation.annotationType() )) {
			System.out.println("Divide And Conquer");
			return newSPM((DivideAndConquer) pAnnotation, pWorker);
		}
		return null;
	}

	private IWorker<ProceedingJointPointAndArgs, Object> newSPM(DivideAndConquer pAnnotation, IWorker<ProceedingJointPointAndArgs, Object> pWorker) throws Exception {
		IWorkSplitter<ProceedingJointPointAndArgs> oSplitter = pAnnotation.splitter().newInstance();
		IResultMerger<Object> oMerger = pAnnotation.merger().newInstance();
		IExecutorServiceFactory oFactory = pAnnotation.executorServiceFactory().newInstance();
		if (oFactory instanceof ExecutorServiceFactory) {
			int oThreads = pAnnotation.threads();
			((ExecutorServiceFactory) oFactory).setThreads(oThreads);
		}
		ExecutorService oES = oFactory.newExecutorService();
		return new SimpleSPMWorker<ProceedingJointPointAndArgs, Object>(oES, oSplitter, oMerger, pWorker);
	}

	private IWorker<ProceedingJointPointAndArgs, Object> newShortCircuit(ShortCircuit pAnnotation, IWorker<ProceedingJointPointAndArgs, Object> pWorker) throws Exception {
		ICircuitBreakerCondition oCondition = pAnnotation.condition().newInstance();
		if (oCondition instanceof BaseCircuitBreakerCondition) {
			BaseCircuitBreakerCondition oBaseCondition = (BaseCircuitBreakerCondition) oCondition;
			oBaseCondition.setExceptionsToTrip(pAnnotation.exceptionsToTrip());
			oBaseCondition.setTripers(pAnnotation.tripers());
		}
		ICircuitBreakerHandler oOpenHandler = pAnnotation.openHandler().newInstance();
		ICircuitBreakerStrategy oStgy = new BaseCircuitBreakerStrategy(new InMemoryCircuitBreaker(), oOpenHandler, oCondition);
		return new CircuitBreakerWorker<ProceedingJointPointAndArgs, Object>(pWorker, oStgy);
	}

	private IWorker<ProceedingJointPointAndArgs, Object> newRetry(Retry pAnnotation, IWorker<ProceedingJointPointAndArgs, Object> pWorker) throws Exception {
		IRetryCondition oCondition = pAnnotation.condition().newInstance();
		if (oCondition instanceof BasicRetryCondition) {
			BasicRetryCondition oBaseCondition = (BasicRetryCondition) oCondition;
			oBaseCondition.setMaxExecutions(pAnnotation.maxExecutions());
			oBaseCondition.setExceptions(pAnnotation.exceptions());
		}
		IBackoffStrategy oBackoffStrategy = pAnnotation.backoff().newInstance();
		if (oBackoffStrategy instanceof ExponentialBackoffStrategy) {
			ExponentialBackoffStrategy oExpo = (ExponentialBackoffStrategy) oBackoffStrategy;
			oExpo.setMaxBackoffInMillis(pAnnotation.maxBackoffInMillis());
			oExpo.setScaleFactor(pAnnotation.scaleFactor());
		}
		IRetryStrategy oStgy = new BaseRetryStrategy(oCondition, oBackoffStrategy);	
		return new RetryWorker<ProceedingJointPointAndArgs, Object>(pWorker, oStgy);
	}

	private IWorker<ProceedingJointPointAndArgs, Object> newSupervisor(Supervise pAnnotation, IWorker<ProceedingJointPointAndArgs, Object> pWorker) throws Exception {
		IExecutorServiceFactory oFactory = pAnnotation.executorServiceFactory().newInstance();
		if (oFactory instanceof ExecutorServiceFactory) {
			int oThreads = pAnnotation.threads();
			((ExecutorServiceFactory) oFactory).setThreads(oThreads);
		}
		ExecutorService oES = oFactory.newExecutorService();
		long oTimeout = pAnnotation.timeout();
		TimeUnit oTimeUnit = pAnnotation.timeunit();
		return new SupervisorWorker<ProceedingJointPointAndArgs, Object>(pWorker, oES, oTimeout, oTimeUnit);
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
