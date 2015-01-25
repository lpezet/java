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
		mImpl = Splitters.splitByChunks(3, 1, new MySplitter());
	}
	
	@Override
	public Collection<ProceedingJointPointAndArgs> split(ProceedingJointPointAndArgs pWork) {
		return mImpl.split(pWork);
	}
}
