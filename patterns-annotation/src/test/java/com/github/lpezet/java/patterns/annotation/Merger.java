package com.github.lpezet.java.patterns.annotation;

import java.util.Collection;

import com.github.lpezet.java.patterns.worker.IResultMerger;

public class Merger implements IResultMerger<Object> {
	@Override
	public Object merge(Collection<Object> pSource) {
		long oResult = 0;
		for (Object i : pSource) {
			oResult += ((Long) i).longValue();
		}
		return oResult;
	}
}
