/**
 * 
 */
package com.github.lpezet.java.cache;

/**
 * @author luc
 *
 */
public interface CommandAugmentedItem<T> {

	public T getValue();
	public Command<T> getCommand();
	
}
