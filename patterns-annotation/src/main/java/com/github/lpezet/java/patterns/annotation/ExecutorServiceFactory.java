/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create a fixed thread pool using getThreads() number of threads.
 * Default is 10 thread.
 * 
 * @author Luc Pezet
 *
 */
public class ExecutorServiceFactory implements IExecutorServiceFactory {

	private int mThreads = 10;
	
	
	@Override
	public ExecutorService newExecutorService() {
		return Executors.newFixedThreadPool(mThreads);
	}
	
	public int getThreads() {
		return mThreads;
	}
	
	public void setThreads(int pThreads) {
		mThreads = pThreads;
	}
}
