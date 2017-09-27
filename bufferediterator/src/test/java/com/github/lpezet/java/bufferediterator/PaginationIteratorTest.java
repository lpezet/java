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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author luc
 *
 */
public class PaginationIteratorTest {
	
	private static class SimplePaginationIterator extends PaginationIterator<String> {
		
		private String mText;
		
		public SimplePaginationIterator(String pText, long pFrom, long pTo, int pIncrement) {
			super(pFrom, pTo, pIncrement);
			mText = pText;
		}

		@Override
		protected String getItems(long pFrom, long pTo) {
			System.out.println("from = " + pFrom + ", to = " + pTo);
			if (pFrom > mText.length()) return null;
			return mText.substring((int) pFrom - 1, (int) Math.min(mText.length(), pTo));
		}
	}
	
	private static final String SAMPLE_TEXT = "Bacon ipsum dolor sit amet tongue cow brisket tri-tip fatback turducken jowl ribeye jerky ball tip prosciutto frankfurter shankle pork loin corned beef. Tail strip steak ham, meatloaf bresaola jowl t-bone ham hock ground round. Doner filet mignon spare ribs drumstick rump shankle brisket bresaola shank sirloin flank ribeye leberkas tenderloin.";
	
	@Test
	public void basic() throws Exception {
		for (PaginationIterator<String> i = new SimplePaginationIterator(SAMPLE_TEXT, 1, 100, 1); i.hasNext(); ) {
			String oSubstring = i.next();
			//if (i.hasNext()) assertEquals(15, oSubstring.length());
			//else assertEquals(10, oSubstring.length()); // last one is shorter
		}
	}
	
	@Test
	public void hasNextWhenCursorEqualsTo() throws Exception {
		PaginationIterator<String> i = new SimplePaginationIterator("X", 1, 1, 1);
		assertTrue(i.hasNext());
		assertEquals("X", i.next());
		assertFalse(i.hasNext());
	}
	
}
