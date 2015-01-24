/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.lpezet.java.patterns.circuitbreaker.BaseCircuitBreakerLogic;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerHandler;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerLogic;
import com.github.lpezet.java.patterns.circuitbreaker.SingleTryCircuitBreakerStrategy;

/**
 * @author Luc Pezet
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CircuitBreaker {

	public Class<? extends Exception> triper() default Exception.class;
	
	public int exceptionsToTrip() default 3;
	
	public Class<? extends ICircuitBreakerLogic> logic() default BaseCircuitBreakerLogic.class;
	
	public Class<? extends ICircuitBreakerHandler> openHandler() default SingleTryCircuitBreakerStrategy.class;
	
}