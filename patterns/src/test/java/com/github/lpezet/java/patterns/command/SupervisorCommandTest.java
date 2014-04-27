/**
 * 
 */
package com.github.lpezet.java.patterns.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.lpezet.java.patterns.supervisor.TimeoutException;

/**
 * @author luc
 *
 */
public class SupervisorCommandTest {

	@Test
	public void succeed() throws Exception {
		ICommand<Integer> oCmd = new BaseCommand<Integer>() {
			@Override
			public Integer execute() throws Exception {
				return 987;
			}
		};
		SupervisorCommand<Integer> s = new SupervisorCommand<Integer>(oCmd, 100, TimeUnit.MILLISECONDS);
		Integer oActual = s.execute();
		assertNotNull(oActual);
		assertEquals(987, oActual.intValue());
	}
	
	@Test(expected=TimeoutException.class)
	public void failFast() throws Exception {
		ICommand<Void> oCmd = new BaseCommand<Void>() {
			@Override
			public Void execute() throws Exception {
				Thread.sleep(100);
				return null;
			}
		};
		SupervisorCommand<Void> s = new SupervisorCommand<Void>(oCmd, 50, TimeUnit.MILLISECONDS);
		s.execute();
	}
}
