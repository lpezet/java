/**
 * 
 */
package com.github.lpezet.java.patterns.util;


/**
 * @author Luc Pezet
 *
 */
public class Assert {
	
	public static void isNotNull( Object pObj, String pMessage) {
		if ( pObj == null ) throw new IllegalStateException( pMessage );
	}
	
	public static void isTrue(boolean pCondition, String pMessage) {
		if ( !pCondition ) throw new IllegalStateException( pMessage );
	}
	
	public static void isFalse(boolean pCondition, String pMessage) {
		isTrue( ! pCondition, pMessage );
	}
}
