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
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * @author luc
 *
 */
public class AsyncTaskTest {
	
	private static class MyTask implements ICommand<Integer> {
		private Random mRandom = new Random();
		
		@Override
		public Integer execute() throws Exception {
			Thread.sleep(mRandom.nextInt(30) * 100);
			return mRandom.nextInt();
		}
	}

	@Test
	public void callback() throws Exception {
		ExecutorService oExecutorService = Executors.newFixedThreadPool(3);
		MyTask oImpl = new MyTask();
		AsyncCommand<Integer> oAdvCmd = new AsyncCommand<Integer>(oExecutorService, oImpl);
		Callback<Integer> oCallback = new Callback<Integer>() {
			@Override
			public void callback(Integer pResult) {
				System.out.println(Thread.currentThread().getName() + " : callback(" + pResult + ").");
				
			}
		};
		Future<Integer> oResult = oAdvCmd.execute(oCallback);
		System.out.println(Thread.currentThread().getName() + ": waiting for result...");
		System.out.println(Thread.currentThread().getName() + ": could do something here...");
		oResult.get(); // Called here only to make sure the callback is called. Could put thread to sleep too.
		System.out.println(Thread.currentThread().getName() + ": done waiting.");
	}
	
	@Test
	public void future() throws Exception {
		ExecutorService oExecutorService = Executors.newFixedThreadPool(3);
		MyTask oImpl = new MyTask();
		AsyncCommand<Integer> oAdvCmd = new AsyncCommand<Integer>(oExecutorService, oImpl);
		Future<Integer> oResult = oAdvCmd.execute();
		System.out.println(Thread.currentThread().getName() + ": waiting for result...");
		//oResult.get(3, TimeUnit.SECONDS);
		System.out.println(Thread.currentThread().getName() + ": could do something here...");
		Integer oFinalResult = oResult.get();
		System.out.println(Thread.currentThread().getName() + ": done waiting: " + oFinalResult);
	}
	
	

}
