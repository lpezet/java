/**
 * The MIT License
 * Copyright (c) 2015 Luc Pezet
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Luc Pezet
 *
 */
public class SplitAndMergeMe implements ISplitAndMergeMe {
	
	private Logger mLogger = LoggerFactory.getLogger(this.getClass());

	@DivideAndConquer(merger=Merger.class, splitter=Splitter.class)
	@Override
	public long addUp(Integer[] pOperands) {
		mLogger.info("Thread[" + Thread.currentThread().getName() + "] Adding up " + pOperands.length + " operands...");
		long oStart = System.currentTimeMillis();
		long oResult = 0;
		for (int o : pOperands) oResult += o;
		long oEnd = System.currentTimeMillis();
		mLogger.info("Thread[" + Thread.currentThread().getName() + "] Done in " + (oEnd - oStart) + "ms.");
		return oResult;
	}
}
