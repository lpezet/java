/**
 * 
 */
package com.github.lpezet.java.patterns.samples;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
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

import com.github.lpezet.java.patterns.activity.CircuitBreakerActivity;
import com.github.lpezet.java.patterns.activity.IActivity;
import com.github.lpezet.java.patterns.activity.IContext;
import com.github.lpezet.java.patterns.activity.RetryActivity;
import com.github.lpezet.java.patterns.circuitbreaker.CircuitBreakerStrategies;
import com.github.lpezet.java.patterns.circuitbreaker.ICircuitBreakerStrategy;
import com.github.lpezet.java.patterns.retry.IRetryStrategy;
import com.github.lpezet.java.patterns.retry.RetryStrategies;

/**
 * @author luc
 *
 */
public class HttpActivitySample {

	static class HttpActivity implements IActivity<HttpResponse> {
		
		private HttpClient mClient;
		
		public HttpActivity(HttpClient pClient) {
			mClient = pClient;
		}
		
		@Override
		public HttpResponse start(IContext pContext) throws Exception {
			IHttpContext oCtxt = (IHttpContext) pContext;
			return mClient.execute(oCtxt.getRequest());
		}
	}
	
	static interface IHttpContext extends IContext {
		HttpUriRequest getRequest();
	}
	
	static class HttpContext implements IHttpContext {
		
		private HttpUriRequest mRequest;
		
		public HttpContext(HttpUriRequest pRequest) {
			mRequest = pRequest;
		}
		
		@Override
		public HttpUriRequest getRequest() {
			return mRequest;
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
				if (oRnd >= (1000 - mWeight * 250)) {
					mWeight = (mRnd.nextInt(10) >= 2) ? 0 : mWeight - 1;
					throw new IOException();
				}
				return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, null));
			}
		});
		HttpActivity oActivity = new HttpActivity(oHttpClient);
		IRetryStrategy oRetryStrategy = RetryStrategies.getDefaultBackoffIORetryStrategy();
		RetryActivity<HttpResponse> oRetry = new RetryActivity<HttpResponse>(oActivity, oRetryStrategy);
		
		ICircuitBreakerStrategy oCircuiteBreakerStrategy = CircuitBreakerStrategies.newSingleTryCircuitBreakerStrategy();
		CircuitBreakerActivity<HttpResponse> oCB = new CircuitBreakerActivity<HttpResponse>(oRetry, oCircuiteBreakerStrategy);
		HttpGet oGet = new HttpGet();
		HttpContext oCtxt = new HttpContext(oGet);
		for (int i = 0; i < 25; i++) {
			try {
				Thread.sleep(70);
				oGet.setURI(new URI("http://test.com/" + i));
				oCB.start(oCtxt);
				System.out.println(i + ": Executed!");
			} catch (Throwable t) {
				System.out.println(i + " : Got an exception: " + t.getClass().getName());
			}
		}
	}
}
