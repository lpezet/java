/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Luc Pezet
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SplitAndMergeMeTest {

	@Autowired
	private ISplitAndMergeMe mTest;
	
	@Test
	public void doIt() throws Exception {
		//List<Integer> oOperands = new ArrayList<Integer>();
		Integer[] oOperands = new Integer[1000000];
		long oExpectedResult = 0; 
		for (int i = 1; i <= 1000000; i++) {
			//oOperands.add(i);
			oOperands[i-1] = i;
			oExpectedResult += i;
		}
		long oStart = System.currentTimeMillis();
		long oResult = mTest.addUp( oOperands ); //oOperands.toArray(new Integer[ oOperands.size() ]));
		long oEnd = System.currentTimeMillis();
		System.out.println("Total execution time = " + (oEnd - oStart) + "ms.");
		//System.out.println("Result = " + oResult + ", expected = " + oExpectedResult);
		assertEquals(oExpectedResult, oResult);
	}
}
