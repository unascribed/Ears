package com.unascribed.ears;

import com.unascribed.ears.common.WritableEarsImage;

import net.minecraft.client.texture.NativeImage;

public class NativeImageAdapter implements WritableEarsImage {

	private final NativeImage img;

	public NativeImageAdapter(NativeImage img) {
		this.img = img;
	}

	@Override
	public int getARGB(int x, int y) {
		return img.getColorArgb(x, y);
	}

	@Override
	public void setARGB(int x, int y, int argb) {
		img.setColorArgb(x, y, argb);
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
		NativeImage copy = new NativeImage(img.getFormat(), img.getWidth(), img.getHeight(), false);
		copy.copyFrom(img);
		return new NativeImageAdapter(copy);
	}
}
