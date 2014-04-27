/**
 * 
 */
package com.github.lpezet.java.patterns.worker;

/**
 * @author luc
 *
 */
public interface Callback<T> {

		public void onResult(T pResult);
		
		public void onException(Exception e);

}
