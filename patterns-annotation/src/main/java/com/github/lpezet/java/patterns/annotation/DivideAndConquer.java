/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.lpezet.java.patterns.worker.IResultMerger;
import com.github.lpezet.java.patterns.worker.IWorkSplitter;

/**
 * @author Luc Pezet
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DivideAndConquer {

	public Class<? extends IWorkSplitter<ProceedingJointPointAndArgs>> splitter();
	
	public Class<? extends IResultMerger<Object>> merger();
	
	public Class<? extends IExecutorServiceFactory> executorServiceFactory() default ExecutorServiceFactory.class;
	
	public int threads() default 10;
	
}
