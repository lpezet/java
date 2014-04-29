/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author luc
 *
 */
public class AsyncWorkerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncWorkerTest.class);
	
	private static class IntegerResult {
		private int mResult;
		public IntegerResult(int pResult) {
			mResult = pResult;
		}
		public int getResult() {
			return mResult;
		}
	}

	private static class MyWorker implements IWorker<Void, IntegerResult> {
		private Random mRandom = new Random();
		@Override
		public IntegerResult perform(Void pWork) throws Exception {
			LOGGER.info("Simulating work (i.e. sleeping)...");
			Thread.sleep(mRandom.nextInt(30) * 100);
			LOGGER.info("Done working! (i.e. just woke up!!).");
			return new IntegerResult(mRandom.nextInt(100));
		}
	}

	@Test
	public void pushResults() throws Exception {
		ExecutorService oExecutorService = Executors.newFixedThreadPool(3);
		MyWorker oImpl = new MyWorker();
		AsyncWorker<Void, IntegerResult> oWorker = new AsyncWorker<Void, IntegerResult>(oExecutorService, oImpl);
		Callback<IntegerResult> oCallback = new Callback<IntegerResult>() {
			@Override
			public void onResult(IntegerResult pResult) {
				LOGGER.info("Got result: " + pResult.getResult() + ".");
			}
			@Override
			public void onException(Exception e) {
				LOGGER.error("Got exception!", e);
			}
		};
		IAsyncResult<IntegerResult> oResultHolder = oWorker.perform(null);
		oResultHolder.setCallback(oCallback);
		LOGGER.info("Could do something here while work is being peformed...");
		oResultHolder.get(); // Called here only to make sure the callback has been called. Could put thread to sleep too.
		LOGGER.info("All done.");
	}
	
	@Test
	public void pullResults() throws Exception {
		ExecutorService oExecutorService = Executors.newFixedThreadPool(3);
		MyWorker oImpl = new MyWorker();
		AsyncWorker<Void, IntegerResult> oWorker = new AsyncWorker<Void, IntegerResult>(oExecutorService, oImpl);
		IAsyncResult<IntegerResult> oResultHolder = oWorker.perform(null);
		LOGGER.info("Could do something here while work is being peformed...");
		IntegerResult oFinalResult = oResultHolder.get();
		LOGGER.info("All done. Final Result = " + oFinalResult.getResult());
	}
}
