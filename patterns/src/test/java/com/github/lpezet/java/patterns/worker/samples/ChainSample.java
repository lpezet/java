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
package com.github.lpezet.java.patterns.worker.samples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.lpezet.java.patterns.worker.ChainBuilder;
import com.github.lpezet.java.patterns.worker.ChainWorker;
import com.github.lpezet.java.patterns.worker.IWorker;

/**
 * @author luc
 *
 */
public class ChainSample {
	@Test
	public void doIt() throws Exception {
		IWorker<Integer[], Integer> oSumWorker = new IWorker<Integer[], Integer>() {
			@Override
			public Integer perform(Integer[] pWork) throws Exception {
				int oResult = 0;
				for (Integer i : pWork) oResult += i.intValue();
				return oResult;
			}
		};
		IWorker<Integer, Double> oSqrtWorker = new IWorker<Integer, Double>() {
			@Override
			public Double perform(Integer pWork) throws Exception {
				return Math.sqrt(pWork.intValue());
			}
		};
		IWorker<Integer[], Integer[]> oSqrWorker = new IWorker<Integer[], Integer[]>() {
			@Override
			public Integer[] perform(Integer[] pWork) throws Exception {
				Integer[] oResult = new Integer[pWork.length];
				for (int i = 0; i < pWork.length; i++) oResult[i] = pWork[i] * pWork[i];
				return oResult;
			}
		};
		
		
		// Chain:
		// a,b,c --> a2,b2,c2 --> a2 + b2 + c2 --> sqrt(a2 + b2 + c2)
		Integer[] oInput = new Integer[] { 1, 2, 3 };
		Integer[] oSqr = oSqrWorker.perform(oInput);
		Integer oSum = oSumWorker.perform(oSqr);
		Double oSqrt = oSqrtWorker.perform(oSum);
		System.out.println("Sqrt = " + oSqrt);
		assertEquals(3.741, oSqrt.doubleValue(), 0.001);
		
		// Manual chaining
		ChainWorker<Integer[], Integer, Integer[]> oLinkOne = new ChainWorker<Integer[], Integer, Integer[]>(oSumWorker, oSqrWorker);
		ChainWorker<Integer[], Double, Integer> oLinkTwo = new ChainWorker<Integer[], Double, Integer>(oSqrtWorker, oLinkOne);
		oSqrt = oLinkTwo.perform(oInput);
		assertEquals(3.741, oSqrt.doubleValue(), 0.001);
		
		
		// Chaining with builder
		IWorker<Integer[], Double> oChainWorker = ChainBuilder
				.newBuilder(oSqrWorker)
				.chain(oSumWorker)
				.chain(oSqrtWorker)
				.getChain();
		oSqrt = oChainWorker.perform(oInput);
		System.out.println("Chained Sqrt = " + oSqrt);
		assertEquals(3.741, oSqrt.doubleValue(), 0.001);
		
		
		
	}
}
