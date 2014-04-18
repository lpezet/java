/**
 * 
 */
package com.github.lpezet.java.patterns.command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.lpezet.java.patterns.supervisor.Supervisor;

/**
 * @author luc
 *
 */
public class SupervisorCommand<T> extends BaseCommand<T> {
	
	private Supervisor<T> mSupervisor;
	private ICommand<T> mImpl;
	
	public SupervisorCommand(ICommand<T> pImpl, long pTimeout, TimeUnit pTimeoutUnit) {
		this(pImpl, Executors.newCachedThreadPool(), pTimeout, pTimeoutUnit);
	}
	
	public SupervisorCommand(ICommand<T> pImpl, ExecutorService pExecutorService, long pTimeout, TimeUnit pTimeoutUnit) {
		mImpl = pImpl;
		mSupervisor = new Supervisor<T>(pExecutorService, pTimeout, pTimeoutUnit);
	}

	@Override
	public T execute() throws Exception {
		return mSupervisor.supervise(mImpl);
	}
}
