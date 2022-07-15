package com.unascribed.ears;

import com.mojang.blaze3d.platform.NativeImage;
import com.unascribed.ears.common.SwappedEarsImage;
import com.unascribed.ears.common.WritableEarsImage;

public class NativeImageAdapter extends SwappedEarsImage {

	private final NativeImage img;

	public NativeImageAdapter(NativeImage img) {
		this.img = img;
	}

	@Override
	public int getABGR(int x, int y) {
		return img.getPixelRGBA(x, y);
	}
	
	@Override
	public void setABGR(int x, int y, int abgr) {
		img.setPixelRGBA(x, y, abgr);
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
