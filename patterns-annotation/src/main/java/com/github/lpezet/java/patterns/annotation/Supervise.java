/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Luc Pezet
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Supervise {

	public Class<? extends IExecutorServiceFactory> executorServiceFactory() default ExecutorServiceFactory.class;
	
	public int threads() default 10;
	
	public long timeout() default 1000;
	
	public TimeUnit timeunit() default TimeUnit.MILLISECONDS;
	
}
