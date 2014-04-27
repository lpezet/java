/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.lpezet.java.patterns.supervisor.Supervisor;

/**
 * @author luc
 *
 */
public class SupervisorWorker<W extends IWork, R extends IResult> implements IWorker<W, R> {

	private IWorker<W, R> mImpl;
	private Supervisor<R> mSupervisor;
	
	
	public SupervisorWorker(IWorker<W, R> pImpl, long pTimeout, TimeUnit pTimeoutUnit) {
		this(pImpl, Executors.newCachedThreadPool(), pTimeout, pTimeoutUnit);
	}
	
	public SupervisorWorker(IWorker<W, R> pImpl, ExecutorService pExecutorService, long pTimeout, TimeUnit pTimeoutUnit) {
		mSupervisor = new Supervisor<R>(pExecutorService, pTimeout, pTimeoutUnit);
		mImpl = pImpl;
	}
	
	@Override
	public R perform(final W pWork) throws Exception {
		return mSupervisor.supervise(new Callable<R>() {
			@Override
			public R call() throws Exception {
				return mImpl.perform(pWork);
			}
		});
	}
}
