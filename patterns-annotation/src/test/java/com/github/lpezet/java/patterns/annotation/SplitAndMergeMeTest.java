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
package com.github.lpezet.java.patterns.annotation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private Logger mLogger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ISplitAndMergeMe mTest;
	
	@Test
	public void doIt() throws Exception {
		Integer[] oOperands = new Integer[1000000];
		long oExpectedResult = 0; 
		for (int i = 1; i <= 1000000; i++) {
			//oOperands.add(i);
			oOperands[i-1] = i;
			oExpectedResult += i;
		}
		long oStart = System.currentTimeMillis();
		long oResult = mTest.addUp( oOperands );
		long oEnd = System.currentTimeMillis();
		mLogger.info("Total execution time = " + (oEnd - oStart) + "ms.");
		//System.out.println("Result = " + oResult + ", expected = " + oExpectedResult);
		assertEquals(oExpectedResult, oResult);
	}
}
