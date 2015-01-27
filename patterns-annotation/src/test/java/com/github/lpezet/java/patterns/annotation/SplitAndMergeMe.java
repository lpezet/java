/**
 * 
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
