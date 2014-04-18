/**
 * 
 */
package com.github.lpezet.java.patterns.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author luc
 *
 */
public class SupervisorCommandTest {

	@Test
	public void doIt() throws Exception {
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
}
