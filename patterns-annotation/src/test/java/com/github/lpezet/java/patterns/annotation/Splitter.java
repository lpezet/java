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

import java.util.Arrays;
import java.util.Collection;

import com.github.lpezet.java.patterns.worker.IWorkSplitter;
import com.github.lpezet.java.patterns.worker.Splitters;
import com.github.lpezet.java.patterns.worker.Splitters.ISimpleSplitter;

public class Splitter  implements IWorkSplitter<ProceedingJointPointAndArgs> {
	private class MySplitter implements ISimpleSplitter<ProceedingJointPointAndArgs> {
		@Override
		public int count(ProceedingJointPointAndArgs pWork) {
			return ((Integer[]) pWork.getArgs()[0]).length;
		}
		@Override
		public ProceedingJointPointAndArgs newSplit(int pFrom, int pTo, ProceedingJointPointAndArgs pWork) {
			//System.out.println("From = " + pFrom + ", to = " + pTo);
			Integer[] oArg = (Integer[]) pWork.getArgs()[0];
			return new ProceedingJointPointAndArgs( pWork.getProceedingJoinPoint() , new Object[] { Arrays.copyOfRange(oArg, pFrom - 1, Math.min(pTo, oArg.length)) } );
		}
	}
	
	private IWorkSplitter<ProceedingJointPointAndArgs> mImpl;
	
	public Splitter() {
		mImpl = Splitters.splitByChunks(4, 1, new MySplitter());
	}
	
	@Override
	public Collection<ProceedingJointPointAndArgs> split(ProceedingJointPointAndArgs pWork) {
		return mImpl.split(pWork);
	}
}
