package com.unascribed.ears.common;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.unascribed.ears.api.Slice;
import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.WingMode;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;
import com.unascribed.ears.common.util.BitInputStream;

/**
 * Entrypoint to methods that are common to all ports of Ears.
 */
public class EarsCommon {

	private static final ThreadLocal<float[][]> uvScratch = new ThreadLocal<float[][]>() {
		@Override
		protected float[][] initialValue() {
			return new float[][] {
				{0, 0},
				{0, 0},
				{0, 0},
				{0, 0}
			};
		}
	};
	
	public interface StripAlphaMethod {
		void stripAlpha(int x1, int y1, int x2, int y2);
	}
	
	public static final class Rectangle {
		public final int x1, y1, x2, y2;

		public Rectangle(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
	}
	
	/**
	 * A list of rectangles in a 64x64 skin that are forced to have their translucency removed by
	 * the vanilla skin loader.
	 */
	public static final List<Rectangle> FORCED_OPAQUE_REGIONS = Collections.unmodifiableList(Arrays.asList(
			new Rectangle(8, 0, 24, 8),
			new Rectangle(0, 8, 32, 16),
			
			new Rectangle(4, 16, 12, 20),
			new Rectangle(20, 16, 36, 20),
			new Rectangle(44, 16, 52, 20),
			
			new Rectangle(0, 20, 56, 32),
			
			new Rectangle(20, 48, 28, 52),
			new Rectangle(36, 48, 44, 52),
				
			new Rectangle(16, 52, 48, 64)
		));
	
	/**
	 * Call {@code sam} with every rectangle in {@link #FORCED_OPAQUE_REGIONS}.
	 * <p>
	 * Needed because vanilla minecraft uses rough rectangles that mess up a ton of unused pixels,
	 * which we give a use to.
	 */
	public static void carefullyStripAlpha(StripAlphaMethod sam, boolean sixtyFour) {
		EarsLog.debug(EarsLog.Tag.COMMON, "carefullyStripAlpha({}, {})", sam, sixtyFour);
		for (Rectangle rect : FORCED_OPAQUE_REGIONS) {
			if (!sixtyFour && rect.y1 > 32) continue;
			sam.stripAlpha(rect.x1, rect.y1, rect.x2, rect.y2);
		}
	}
	
	public static float lerpDelta(float prev, float cur, float tickDelta) {
		return prev + ((cur-prev)*tickDelta);
	}
	
	public static double lerpDelta(double prev, double cur, float tickDelta) {
		return prev + ((cur-prev)*tickDelta);
	}
	
	public static boolean shouldSuppressElytra(EarsFeatures features) {
		return features != null && features.animateWings && features.wingMode != WingMode.NONE;
	}
	
	public static String getConfigPreviewUrl() {
		return "https://ears.unascribed.com/manipulator/";
	}
	
	public static String getConfigUrl(String username, String uuid) {
		return "https://ears.unascribed.com/manipulator/#v="+EarsVersion.COMMON+(uuid == null ? ",username="+username : ",id="+uuid);
	}
	
	public static AlfalfaData preprocessSkin(WritableEarsImage img) {
		AlfalfaData a = Alfalfa.read(img);
		Slice erase = a.data.get("erase");
		if (erase != null) {
			BitInputStream bis = new BitInputStream(new ByteArrayInputStream(erase.toByteArray()));
			int count = 0;
			try {
				while (true) {
					int x = bis.read(6); // 0-63
					int y = bis.read(6);
					int w = bis.read(5)+1; // 1-32
					int h = bis.read(5)+1;
					for (int xi = x; xi < x+w; xi++) {
						for (int yi = y; yi < y+h; yi++) {
							img.setARGB(xi, yi, 0);
						}
					}
					count++;
				}
			} catch (EOFException e) {
			} catch (IOException e) {
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Exception while parsing eraser data", e);
			}
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Discovered and applied {} eraser rectangle{}", count, count == 1 ? "" : "s");
		} else {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Discovered no eraser data");
		}
		return a;
	}
	
	/**
	 * Render all the features described in {@code features} using {@code delegate}.
	 */
	public static void render(EarsFeatures features, EarsRenderDelegate delegate) {
		EarsRenderer.render(features, delegate);
	}

	/**
	 * Calculate the needed UVs to render a quad with the given rotation and flip of the given
	 * texture.
	 */
	public static float[][] calculateUVs(int u, int v, int w, int h, TexRotation rot, TexFlip flip, TexSource src) {
		return calculateUVs(u, v, w, h, rot, flip, src, 0);
	}
	
	/**
	 * Calculate the needed UVs to render a quad with the given rotation and flip of the given
	 * texture, "pinching" the UVs in by the specified amount to avoid UV bleed.
	 */
	public static float[][] calculateUVs(int u, int v, int w, int h, TexRotation rot, TexFlip flip, TexSource src, float pinch) {
		EarsLog.debug(EarsLog.Tag.COMMON_RENDERER, "calculateUVs(u={}, v={}, w={}, h={}, rot={}, flip={}, src={})", u, v, w, h, rot, flip, src);
		float tw = src.getWidth();
		float th = src.getHeight();
		
		float minU = (u/tw)+pinch;
		float minV = (v/th)+pinch;
		
		float maxU = ((u+(rot.transpose ? h : w))/tw)-pinch;
		float maxV = ((v+(rot.transpose ? w : h))/th)-pinch;
		
		if (rot.transpose) {
			if (flip == TexFlip.HORIZONTAL) flip = TexFlip.VERTICAL;
			else if (flip == TexFlip.VERTICAL) flip = TexFlip.HORIZONTAL;
		}
		
		if (flip == TexFlip.HORIZONTAL || flip == TexFlip.BOTH) {
			float swap = maxU;
			maxU = minU;
			minU = swap;
		}
		if (flip == TexFlip.VERTICAL || flip == TexFlip.BOTH) {
			float swap = maxV;
			maxV = minV;
			minV = swap;
		}
		
		float[][] uv = uvScratch.get();
		
		uv[0][0] = minU;
		uv[0][1] = maxV;
		uv[1][0] = maxU;
		uv[1][1] = maxV;
		uv[2][0] = maxU;
		uv[2][1] = minV;
		uv[3][0] = minU;
		uv[3][1] = minV;
		
		if (rot == TexRotation.CW) {
			float[] swap = uv[3];
			uv[3] = uv[2];
			uv[2] = uv[1];
			uv[1] = uv[0];
			uv[0] = swap;
		} else if (rot == TexRotation.CCW) {
			float[] swap = uv[0];
			uv[0] = uv[1];
			uv[1] = uv[2];
			uv[2] = uv[3];
			uv[3] = swap;
		} else if (rot == TexRotation.UPSIDE_DOWN) {
			float[] swap = uv[0];
			float[] swap2 = uv[1];
			uv[0] = uv[2];
			uv[1] = uv[3];
			uv[2] = swap;
			uv[3] = swap2;
		}
		return uv;
	}
	
	
}
