package com.unascribed.ears.common.util;

import java.util.Random;

/**
 * A Random that always returns 0. Useful for versions of Minecraft where the cuboid list in a
 * model part is private, but there's a getRandomCuboid method that accepts a custom Random instance.
 */
public class NotRandom extends Random {

	public static final NotRandom INSTANCE = new NotRandom();
	
	@Override
	public int nextInt() {
		return 0;
	}
	
	@Override
	public int nextInt(int bound) {
		return 0;
	}
	
}
