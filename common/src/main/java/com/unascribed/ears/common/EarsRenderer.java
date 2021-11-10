package com.unascribed.ears.common;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.EarsInhibitorRegistry;
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

class EarsRenderer {

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
					
					if (earMode != EarMode.NONE && isInhibited(delegate, EarsFeatureType.EARS)) earMode = EarMode.NONE;
					
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
					
					if (tailMode != TailMode.NONE && !isInhibited(delegate, EarsFeatureType.TAIL)) {
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
							int segments = features.tailSegments;
							float[] angles = {vert ? 0 : baseAngle, features.tailBend1, features.tailBend2, features.tailBend3};
							int segHeight = 12/segments;
							for (int i = 0; i < segments; i++) {
								delegate.rotate(angles[i]*(1-(swingAmount/2)), 1, 0, 0);
								delegate.renderDoubleSided(56, 16+(i*segHeight), 8, segHeight, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
								delegate.translate(0, segHeight, 0);
							}
						delegate.pop();
					}
					
					boolean claws = features.claws;
					boolean horn = features.horn;
					
					if (claws) {
						if (!delegate.isWearingBoots()) {
							if (!isInhibited(delegate, EarsFeatureType.CLAW_LEFT_LEG)) {
								delegate.push();
									delegate.anchorTo(BodyPart.LEFT_LEG);
									delegate.translate(0, 0, -4);
									delegate.rotate(90, 1, 0, 0);
									delegate.renderDoubleSided(16, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
								delegate.pop();
							}
							
							if (!isInhibited(delegate, EarsFeatureType.CLAW_RIGHT_LEG)) {
								delegate.push();
									delegate.anchorTo(BodyPart.RIGHT_LEG);
									delegate.translate(0, 0, -4);
									delegate.rotate(90, 1, 0, 0);
									delegate.renderDoubleSided(0, 16, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
								delegate.pop();
							}
						}
						
						if (!isInhibited(delegate, EarsFeatureType.CLAW_LEFT_ARM)) {
							delegate.push();
								delegate.anchorTo(BodyPart.LEFT_ARM);
								delegate.rotate(90, 0, 1, 0);
								delegate.translate(-4, 0, slim ? 3 : 4);
								delegate.renderDoubleSided(44, 48, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.HORIZONTAL, QuadGrow.NONE);
							delegate.pop();
						}
						
						if (!isInhibited(delegate, EarsFeatureType.CLAW_RIGHT_ARM)) {
							delegate.push();
								delegate.anchorTo(BodyPart.RIGHT_ARM);
								delegate.rotate(90, 0, 1, 0);
								delegate.translate(-4, 0, 0);
								delegate.renderDoubleSided(52, 16, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.NONE, QuadGrow.NONE);
							delegate.pop();
						}
					}
					
					if (horn && !isInhibited(delegate, EarsFeatureType.HORN)) {
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
					
					if (snoutWidth > 0 && snoutHeight > 0 && snoutDepth > 0 && !isInhibited(delegate, EarsFeatureType.SNOUT)) {
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
				
				if (chestSize > 0 && !delegate.isWearingChestplate() && (p == 0 || delegate.isJacketEnabled()) && !isInhibited(delegate, EarsFeatureType.OTHER_TORSO)) {
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
				
				if (p == 0 && !isInhibited(delegate, EarsFeatureType.WINGS)) {
					WingMode wingMode = features.wingMode;
		
					if (wingMode != WingMode.NONE) {
						boolean g = delegate.isGliding();
						boolean f = delegate.isFlying();
						delegate.push();
							float wiggle;
							if (features.animateWings) {
								wiggle = g ? -40 : ((float)(Math.sin((delegate.getTime()+8)/(f ? 2 : 12))*(f ? 20 : 2)))+(swingAmount*10);
							} else {
								wiggle = 0;
							}
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

	private static boolean isInhibited(EarsRenderDelegate delegate, EarsFeatureType feature) {
		String namespace = EarsInhibitorRegistry.isInhibited(feature, delegate.getPeer());
		if (namespace != null) {
			EarsLog.debug("Common:API", "Rendering of feature {} is being inhibited by {}", feature, namespace);
			return true;
		}
		return false;
	}

}
