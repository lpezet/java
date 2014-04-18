/**
 * 
 */
package com.github.lpezet.java.patterns.supervisor;

/**
 * @author luc
 *
 */
public class TimeoutException extends RuntimeException {

	private static final long serialVersionUID = -8177742197414428497L;

	public TimeoutException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TimeoutException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
		// TODO Auto-generated constructor stub
	}

	public TimeoutException(String pMessage) {
		super(pMessage);
		// TODO Auto-generated constructor stub
	}

	public TimeoutException(Throwable pCause) {
		super(pCause);
		// TODO Auto-generated constructor stub
	}

}
