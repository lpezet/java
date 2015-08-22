/**
 * 
 */
package com.github.lpezet.java.cache.annotation;

import java.util.Date;

/**
 * @author Luc Pezet
 *
 */
public class MyClient {

	@ProactiveCache
	public String sayHello(String pName) {
		return "Hello " + pName + ". It is " + new Date().getTime() + ".";
	}
	
	@ProactiveCache
	public void dontCacheMe(String pName) {
		// nop
	}
}
