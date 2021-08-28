package com.unascribed.ears.common;

import java.nio.IntBuffer;

/**
 * For newer versions of Minecraft that use LWJGL3 and cannot initialize AWT.
 */
public class RawEarsImage implements EarsImage {

	private final int width;
	private final int height;
	private final IntBuffer data;
	private final boolean swapRedBlue;
	
	public RawEarsImage(int[] data, int width, int height, boolean swapRedBlue) {
		this(IntBuffer.wrap(data), width, height, swapRedBlue);
	}
	
	public RawEarsImage(IntBuffer data, int width, int height, boolean swapRedBlue) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.swapRedBlue = swapRedBlue;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getARGB(int x, int y) {
		int raw = data.get((y*width)+x);
		if (swapRedBlue) {
			int swapped = raw & 0xFF00FF00;
			swapped |= (raw&0x00FF0000)>>16;
			swapped |= (raw&0x000000FF)<<16;
			return swapped;
		}
		return raw;
	}
	
	@Override
	public String toString() {
		return "RawEarsImage["+getWidth()+"x"+getHeight()+", swapRedBlue="+swapRedBlue+", data=["+data.limit()+" entries]]";
	}
	
}
