package com.unascribed.ears.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.unascribed.ears.common.EarsFeatures.EarAnchor;
import com.unascribed.ears.common.EarsFeatures.EarMode;
import com.unascribed.ears.common.EarsFeatures.TailMode;
import com.unascribed.ears.common.EarsFeatures.WingMode;
import com.unascribed.ears.common.debug.DebuggingDelegate;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;

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
		EarsLog.debug("Common", "carefullyStripAlpha({}, {})", sam, sixtyFour);
		for (Rectangle rect : FORCED_OPAQUE_REGIONS) {
			if (!sixtyFour && rect.y1 > 32) continue;
			sam.stripAlpha(rect.x1, rect.y1, rect.x2, rect.y2);
		}
	}
	
	public static boolean shouldSuppressElytra(EarsFeatures features) {
		return features.wingMode != WingMode.NONE;
	}
	
	/**
	 * Render all the features described in {@code features} using {@code delegate}.
	 */
	public static void render(EarsFeatures features, EarsRenderDelegate delegate, float swingAmount, boolean slim) {
		EarsLog.debug("Common:Renderer", "render({}, {}, {})", features, delegate, swingAmount);
		
		if (EarsLog.DEBUG && EarsLog.shouldLog("Platform:Renderer:Delegate")) {
			delegate = new DebuggingDelegate(delegate);
		}

		if (EarsLog.DEBUG && EarsLog.shouldLog("Common:Renderer:Dots")) {
			for (BodyPart part : BodyPart.values()) {
				delegate.push();
					delegate.anchorTo(part);
					delegate.renderDebugDot(1, 1, 1, 1);
					delegate.push();
						delegate.translate(part.getXSize(slim), 0, 0);
						delegate.renderDebugDot(1, 0, 0, 1);
					delegate.pop();
					delegate.push();
						delegate.translate(0, -part.getYSize(slim), 0);
						delegate.renderDebugDot(0, 1, 0, 1);
					delegate.pop();
					delegate.push();
						delegate.translate(0, 0, part.getZSize(slim));
						delegate.renderDebugDot(0, 0, 1, 1);
					delegate.pop();
				delegate.pop();
			}
		}
		
		if (features != null && features.enabled) {
			delegate.setUp();
			// the 1.15+ renderring pipeline introduces nasty transparency sort bugs due to the buffering it does
			// render in two passes to avoid it
			for (int p = 0; p < 2; p++) {
				if (p == 1 && delegate instanceof IndirectEarsRenderDelegate) {
					((IndirectEarsRenderDelegate<?,?,?,?,?>)delegate).beginTranslucent();
				}
				delegate.bind(TexSource.SKIN);
				
				if (p == 0) {
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
					} else if (earMode == EarMode.OUT) {
						delegate.push();
							delegate.anchorTo(BodyPart.HEAD);
							delegate.rotate(90, 0, 1, 0);
							if (earAnchor == EarAnchor.BACK) {
								delegate.translate(-16, -8, 0);
							} else if (earAnchor == EarAnchor.CENTER) {
								delegate.translate(-8, -16, 0);
							} else if (earAnchor == EarAnchor.FRONT) {
								delegate.translate(0, -8, 0);
							}
							delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
							delegate.rotate(180, 0, 1, 0);
							delegate.translate(-8, 0, -8);
							delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
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
						float baseAngle = features.tailBend0;
						if (delegate.isGliding()) {
							baseAngle = -30;
							ang = 0;
						}
						delegate.push();
							delegate.anchorTo(BodyPart.TORSO);
							delegate.translate(0, -2, 4);
							delegate.rotate(ang+(swingAmount*swing)+(float)(Math.sin(delegate.getTime()/12)*4), 1, 0, 0);
							boolean vert = tailMode == TailMode.VERTICAL;
							if (vert) {
								delegate.translate(4, 0, 0);
								delegate.rotate(90, 0, 0, 1);
								if (baseAngle < 0) {
									delegate.translate(4, 0, 0);
									delegate.rotate(baseAngle, 0, 1, 0);
									delegate.translate(-4, 0, 0);
								}
								delegate.translate(-4, 0, 0);
								if (baseAngle > 0) {
									delegate.rotate(baseAngle, 0, 1, 0);
								}
								delegate.rotate(90, 1, 0, 0);
							}
							int segments = 1;
							float[] angles = {vert ? 0 : baseAngle, features.tailBend1, features.tailBend2, features.tailBend3};
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
								delegate.renderDoubleSided(56, 16+(i*segHeight), 8, segHeight, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
								delegate.translate(0, segHeight, 0);
							}
						delegate.pop();
					}
					
					boolean claws = features.protrusions.claws;
					boolean horn = features.protrusions.horn;
					
					if (claws) {
						if (!delegate.isWearingBoots()) {
							delegate.push();
								delegate.anchorTo(BodyPart.LEFT_LEG);
								delegate.translate(0, 0, -4);
								delegate.rotate(90, 1, 0, 0);
								delegate.renderDoubleSided(16, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
							delegate.pop();
							
							delegate.push();
								delegate.anchorTo(BodyPart.RIGHT_LEG);
								delegate.translate(0, 0, -4);
								delegate.rotate(90, 1, 0, 0);
								delegate.renderDoubleSided(0, 16, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
							delegate.pop();
						}
						
						delegate.push();
							delegate.anchorTo(BodyPart.LEFT_ARM);
							delegate.rotate(90, 0, 1, 0);
							delegate.translate(-4, 0, slim ? 3 : 4);
							delegate.renderDoubleSided(44, 48, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.HORIZONTAL, QuadGrow.NONE);
						delegate.pop();
						
						delegate.push();
							delegate.anchorTo(BodyPart.RIGHT_ARM);
							delegate.rotate(90, 0, 1, 0);
							delegate.translate(-4, 0, 0);
							delegate.renderDoubleSided(52, 16, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.NONE, QuadGrow.NONE);
						delegate.pop();
					}
					
					if (horn) {
						delegate.push();
							delegate.anchorTo(BodyPart.HEAD);
							delegate.translate(0, -8, 0);
							delegate.rotate(25, 1, 0, 0);
							delegate.translate(0, -8, 0);
							delegate.renderDoubleSided(56, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						delegate.pop();
					}
					
					int snoutOffset = features.snoutOffset;
					int snoutWidth = features.snoutWidth;
					int snoutHeight = features.snoutHeight;
					int snoutDepth = features.snoutDepth;
					
					if (snoutWidth > 0 && snoutHeight > 0 && snoutDepth > 0) {
						delegate.push();
							delegate.anchorTo(BodyPart.HEAD);
							delegate.translate((8-snoutWidth)/2f, -(snoutOffset+snoutHeight), -snoutDepth);
							delegate.renderDoubleSided(0, 2, snoutWidth, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							delegate.push();
								// top
								delegate.rotate(-90, 1, 0, 0);
								delegate.translate(0, -1, 0);
								delegate.renderDoubleSided(0, 1, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								for (int i = 0; i < snoutDepth-1; i++) {
									delegate.translate(0, -1, 0);
									delegate.renderDoubleSided(0, 0, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								}
							delegate.pop();
							delegate.push();
								// bottom
								delegate.translate(0, snoutHeight, 0);
								delegate.rotate(90, 1, 0, 0);
								delegate.renderDoubleSided(0, 2+snoutHeight, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								for (int i = 0; i < snoutDepth-1; i++) {
									delegate.translate(0, 1, 0);
									delegate.renderDoubleSided(0, 2+snoutHeight+1, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								}
							delegate.pop();
							delegate.push();
								delegate.rotate(90, 0, 1, 0);
								// right
								delegate.push();
									delegate.translate(-1, 0, 0);
									delegate.renderDoubleSided(7, 0, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
									for (int i = 0; i < snoutDepth-1; i++) {
										delegate.translate(-1, 0, 0);
										delegate.renderDoubleSided(7, 4, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
									}
								delegate.pop();
								// left
								delegate.push();
									delegate.translate(-1, 0, snoutWidth);
									delegate.renderDoubleSided(7, 0, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
									for (int i = 0; i < snoutDepth-1; i++) {
										delegate.translate(-1, 0, 0);
										delegate.renderDoubleSided(7, 4, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
									}
								delegate.pop();
							delegate.pop();
						delegate.pop();
					}
				}
				
				
				float chestSize = features.chestSize;
				
				if (chestSize > 0 && !delegate.isWearingChestplate()) {
					delegate.push();
						delegate.anchorTo(BodyPart.TORSO);
						delegate.translate(0, -10, 0);
						delegate.rotate(-chestSize*45, 1, 0, 0);
						if (p == 0) {
							delegate.renderDoubleSided(20, 22, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						} else if (p == 1) {
							delegate.push();
								delegate.translate(4, 2, 0);
								// can't use QuadGrow as we have two quads side-by-side
								delegate.scale(8.5f/8, 4.5f/4, 1);
								delegate.translate(-4, -2, 0);
								delegate.translate(0, 0, -0.25f);
								delegate.renderDoubleSided(0, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								delegate.translate(4, 0, 0);
								delegate.renderDoubleSided(12, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							delegate.pop();
						}
						delegate.push();
							delegate.translate(0, 4, 0);
							delegate.rotate(90, 1, 0, 0);
							if (p == 0) {
								delegate.renderDoubleSided(56, 44, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							} else if (p == 1) {
								delegate.push();
									delegate.translate(0, 0, -0.25f);
									delegate.renderDoubleSided(28, 48, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.QUARTERPIXEL);
								delegate.pop();
							}
						delegate.pop();
						delegate.push();
							delegate.rotate(90, 0, 1, 0);
							delegate.translate(-4f, 0, 0.01f);
							if (p == 0) {
								delegate.renderDoubleSided(60, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							} else if (p == 1) {
								delegate.push();
									delegate.translate(0, 0, -0.25f);
									delegate.renderDoubleSided(48, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.QUARTERPIXEL);
								delegate.pop();
							}
							delegate.translate(0, 0, 7.98f);
							delegate.rotate(180, 0, 1, 0);
							delegate.translate(-4, 0, 0);
							if (p == 0) {
								delegate.renderDoubleSided(60, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
							} else if (p == 1) {
								delegate.push();
									delegate.translate(0, 0, -0.25f);
									delegate.renderDoubleSided(48, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.QUARTERPIXEL);
								delegate.pop();
							}
						delegate.pop();
					delegate.pop();
				}
				
				if (p == 0) {
					WingMode wingMode = features.wingMode;
		
					if (wingMode != WingMode.NONE) {
						boolean g = delegate.isGliding();
						boolean f = delegate.isFlying();
						delegate.push();
							float wiggle = g ? -40 : ((float)(Math.sin((delegate.getTime()+8)/(f ? 2 : 12))*(f ? 20 : 2)))+(swingAmount*10);
							delegate.anchorTo(BodyPart.TORSO);
							delegate.bind(TexSource.WING);
							delegate.translate(2, -12, 4);
							if (wingMode == WingMode.SYMMETRIC_DUAL || wingMode == WingMode.ASYMMETRIC_R) {
								delegate.push();
									delegate.rotate(-120+wiggle, 0, 1, 0);
									delegate.renderDoubleSided(0, 0, 12, 12, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								delegate.pop();
							}
							if (wingMode == WingMode.SYMMETRIC_DUAL || wingMode == WingMode.ASYMMETRIC_L) {
								delegate.translate(4, 0, 0);
								delegate.push();
									delegate.rotate(-60-wiggle, 0, 1, 0);
									delegate.renderDoubleSided(0, 0, 12, 12, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								delegate.pop();
							}
							if (wingMode == WingMode.SYMMETRIC_SINGLE) {
								delegate.translate(2, 0, 0);
								delegate.push();
									delegate.rotate(-90+wiggle, 0, 1, 0);
									delegate.renderDoubleSided(0, 0, 12, 12, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
								delegate.pop();
							}
						delegate.pop();
					}
				}
			}
			
			delegate.bind(TexSource.SKIN);
			delegate.tearDown();
		}
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
		EarsLog.debug("Common:Renderer", "calculateUVs(u={}, v={}, w={}, h={}, rot={}, flip={}, src={})", u, v, w, h, rot, flip, src);
		float tw = src.width;
		float th = src.height;
		
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
