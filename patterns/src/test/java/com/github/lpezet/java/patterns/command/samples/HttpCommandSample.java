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
package com.github.lpezet.java.patterns.command.samples;

import static org.mockito.Mockito.when;

import java.io.IOException;

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
import com.github.lpezet.java.patterns.command.BaseCommand;
import com.github.lpezet.java.patterns.command.Callback;
import com.github.lpezet.java.patterns.command.CircuitBreakerCommand;
import com.github.lpezet.java.patterns.command.RetryCommand;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;
import com.github.lpezet.java.patterns.retry.RetryStrategies;

/**
 * @author luc
 *
 */
public class HttpCommandSample {
	
	private static class HttpCommand extends BaseCommand<Void> {
		
		private HttpClient mHttpClient;
		private HttpUriRequest mHttpRequest;
		private Callback<HttpResponse> mCallback;
		
		public HttpCommand(HttpClient pHttpClient, HttpUriRequest pHttpRequest, Callback<HttpResponse> pCallback) {
			mHttpClient = pHttpClient;
			mHttpRequest = pHttpRequest;
			mCallback = pCallback;
		}
		
		@Override
		public Void execute() throws Exception {
			HttpResponse oResponse = mHttpClient.execute(mHttpRequest);
			mCallback.onResult(oResponse);
			return null;
		}
		
		
	}

	@Test
	public void doIt() throws Exception {
		HttpClient oHttpClient = Mockito.mock(HttpClient.class);
		when(oHttpClient.execute(Mockito.any(HttpUriRequest.class))).thenAnswer(new Answer<HttpResponse>() {
			
			private int mExecutions = 0;
			
			@Override
			public HttpResponse answer(InvocationOnMock pInvocation) throws Throwable {
				mExecutions++;
				
				if (mExecutions <= 7) throw new IOException();
				return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, null));
			}
		});
		HttpGet oHttpRequest = new HttpGet("http://localhost:8080/test");
		Callback<HttpResponse> oCallback = new Callback<HttpResponse>() {
			@Override
			public void onResult(HttpResponse pResponse) {
				System.out.println("Got response!!!");
				//Exception e = new Exception();
				//e.printStackTrace();
			}
			
			@Override
			public void onException(Exception e) {
				System.err.println("Got exception!");
				e.printStackTrace(System.err);
			}
		};
		HttpCommand oHttpCommand = new HttpCommand(oHttpClient, oHttpRequest, oCallback);
		IRetryStrategy oRetryStrategy = RetryStrategies.defaultBackoffIORetryStrategy();
		RetryCommand<Void> oRetry = new RetryCommand<Void>(oHttpCommand, oRetryStrategy);
		
		ICircuitBreakerStrategy oCircuiteBreakerStrategy = CircuitBreakerStrategies.newSingleTryCircuitBreakerStrategy(); 
		CircuitBreakerCommand<Void> oCB = new CircuitBreakerCommand<Void>(oRetry, oCircuiteBreakerStrategy);
		
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(70);
				oCB.execute();
				System.out.println(i + ": Executed!");
			} catch (Throwable t) {
				System.out.println(i + " : Got an exception: " + t.getClass().getName());
			}
		}
	}

}
