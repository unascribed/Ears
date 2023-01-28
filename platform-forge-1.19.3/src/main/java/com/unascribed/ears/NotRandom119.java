package com.unascribed.ears;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class NotRandom119 implements RandomSource {

	public static final NotRandom119 INSTANCE = new NotRandom119();


	@Override
	public RandomSource fork() {
		return this;
	}

	@Override
	public PositionalRandomFactory forkPositional() {
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
