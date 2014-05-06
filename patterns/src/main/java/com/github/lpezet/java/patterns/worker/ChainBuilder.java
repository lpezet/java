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