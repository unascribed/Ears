package com.unascribed.ears.common.legacy;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicViewportUI;

import com.unascribed.ears.common.WritableEarsImage;

/**
 * For older versions of Minecraft that use AWT ImageIO and BufferedImage.
 */
public class AWTEarsImage implements WritableEarsImage {

	private final BufferedImage delegate;

	public AWTEarsImage(BufferedImage delegate) {
		this.delegate = delegate;
	}

	@Override
	public int getWidth() {
		return delegate.getWidth();
	}

	@Override
	public int getHeight() {
		return delegate.getHeight();
	}

	@Override
	public int getARGB(int x, int y) {
		return delegate.getRGB(x, y);
	}
	
	@Override
	public void setARGB(int x, int y, int argb) {
		delegate.setRGB(x, y, argb);
	}
	
	@Override
	public String toString() {
		return "AWTEarsImage["+getWidth()+"x"+getHeight()+", delegate="+delegate+"]";
	}
	
	@Override
	public WritableEarsImage copy() {
		WritableRaster raster = delegate.getRaster().createCompatibleWritableRaster();
		delegate.copyData(raster);
		return new AWTEarsImage(new BufferedImage(delegate.getColorModel(), raster, delegate.isAlphaPremultiplied(), null));
	}
	
}
