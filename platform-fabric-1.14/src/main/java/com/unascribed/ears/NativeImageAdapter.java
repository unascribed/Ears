package com.unascribed.ears;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;

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
		return img.getPixelRgba(x, y);
	}
	
	@Override
	public void setABGR(int x, int y, int abgr) {
		img.setPixelRgba(x, y, abgr);
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
