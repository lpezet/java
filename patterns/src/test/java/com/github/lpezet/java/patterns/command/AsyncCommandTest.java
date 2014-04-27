/**
 * The MIT License
 * Copyright (c) 2014 Luc Pezet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * 
 */
package com.github.lpezet.java.patterns.command;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lpezet.java.patterns.worker.AsyncWorkerTest;

/**
 * @author luc
 *
 */
public class AsyncCommandTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncWorkerTest.class);
	
	private static class MyCommand extends BaseCommand<Integer> {
		private Random mRandom = new Random();
		
		@Override
		public Integer execute() throws Exception {
			LOGGER.info("Simulating work (i.e. sleeping)...");
			Thread.sleep(mRandom.nextInt(30) * 100);
			LOGGER.info("Done working! (i.e. just woke up!!).");
			return mRandom.nextInt(50);
		}
	}

	@Test
	public void pushResults() throws Exception {
		ExecutorService oExecutorService = Executors.newFixedThreadPool(3);
		MyCommand oImpl = new MyCommand();
		AsyncCommand<Integer> oAdvCmd = new AsyncCommand<Integer>(oExecutorService, oImpl);
		Callback<Integer> oCallback = new Callback<Integer>() {
			@Override
			public void onResult(Integer pResult) {
				LOGGER.info("Got result: " + pResult + ".");
			}
			@Override
			public void onException(Exception e) {
				LOGGER.error("Got exception!", e);
			}
		};
		IAsyncResult<Integer> oResultHolder = oAdvCmd.execute();
		oResultHolder.setCallback(oCallback);
		LOGGER.info("Could do something here while work is being peformed...");
		oResultHolder.get(); // Called here only to make sure the callback has been called. Could put thread to sleep too.
		LOGGER.info("All done.");
	}
	
	@Test
	public void pullResults() throws Exception {
		ExecutorService oExecutorService = Executors.newFixedThreadPool(3);
		MyCommand oImpl = new MyCommand();
		AsyncCommand<Integer> oAdvCmd = new AsyncCommand<Integer>(oExecutorService, oImpl);
		IAsyncResult<Integer> oResultHolder = oAdvCmd.execute();
		LOGGER.info("Could do something here while work is being peformed...");
		Integer oFinalResult = oResultHolder.get();
		LOGGER.info("All done. Final Result = " + oFinalResult);
	}
	
	

}
