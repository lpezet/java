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
package com.github.lpezet.java.bufferediterator.samples;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.github.lpezet.java.bufferediterator.BufferedIterator;
import com.github.lpezet.java.bufferediterator.PaginationIterator;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;

/**
 * @author luc
 * 
 */
public class GoogleBooksSearchSample {

	public static final String BOOKS_GDATA_SERVER = "http://www.googleapis.com/books/v1/volumes";
	private static final String API_KEY = "";
	private static final String APPLICATION_NAME = "lpezet-bufferediterator";

	private static class GoogleBooksSearch implements Iterable<Volume> {

		private Iterator<Volume> mIterator;

		public GoogleBooksSearch(String pQuery) throws Exception {
			this(pQuery, 10);
		}

		public GoogleBooksSearch(String pQuery, int pBatchSize)
				throws Exception {
			mIterator = new BufferedIterator<Volume, List<Volume>>(
					new GoogleBooksSearchIterator(pQuery, pBatchSize));
		}

		@Override
		public Iterator<Volume> iterator() {
			return mIterator;
		}
	}

	private static class GoogleBooksSearchIterator extends
			PaginationIterator<List<Volume>> {

		private com.google.api.services.books.Books.Volumes.List mSearch;

		public GoogleBooksSearchIterator(String pQuery, int pMaxResults)
				throws Exception {
			super(1, 20, pMaxResults);
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			final Books books = new Books.Builder(
					GoogleNetHttpTransport.newTrustedTransport(), jsonFactory,
					null)
					.setApplicationName(APPLICATION_NAME)
					.setGoogleClientRequestInitializer(
							new BooksRequestInitializer(API_KEY)).build();
			System.out.println("Query: [" + pQuery + "]");
			mSearch = books.volumes().list(pQuery);
			mSearch.setMaxResults(1l);
			setTo(mSearch.execute().getTotalItems());

			mSearch.setMaxResults(Long.valueOf(pMaxResults));
			mSearch.setOrderBy("newest");
		}

		@Override
		protected List<Volume> getItems(long pFrom, long pTo) {
			System.out.println("###################### From = " + pFrom);
			mSearch.setStartIndex(pFrom);
			try {
				return mSearch.execute().getItems();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
	}

	public static void main(String[] pArgs) throws Exception {
		//Google
	}

	public void iteratorLoop() throws Exception {
		for (Iterator<Volume> i = new BufferedIterator<Volume, List<Volume>>(new GoogleBooksSearchIterator("inauthor:\"marvin lee minsky\"", 10)); i.hasNext();) {
			Volume volume = i.next();
			printVolumeInfo(volume);
		}
	}
	
	public void iterableLoop() throws Exception {
		for (Volume v : new GoogleBooksSearch("inauthor:\"marvin lee minsky\"")) {
			printVolumeInfo(v);
		}
	}

	private void printVolumeInfo(Volume volume) {
		Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
		// Volume.SaleInfo saleInfo = volume.getSaleInfo();
		System.out.println("==========");
		System.out.println("Id: " + volume.getId());
		// Title.
		System.out.println("Title: " + volumeInfo.getTitle());
		// Author(s).
		java.util.List<String> authors = volumeInfo.getAuthors();
		if (authors != null && !authors.isEmpty()) {
			System.out.print("Author(s): ");
			for (int j = 0; j < authors.size(); ++j) {
				System.out.print(authors.get(j));
				if (j < authors.size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}

	

}
