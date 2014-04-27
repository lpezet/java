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
	
	private static class IntegerResult implements IResult {
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
		IWorker<IWork, IResult> oWorker = new IWorker<IWork, IResult>() {
			@Override
			public IResult perform(IWork pWork) throws Exception {
				Thread.sleep(100);
				return null;
			}
		};
		SupervisorWorker<IWork, IResult> s = new SupervisorWorker<IWork, IResult>(oWorker, 50, TimeUnit.MILLISECONDS);
		s.perform(null);
	}
	
	@Test
	public void succeed() throws Exception {
		IWorker<IWork, IntegerResult> oWorker = new IWorker<IWork, IntegerResult>() {
			@Override
			public IntegerResult perform(IWork pWork) throws Exception {
				return new IntegerResult(987);
			}
		};
		SupervisorWorker<IWork, IntegerResult> s = new SupervisorWorker<IWork, IntegerResult>(oWorker, 100, TimeUnit.MILLISECONDS);
		IntegerResult oActual = s.perform(null);
		assertNotNull(oActual);
		assertEquals(987, oActual.getResult());
	}

}
