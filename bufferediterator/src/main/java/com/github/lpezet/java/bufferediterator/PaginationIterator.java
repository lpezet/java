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
/**
 * 
 */
package com.github.lpezet.java.bufferediterator;

/**
 * @author luc
 *
 */
public abstract class PaginationIterator<T> implements IIterator<T> {
	
	private long mTo;
	private int mIncrement;
	
	private long mCursor;
	
	public PaginationIterator(long pFrom, long pTo, int pIncrement) {
		mTo = pTo;
		mIncrement = pIncrement;
		mCursor = pFrom;
	}

	public T next() {
		if (!hasNext()) return null;
		long oFrom = mCursor;
		long oTo = Math.min(mTo, mCursor + mIncrement - 1);		
		T oItems = getItems(oFrom, oTo);
		mCursor += mIncrement;
		return oItems;
	}
	
	public boolean hasNext() {
		return (mCursor <= mTo);
	}
	
	public void remove() {
		throw new RuntimeException("Not supported.");
	}
	
	protected abstract T getItems(long pFrom, long pTo);
	
	protected void setTo(int pValue) {
		mTo = pValue;
	}
	
	public long getCursor() {
		return mCursor;
	}
	public int getIncrement() {
		return mIncrement;
	}
	public long getTo() {
		return mTo;
	}

}
