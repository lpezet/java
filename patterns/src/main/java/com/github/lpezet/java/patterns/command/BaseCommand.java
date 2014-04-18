/**
 * 
 */
package com.github.lpezet.java.patterns.command;

/**
 * @author luc
 *
 */
public abstract class BaseCommand<T> implements ICommand<T> {

	@Override
	public T call() throws Exception {
		return execute();
	}
}
