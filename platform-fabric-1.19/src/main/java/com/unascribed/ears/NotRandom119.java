package com.unascribed.ears;

import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.math.random.RandomDeriver;

public class NotRandom119 implements AbstractRandom {

	public static final NotRandom119 INSTANCE = new NotRandom119();
	
	@Override
	public RandomDeriver createRandomDeriver() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AbstractRandom derive() {
		return this;
	}

	@Override
	public boolean nextBoolean() {
		return false;
	}

	@Override
	public double nextDouble() {
		return 0;
	}

	@Override
	public float nextFloat() {
		return 0;
	}

	@Override
	public double nextGaussian() {
		return 0;
	}

	@Override
	public int nextInt() {
		return 0;
	}

	@Override
	public int nextInt(int bound) {
		return 0;
	}

	@Override
	public long nextLong() {
		return 0;
	}

	@Override
	public void setSeed(long seed) {
		
	}

}
