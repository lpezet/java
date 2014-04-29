/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.lpezet.java.patterns.supervisor.TimeoutException;

/**
 * @author luc
 *
 */
public class SupervisorWorkerTest {
	
	private static class IntegerResult {
		private int mResult;
		public IntegerResult(int pResult) {
			mResult = pResult;
		}
		public int getResult() {
			return mResult;
		}
	}
	
	@Test(expected=TimeoutException.class)
	public void failFast() throws Exception {
		IWorker<Void, Void> oWorker = new IWorker<Void, Void>() {
			@Override
			public Void perform(Void pWork) throws Exception {
				Thread.sleep(100);
				return null;
			}
		};
		SupervisorWorker<Void, Void> s = new SupervisorWorker<Void, Void>(oWorker, 50, TimeUnit.MILLISECONDS);
		s.perform(null);
	}
	
	@Test
	public void succeed() throws Exception {
		IWorker<Void, IntegerResult> oWorker = new IWorker<Void, IntegerResult>() {
			@Override
			public IntegerResult perform(Void pWork) throws Exception {
				return new IntegerResult(987);
			}
		};
		SupervisorWorker<Void, IntegerResult> s = new SupervisorWorker<Void, IntegerResult>(oWorker, 100, TimeUnit.MILLISECONDS);
		IntegerResult oActual = s.perform(null);
		assertNotNull(oActual);
		assertEquals(987, oActual.getResult());
	}

}
