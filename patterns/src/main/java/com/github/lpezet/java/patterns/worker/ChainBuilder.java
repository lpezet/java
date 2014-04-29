package com.github.lpezet.java.patterns.worker;


/**
 * @author luc
 *
 */
public class ChainBuilder<W,R> {
	
	private IWorker<W, R> mChain;
	
	private ChainBuilder(IWorker<W, R> pWorker) {
		mChain = pWorker;
	}
	
	public static <W,R> ChainBuilder<W, R> newBuilder(IWorker<W, R> pWorker) {
		return new ChainBuilder<W, R>(pWorker);
	}
	
	public <R2> ChainBuilder<W,R2> chain(IWorker<R, R2> pNextWorker) {
		return new ChainBuilder<W, R2>(new ChainWorker<W, R2, R>(pNextWorker, mChain));
	}
	
	public IWorker<W, R> getChain() {
		return mChain;
	}
}