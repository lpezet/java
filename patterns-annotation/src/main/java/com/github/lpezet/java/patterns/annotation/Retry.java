/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.lpezet.java.patterns.retry.BasicRetryCondition;
import com.github.lpezet.java.patterns.retry.ExponentialBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IBackoffStrategy;
import com.github.lpezet.java.patterns.retry.IRetryCondition;

/**
 * @author Luc Pezet
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {

	public Class<? extends IRetryCondition> condition() default BasicRetryCondition.class;
	
	public int maxExecutions() default 3;
	
	public Class<? extends Exception>[] exceptions() default Exception.class;
	
	public int scaleFactor() default 300;
	
	public Class<? extends IBackoffStrategy> backoff() default ExponentialBackoffStrategy.class;
	
	public long maxBackoffInMillis() default 20 * 1000;
	
}
