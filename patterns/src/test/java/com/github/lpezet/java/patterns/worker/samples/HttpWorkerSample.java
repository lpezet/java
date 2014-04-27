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
import com.github.lpezet.java.patterns.worker.IResult;
import com.github.lpezet.java.patterns.worker.IWork;
import com.github.lpezet.java.patterns.worker.IWorker;
import com.github.lpezet.java.patterns.worker.RetryWorker;

public class HttpWorkerSample {
	
	static class HttpWork implements IWork {
		private HttpUriRequest mRequest;
		public HttpWork(HttpUriRequest pRequest) {
			mRequest = pRequest;
		}
		public HttpUriRequest getRequest() {
			return mRequest;
		}
	}
	
	static class HttpResult implements IResult {
		private HttpResponse mResponse;
		public HttpResult(HttpResponse pResponse) {
			mResponse = pResponse;
		}
		public HttpResponse getResponse() {
			return mResponse;
		}
	}

	static class HttpWorker implements IWorker<HttpWork, HttpResult> {
		
		private HttpClient mClient;
		
		public HttpWorker(HttpClient pClient) {
			mClient = pClient;
		}
		
		@Override
		public HttpResult perform(HttpWork pWork) throws Exception {
			HttpResponse oResponse = mClient.execute(pWork.getRequest());
			return new HttpResult(oResponse);
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
		RetryWorker<HttpWork, HttpResult> oRetry = new RetryWorker<HttpWork,HttpResult>(oWorker, oRetryStrategy);
		
		ICircuitBreakerStrategy oCircuiteBreakerStrategy = CircuitBreakerStrategies.newSingleTryCircuitBreakerStrategy();
		CircuitBreakerWorker<HttpWork, HttpResult> oCB = new CircuitBreakerWorker<HttpWork, HttpResult>(oRetry, oCircuiteBreakerStrategy);
		for (int i = 0; i < 25; i++) {
			try {
				Thread.sleep(70);
				oCB.perform( new HttpWork(new HttpGet("http://something.com/" + i)));
				System.out.println(i + ": Executed!");
			} catch (Throwable t) {
				System.out.println(i + " : Got an exception: " + t.getClass().getName());
			}
		}
	}
}
