/**
 * 
 */
package com.github.lpezet.java.patterns.loadbalancer;

import java.security.SecureRandom;

/**
 * @author Luc Pezet
 *
 */
public class RandomLoadBalancer<T> extends SimpleListLoadBalancer<T> {

	private static interface IRandomizer {
		double next();
	}

	private static class SecureRandomizer implements IRandomizer {
		private SecureRandom mSecureRandom;

		public SecureRandomizer(SecureRandom pRandom) {
			mSecureRandom = pRandom;
		}

		@Override
		public double next() {
			return mSecureRandom.nextDouble();
		}
	}

	private static class MathRandomizer implements IRandomizer {

		@Override
		public double next() {
			return Math.random();
		}
	}

	private IRandomizer mRandomizer = null;

	public RandomLoadBalancer(SecureRandom pRandom) {
		mRandomizer = new SecureRandomizer(pRandom);
	}

	public RandomLoadBalancer() {
		mRandomizer = new MathRandomizer();
	}

	@Override
	protected T pickResource() {
		synchronized (mLock) {
			int oSize = getResources().size();
			int i = (int) Math.round(mRandomizer.next() * oSize);
			return getResources().get(i % oSize);
		}
	}

}
