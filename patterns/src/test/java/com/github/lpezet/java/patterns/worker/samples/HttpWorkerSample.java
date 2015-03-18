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
package com.github.lpezet.java.patterns.worker.samples;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.lpezet.java.patterns.circuitbreaker.CircuitBreakerStrategies;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;
import com.github.lpezet.java.patterns.retry.RetryStrategies;
import com.github.lpezet.java.patterns.worker.CircuitBreakerWorker;
import com.github.lpezet.java.patterns.worker.IWorker;
import com.github.lpezet.java.patterns.worker.RetryWorker;

public class HttpWorkerSample {

	static class HttpWorker implements IWorker<HttpUriRequest, HttpResponse> {
		
		private HttpClient mClient;
		
		public HttpWorker(HttpClient pClient) {
			mClient = pClient;
		}
		
		@Override
		public HttpResponse perform(HttpUriRequest pWork) throws Exception {
			return mClient.execute(pWork);
		}
	}
	
	@Test
	public void doIt() throws Exception {
		HttpClient oHttpClient = Mockito.mock(HttpClient.class);
		when(oHttpClient.execute(Mockito.any(HttpUriRequest.class))).thenAnswer(new Answer<HttpResponse>() {
			private Random mRnd = new Random(12345);
			private int mWeight = 0;
			@Override
			public HttpResponse answer(InvocationOnMock pInvocation) throws Throwable {
				mWeight++;
				int oRnd = mRnd.nextInt(1000);
				int oThreshold = (1000 - mWeight * 250);
				//System.out.println("Rnd = " + oRnd + ", Threshold = " + oThreshold);
				if (oRnd >= oThreshold) {
					mWeight = (mRnd.nextInt(10) >= 2) ? 0 : mWeight - 1;
					throw new IOException();
				}
				return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, null));
			}
		});
		HttpWorker oWorker = new HttpWorker(oHttpClient);
		IRetryStrategy oRetryStrategy = RetryStrategies.defaultBackoffIORetryStrategy();
		RetryWorker<HttpUriRequest, HttpResponse> oRetry = new RetryWorker<HttpUriRequest,HttpResponse>(oWorker, oRetryStrategy);
		
		ICircuitBreakerStrategy oCircuiteBreakerStrategy = CircuitBreakerStrategies.newSingleTryCircuitBreakerStrategy();
		CircuitBreakerWorker<HttpUriRequest, HttpResponse> oCB = new CircuitBreakerWorker<HttpUriRequest, HttpResponse>(oRetry, oCircuiteBreakerStrategy);
		for (int i = 0; i < 33; i++) {
			try {
				Thread.sleep(70);
				oCB.perform( new HttpGet("http://something.com/" + i) );
				System.out.println(i + ": Executed!");
			} catch (Throwable t) {
				System.out.println(i + " : Got an exception: " + t.getClass().getName());
			}
		}
	}
}
