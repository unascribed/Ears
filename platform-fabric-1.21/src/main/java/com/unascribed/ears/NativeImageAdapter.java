package com.unascribed.ears;

import com.unascribed.ears.common.SwappedEarsImage;
import com.unascribed.ears.common.WritableEarsImage;

import net.minecraft.client.texture.NativeImage;

public class NativeImageAdapter extends SwappedEarsImage {

	private final NativeImage img;

	public NativeImageAdapter(NativeImage img) {
		this.img = img;
	}

	@Override
	public int getABGR(int x, int y) {
		return img.getColor(x, y);
	}
	
	@Override
	public void setABGR(int x, int y, int abgr) {
		img.setColor(x, y, abgr);
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
