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