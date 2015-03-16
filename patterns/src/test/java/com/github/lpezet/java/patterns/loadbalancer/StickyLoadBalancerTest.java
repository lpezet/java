/**
 * The MIT License
 * Copyright (c) 2014 Luc Pezet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
