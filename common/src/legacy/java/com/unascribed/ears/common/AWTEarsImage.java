package com.unascribed.ears.common;

import java.awt.image.BufferedImage;

/**
 * For older versions of Minecraft that use AWT ImageIO and BufferedImage.
 */
public class AWTEarsImage implements EarsImage {

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
	public String toString() {
		return "AWTEarsImage["+getWidth()+"x"+getHeight()+", delegate="+delegate+"]";
	}
	
}
