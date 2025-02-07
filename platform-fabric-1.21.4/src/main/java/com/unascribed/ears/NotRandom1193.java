package com.unascribed.ears;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

public class NotRandom1193 implements Random {

	public static final NotRandom1193 INSTANCE = new NotRandom1193();

	@Override
	public Random split() {
		return this;
	}

	@Override
	public RandomSplitter nextSplitter() {
		throw new UnsupportedOperationException();
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
