/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;


/**
 * @author Luc Pezet
 *
 */
public class SplitAndMergeMe implements ISplitAndMergeMe {

	//DivideAndConquer(merger=Merger.class, splitter=Splitter.class)
	@Override
	public long addUp(Integer[] pOperands) {
		System.out.println("Thread[" + Thread.currentThread().getName() + "] Adding up " + pOperands.length + " operands...");
		long oStart = System.currentTimeMillis();
		long oResult = 0;
		for (int o : pOperands) oResult += o;
		long oEnd = System.currentTimeMillis();
		System.out.println("Thread[" + Thread.currentThread().getName() + "] Done in " + (oEnd - oStart) + "ms.");
		return oResult;
	}
}
