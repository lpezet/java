package com.github.lpezet.java.patterns.annotation;

import org.aspectj.lang.ProceedingJoinPoint;

public final class ProceedingJointPointAndArgs {
	private ProceedingJoinPoint mProceedingJoinPoint;
	private Object[] mArgs;
	
	public ProceedingJointPointAndArgs(ProceedingJoinPoint pProceedingJoinPoint, Object[] pArgs) {
		mProceedingJoinPoint = pProceedingJoinPoint;
		mArgs = pArgs;
	}
	
	public Object[] getArgs() {
		return mArgs;
	}
	
	public ProceedingJoinPoint getProceedingJoinPoint() {
		return mProceedingJoinPoint;
	}
}