/**
 * 
 */
package com.github.lpezet.java.cache;

/**
 * @author luc
 *
 */
public class BasicCommandAugmentedItem<T> implements CommandAugmentedItem<T> {

	private T mValue;
	private Command<T> mCommand;
	
	public BasicCommandAugmentedItem() {
	}
	
	public BasicCommandAugmentedItem(T pValue, Command<T> pCommand) {
		mValue = pValue;
		mCommand = pCommand;
	}
	
	public T getValue() {
		return mValue;
	}

	public Command<T> getCommand() {
		return mCommand;
	}

}
