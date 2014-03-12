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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author luc
 *
 */
public class BufferedIteratorTest {
	
	private static class WordsGroupedByPrefixIterator implements IIterator<List<String>> {
		
		private List<List<String>> mItems = new ArrayList<List<String>>();
		private int mCursor = 0;
		
		public WordsGroupedByPrefixIterator(String pText) {
			Map<Character, List<String>> oPrefixToWords = new HashMap<Character, List<String>>();
			for (String oWord : pText.split(" ")) {
				List<String> oWords = oPrefixToWords.get(oWord.charAt(0));
				if (oWords == null) {
					oWords = new ArrayList<String>();
					oPrefixToWords.put(oWord.charAt(0), oWords);
				}
				oWords.add(oWord);
			}
			for (List<String> oItem : oPrefixToWords.values()) {
				mItems.add(oItem);
			}
		}
		
		@Override
		public boolean hasNext() {
			return mCursor < mItems.size();
		}
		@Override
		public List<String> next() {
			List<String> oResult = mItems.get(mCursor);
			mCursor++;
			return oResult;
		}
		@Override
		public void remove() {
			throw new RuntimeException("Not supported.");
		}
	}
	
	private static final String SAMPLE_TEXT = "Bacon ipsum dolor sit amet tongue cow brisket tri-tip fatback turducken jowl ribeye jerky ball tip prosciutto frankfurter shankle pork loin corned beef. Tail strip steak ham, meatloaf bresaola jowl t-bone ham hock ground round. Doner filet mignon spare ribs drumstick rump shankle brisket bresaola shank sirloin flank ribeye leberkas tenderloin.";

	@Test
	public void basic() throws Exception {
		String[] oExpectedWords = SAMPLE_TEXT.split(" ");
		BufferedIterator<String, List<String>> i = new BufferedIterator<String, List<String>>(new WordsGroupedByPrefixIterator(SAMPLE_TEXT));
		List<String> oWords = new ArrayList<String>();
		for (;i.hasNext();) {
			String w = i.next();
			oWords.add(w);
		}
		for (String s : oExpectedWords) {
			if (!oWords.contains(s)) fail("Missing word: " + s);
		}
	}
}
