/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.util.concurrent.ExecutorService;

/**
 * @author Luc Pezet
 *
 */
public interface IExecutorServiceFactory {

	public ExecutorService newExecutorService();
	
}
