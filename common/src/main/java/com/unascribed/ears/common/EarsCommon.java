package com.unascribed.ears.common;

import com.unascribed.ears.common.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.EarsRenderDelegate.TexRotation;

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
	
	public static void carefullyStripAlpha(StripAlphaMethod sam, boolean sixtyFour) {
		EarsLog.debug("Common", "carefullyStripAlpha({}, {})", sam, sixtyFour);
		sam.stripAlpha(8, 0, 24, 8);
		sam.stripAlpha(0, 8, 32, 16);
		
		sam.stripAlpha(4, 16, 12, 20);
		sam.stripAlpha(20, 16, 36, 20);
		sam.stripAlpha(44, 16, 52, 20);
		
		sam.stripAlpha(0, 20, 56, 32);
		
		if (sixtyFour) {
			sam.stripAlpha(20, 48, 28, 52);
			sam.stripAlpha(36, 48, 44, 52);
			
			sam.stripAlpha(16, 52, 48, 64);
		}
	}
	
	public static void render(EarsFeatures features, EarsRenderDelegate delegate, float swingAmount) {
		EarsLog.debug("Common", "render({}, {}, {})", features, delegate, swingAmount);
		
		if (EarsLog.DEBUG) {
			delegate = new DebuggingDelegate(delegate);
		}
		
		if (features != null && features.earsEnabled) {
			if (EarsLog.DEBUG) {
				for (BodyPart part : BodyPart.values()) {
					delegate.push();
					delegate.anchorTo(part);
					delegate.renderDebugDot(1, 1, 1, 1);
					delegate.push();
					delegate.translate(part.xSize, 0, 0);
					delegate.renderDebugDot(1, 0, 0, 1);
					delegate.pop();
					delegate.push();
					delegate.translate(0, -part.ySize, 0);
					delegate.renderDebugDot(0, 1, 0, 1);
					delegate.pop();
					delegate.push();
					delegate.translate(0, 0, part.zSize);
					delegate.renderDebugDot(0, 0, 1, 1);
					delegate.pop();
					delegate.pop();
				}
			}
			delegate.push();
			delegate.anchorTo(BodyPart.HEAD);
			delegate.translate(-4, -16, 4);
			delegate.renderFront(24, 0, 16, 8, TexRotation.NONE, TexFlip.NONE);
			delegate.renderBack(56, 28, 16, 8, TexRotation.CW, TexFlip.NONE);
			delegate.pop();
			
			delegate.push();
			delegate.anchorTo(BodyPart.TORSO);
			delegate.translate(0, -2, 4);
			delegate.rotate(30+(swingAmount*40), 1, 0, 0);
			delegate.renderDoubleSided(56, 16, 8, 12, TexRotation.NONE, TexFlip.HORIZONTAL);
			delegate.pop();
		}
	}

	public static float[][] calculateUVs(int u, int v, int w, int h, TexRotation rot, TexFlip flip) {
		EarsLog.debug("Common", "calculateUVs({}, {}, {}, {}, {}, {})", u, v, w, h, rot, flip);
		float minU = u/64f;
		float minV = v/64f;
		
		float maxU = (u+(rot.transpose ? h : w))/64f;
		float maxV = (v+(rot.transpose ? w : h))/64f;
		
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
