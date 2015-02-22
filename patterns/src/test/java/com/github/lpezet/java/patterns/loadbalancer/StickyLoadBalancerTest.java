/**
 * 
 */
package com.github.lpezet.java.patterns.loadbalancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Luc Pezet
 *
 */
public class StickyLoadBalancerTest {

	@Test
	public void doIt() {
		RoundRobinLoadBalancer<String> oImpl = new RoundRobinLoadBalancer<String>();
		
		ThreadLocal<String> oHolder = new ThreadLocal<String>();
		StickyLoadBalancer<String, String> oLB = new StickyLoadBalancer<String, String>(oImpl, oHolder, null);
		
		oImpl.addResource("A","B","C");
		// First, setting up sticky map for each key
		assertTrue(oLB.hasNext());
		oHolder.set("AAA");
		assertEquals("A", oLB.next());
		assertTrue(oLB.hasNext());
		oHolder.set("BBB");
		assertEquals("B", oLB.next());
		assertTrue(oLB.hasNext());
		oHolder.set("CCC");
		assertEquals("C", oLB.next());
		
		// Now testing stickyness...
		assertTrue(oLB.hasNext());
		oHolder.set("CCC");
		assertEquals("C", oLB.next());
		assertTrue(oLB.hasNext());
		oHolder.set("BBB");
		assertEquals("B", oLB.next());
		assertTrue(oLB.hasNext());
		oHolder.set("AAA");
		assertEquals("A", oLB.next());
		
		oHolder.remove();
	}
}
