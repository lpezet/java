/**
 * 
 */
package com.github.lpezet.java.patterns.throttle;

/**
 * @author Luc Pezet
 *
 */
public class ThrottledException extends RuntimeException {

	private static final long serialVersionUID = 3889847517101513987L;

	public ThrottledException() {
		super();
	}

	public ThrottledException(String pMessage, Throwable pCause,
			boolean pEnableSuppression, boolean pWritableStackTrace) {
		super(pMessage, pCause, pEnableSuppression, pWritableStackTrace);
	}

	public ThrottledException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	public ThrottledException(String pMessage) {
		super(pMessage);
	}

	public ThrottledException(Throwable pCause) {
		super(pCause);
	}

}
