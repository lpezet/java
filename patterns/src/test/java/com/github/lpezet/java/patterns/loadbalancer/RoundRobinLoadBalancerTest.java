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
public class RoundRobinLoadBalancerTest {

	@Test
	public void simple() {
		RoundRobinLoadBalancer<String> oLB = new RoundRobinLoadBalancer<String>();
		oLB.addResource("A","B","C");
		
		assertTrue(oLB.hasNext());
		assertEquals("A", oLB.next());
		assertTrue(oLB.hasNext());
		assertEquals("B", oLB.next());
		assertTrue(oLB.hasNext());
		assertEquals("C", oLB.next());
		assertTrue(oLB.hasNext());
		assertEquals("A", oLB.next());
		
	}
}
