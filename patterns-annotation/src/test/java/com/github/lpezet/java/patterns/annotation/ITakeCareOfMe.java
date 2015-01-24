/**
 * 
 */
package com.github.lpezet.java.patterns.annotation;

/**
 * @author Luc Pezet
 *
 */
public interface ITakeCareOfMe {

	public boolean doSomething() throws Exception;

	public void setBehavior(IBehavior pBehavior);
	
}
