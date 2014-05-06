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
package com.github.lpezet.java.patterns.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luc
 *
 */
public class Splitters {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Splitters.class);

	public static interface ISimpleSplitter<W> {
		public int count(W pWork);
		public W newSplit(int pFrom, int pTo, W pWork);
	}
	
	/**
	 * Splitting in fixed amount of work. So splitting 100 into chunks of 20 will yield 5 chunks. 
	 * 
	 * @param pChunkSize
	 * @param pSplitter
	 * @return
	 */
	public static <T> IWorkSplitter<T> splitByChunkSize(final int pChunkSize, final ISimpleSplitter<T> pSplitter) {
		return new IWorkSplitter<T>() {
			@Override
			public Collection<T> split(T pWork) {
				List<T> oResult = new ArrayList<T>();
				int oAmountOfWork = pSplitter.count(pWork); 
				if (oAmountOfWork < pChunkSize) {
					LOGGER.debug("Not dividing up work: too small.");
					oResult.add(pWork);
				} else {
					int n = (int) Math.ceil( oAmountOfWork / (double) pChunkSize );
					LOGGER.debug("Dividing up work into " + n + " chunks.");
					int oTo, oFrom;
					for (int i = 0; i < n; i++) {
						oFrom = i * pChunkSize + 1;
						oTo = oFrom + pChunkSize - 1;
						oResult.add(pSplitter.newSplit(oFrom, oTo, pWork));
					}
				}
				return oResult;
			}
		};
	}
	
	/**
	 * Splitting working into fixed amount of chunks. For example, 100 split into 5 chunks will yield chunks of 20 work each.
	 * pChunks could be set to Runtime.availableProcessor() for example. 
	 * 
	 * @param pChunks
	 * @param pMinChunkSize
	 * @param pSplitter
	 * @return
	 */
	public static <T> IWorkSplitter<T> splitByChunks(final int pChunks, final int pMinChunkSize, final ISimpleSplitter<T> pSplitter) {
		return new IWorkSplitter<T>() {
			@Override
			public Collection<T> split(T pWork) {
				List<T> oResult = new ArrayList<T>();
				int oAmountOfWork = pSplitter.count(pWork); 
				if (oAmountOfWork < pMinChunkSize) {
					LOGGER.debug("Not dividing up work: too small.");
					oResult.add(pWork);
				} else {
					int oChunkSize = (int) Math.ceil( oAmountOfWork / (double) pChunks );
					LOGGER.debug("Dividing up work into " + pChunks + " chunks of " + oChunkSize + " work each.");
					int oTo, oFrom;
					for (int i = 0; i < pChunks; i++) {
						oFrom = i * oChunkSize + 1;
						oTo = oFrom + oChunkSize - 1;
						oResult.add(pSplitter.newSplit(oFrom, oTo, pWork));
					}
				}
				return oResult;
			}
		};
	}
}
