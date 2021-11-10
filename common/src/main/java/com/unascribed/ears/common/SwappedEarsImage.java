package com.unascribed.ears.common;

public abstract class SwappedEarsImage implements WritableEarsImage {

	@Override
	public final int getARGB(int x, int y) {
		return swap(getABGR(x, y));
	}
	
	@Override
	public final void setARGB(int x, int y, int argb) {
		setABGR(x, y, swap(argb));
	}
	
	private static int swap(int i) {
		return (i & 0xFF000000) | ((i << 16) & 0x00FF0000) | (i & 0x0000FF00) | ((i >> 16) & 0x000000FF);
	}
	
	protected abstract int getABGR(int x, int y);
	protected abstract void setABGR(int x, int y, int bgra);
	
}
