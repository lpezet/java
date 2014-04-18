/**
 * 
 */
package com.github.lpezet.java.patterns.activity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.lpezet.java.patterns.supervisor.Supervisor;

/**
 * @author luc
 *
 */
public class SupervisorActivity<T> implements IActivity<T> {

	private Supervisor<T> mSupervisor;
	private IActivity<T> mImpl;
	
	public SupervisorActivity(IActivity<T> pImpl, long pTimeout, TimeUnit pTimeoutUnit) {
		this(pImpl, Executors.newCachedThreadPool(), pTimeout, pTimeoutUnit);
	}
	
	public SupervisorActivity(IActivity<T> pImpl, ExecutorService pExecutorService, long pTimeout, TimeUnit pTimeoutUnit) {
		mSupervisor = new Supervisor<T>(pExecutorService, pTimeout, pTimeoutUnit);
		mImpl = pImpl;
	}
	
	@Override
	public T start(final IContext pContext) throws Exception {
		return mSupervisor.supervise(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return mImpl.start(pContext);
			}
		});
	}
}
