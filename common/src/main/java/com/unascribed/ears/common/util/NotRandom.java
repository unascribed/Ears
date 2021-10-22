package com.unascribed.ears.common.util;

import java.util.Random;

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
