package com.github.lpezet.java.patterns.worker;


/**
 * 
 * Basically:
 * pLeft.peform(pRight.perform(pWork:W)):R
 * 
 * @author luc
 *
 * @param <W> Input
 * @param <R> Output
 * @param <S> Intemediary output
 */
public class ChainWorker<W,R,S> implements IWorker<W, R> {
	
	private IWorker<S, R> mLeft;
	private IWorker<W, S> mRight;
	
	public ChainWorker(IWorker<S, R> pLeft, IWorker<W, S> pRight) {
		mLeft = pLeft;
		mRight = pRight;
	}
	@Override
	public R perform(W pWork) throws Exception {
		S oResult = mRight.perform(pWork);
		return mLeft.perform(oResult);
	}
}