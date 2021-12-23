package com.unascribed.ears.common.legacy;

import static org.lwjgl.opengl.GL11.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * A specialization of PartiallyUnmanagedEarsRenderDelegate that keeps track of auxillary GL texture
 * IDs itself.
 */
public abstract class UnmanagedEarsRenderDelegate<TPeer, TModelPart> extends PartiallyUnmanagedEarsRenderDelegate<TPeer, TModelPart> {
	
	private final Map<String, Integer> subTextures = new HashMap<String, Integer>();
	
	@Override
	protected void doBindAux(TexSource src, byte[] pngData) {
		if (pngData == null) return;
		String id = src.addSuffix(getSkinUrl());
		if (!subTextures.containsKey(id)) {
			try {
				subTextures.put(id, uploadImage(ImageIO.read(new ByteArrayInputStream(pngData))));
			} catch (IOException e) {
				subTextures.put(id, 0);
			}
		}
		glBindTexture(GL_TEXTURE_2D, subTextures.get(id));
	}
	
	protected abstract String getSkinUrl();
	protected abstract int uploadImage(BufferedImage img);
	
}
