/**
 * 
 */
package com.github.lpezet.java.patterns.supervisor;

import static org.junit.Assert.assertNotNull;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author luc
 *
 */
public class SupervisorTest {

	@Test(expected=TimeoutException.class)
	public void timeout() throws Exception {
		ExecutorService oExec = Executors.newCachedThreadPool();
		Supervisor<Integer> s = new Supervisor<Integer>(oExec, 100, TimeUnit.MILLISECONDS);
		Callable<Integer> oTest = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(200);
				return new Random().nextInt(999);
			}
		};
		s.supervise(oTest);
	}
	
	@Test
	public void ok() throws Exception {
		ExecutorService oExec = Executors.newCachedThreadPool();
		Supervisor<Integer> s = new Supervisor<Integer>(oExec, 100, TimeUnit.MILLISECONDS);
		Callable<Integer> oTest = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(99);
				return new Random().nextInt(999);
			}
		};
		Integer i = s.supervise(oTest);
		assertNotNull(i);
	}
}
