package com.unascribed.ears;

import com.mojang.blaze3d.platform.NativeImage;
import com.unascribed.ears.common.SwappedEarsImage;
import com.unascribed.ears.common.WritableEarsImage;

public class NativeImageAdapter implements WritableEarsImage {

	private final NativeImage img;

	public NativeImageAdapter(NativeImage img) {
		this.img = img;
	}

	@Override
	public int getARGB(int x, int y) {
		return img.getPixel(x, y);
	}
	
	@Override
	public void setARGB(int x, int y, int abgr) {
		img.setPixel(x, y, abgr);
	}

	@Override
	public int getHeight() {
		return img.getHeight();
	}

	@Override
	public int getWidth() {
		return img.getWidth();
	}

	@Override
	public WritableEarsImage copy() {
		NativeImage copy = new NativeImage(img.format(), img.getWidth(), img.getHeight(), false);
		copy.copyFrom(img);
		return new NativeImageAdapter(copy);
	}
}
