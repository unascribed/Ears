package com.unascribed.ears.common;

import com.unascribed.ears.common.EarsFeatures.EarAnchor;
import com.unascribed.ears.common.EarsFeatures.EarMode;
import com.unascribed.ears.common.EarsFeatures.TailMode;
import com.unascribed.ears.common.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.EarsRenderDelegate.QuadGrow;
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
		
		if (features != null && features.enabled) {
			EarMode earMode = features.earMode;
			EarAnchor earAnchor = features.earAnchor;
			
			if (earMode == EarMode.ABOVE || earMode == EarMode.AROUND) {
				delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				delegate.push();
				delegate.translate(-4, -16, 0);
				delegate.renderFront(24, 0, 16, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 16, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
				if (earMode == EarMode.AROUND) {
					delegate.translate(-4, -8, 0);
					delegate.renderFront(36, 16, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(12, 16, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					
					delegate.translate(12, 0, 0);
					delegate.renderFront(36, 32, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(12, 32, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				}
				delegate.pop();
			} else if (earMode == EarMode.SIDES) {
				delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				delegate.translate(-8, -8, 0);
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.translate(16, 0, 0);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			} else if (earMode == EarMode.BEHIND) {
				delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-16, -8, 0);
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.rotate(180, 0, 1, 0);
				delegate.translate(-8, 0, -8);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			} else if (earMode == EarMode.FLOPPY) {
				delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-8, -7, 0);
				delegate.rotate(-30, 1, 0, 0);
				delegate.translate(0, 0, 0);
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
				delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.rotate(-90, 0, 1, 0);
				delegate.translate(0, -7, -8);
				delegate.rotate(-30, 1, 0, 0);
				delegate.translate(0, 0, 0);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			} else if (earMode == EarMode.CROSS) {
				delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				delegate.translate(4, -16, 0);
				delegate.push();
				delegate.rotate(45, 0, 1, 0);
				delegate.translate(-4, 0, 0);
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
				delegate.push();
				delegate.rotate(-45, 0, 1, 0);
				delegate.translate(-4, 0, 0);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
				delegate.pop();
			}
			
			TailMode tailMode = features.tailMode;
			
			if (tailMode != TailMode.NONE) {
				float ang = 0;
				float swing = 0;
				if (tailMode == TailMode.DOWN) {
					ang = 30;
					swing = 40;
				} else if (tailMode == TailMode.BACK) {
					if (features.tailBend0 != 0) {
						ang = 90;
					} else {
						ang = 80;
					}
					swing = 20;
				} else if (tailMode == TailMode.UP) {
					ang = 130;
					swing = -20;
				}
				// tail
				delegate.push();
				delegate.anchorTo(BodyPart.TORSO);
				delegate.translate(0, -2, 4);
				delegate.rotate(ang+(swingAmount*swing), 1, 0, 0);
				int segments = 1;
				float[] angles = {features.tailBend0, features.tailBend1, features.tailBend2, features.tailBend3};
				if (features.tailBend1 != 0) {
					segments++;
					if (features.tailBend2 != 0) {
						segments++;
						if (features.tailBend3 != 0) {
							segments++;
						}
					}
				}
				int segHeight = 12/segments;
				for (int i = 0; i < segments; i++) {
					delegate.rotate(angles[i]*(1-(swingAmount/2)), 1, 0, 0);
					renderDoubleSided(delegate, 56, 16+(i*segHeight), 8, segHeight, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
					delegate.translate(0, segHeight, 0);
				}
				delegate.pop();
			}
			
			boolean claws = features.protrusions.claws;
			boolean horn = features.protrusions.horn;
			
			if (claws) {
				// claws, left leg
				delegate.push();
				delegate.anchorTo(BodyPart.LEFT_LEG);
				delegate.translate(0, 0, -4);
				delegate.rotate(90, 1, 0, 0);
				renderDoubleSided(delegate, 16, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
				delegate.pop();
				
				// claws, right leg
				delegate.push();
				delegate.anchorTo(BodyPart.RIGHT_LEG);
				delegate.translate(0, 0, -4);
				delegate.rotate(90, 1, 0, 0);
				renderDoubleSided(delegate, 0, 16, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
				delegate.pop();
				
				// claws, left arm
				delegate.push();
				delegate.anchorTo(BodyPart.LEFT_ARM);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-4, 0, 4);
				renderDoubleSided(delegate, 44, 48, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.HORIZONTAL, QuadGrow.NONE);
				delegate.pop();
				
				// claws, right arm
				delegate.push();
				delegate.anchorTo(BodyPart.RIGHT_ARM);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-4, 0, 0);
				renderDoubleSided(delegate, 52, 16, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			}
			
			if (horn) {
				delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.translate(0, -8, 0);
				delegate.rotate(25, 1, 0, 0);
				delegate.translate(0, -8, 0);
				renderDoubleSided(delegate, 56, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			}
		}
	}

	private static void renderDoubleSided(EarsRenderDelegate delegate, int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
		delegate.renderFront(u, v, width, height, rot, flip, grow);
		delegate.renderBack(u, v, width, height, rot, flip.flipHorizontally(), grow);
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
