package com.unascribed.ears.common;

import com.unascribed.ears.Identified;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.EarAnchor;
import com.unascribed.ears.api.features.EarsFeatures.EarMode;
import com.unascribed.ears.api.features.EarsFeatures.TailMode;
import com.unascribed.ears.api.features.EarsFeatures.WingMode;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.ears.api.registry.EarsStateOverriderRegistry;
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
	public static void render(EarsFeatures features, EarsRenderDelegate delegate) {
		EarsLog.debug(EarsLog.Tag.COMMON_RENDERER, "render({}, {})", features, delegate);
		boolean slim = delegate.isSlim();
		
		if (EarsLog.DEBUG && EarsLog.shouldLog(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE)) {
			delegate = new DebuggingDelegate(delegate);
		}
	
		if (EarsLog.DEBUG && EarsLog.shouldLog(EarsLog.Tag.COMMON_RENDERER_DOTS)) {
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
		
		if ((features != null && features.enabled) || delegate.needsSecondaryLayersDrawn()) {
			// the 1.15+ rendering pipeline introduces nasty transparency sort bugs due to the buffering it does
			// render in multiple passes to avoid it (third is for armor, fourth is for armor glint)
			delegate.setUp();
			for (int p = 0; p < 4; p++) {
				renderInner(features, delegate, p, false);
				if (features != null && features.emissive && p < 2) {
					delegate.setEmissive(true);
					renderInner(features, delegate, p, true);
					delegate.setEmissive(false);
				}
			}
			delegate.bind(TexSource.SKIN);
			delegate.tearDown();
		}
	}
		
	private static void renderInner(EarsFeatures features, EarsRenderDelegate delegate, int p, boolean drawingEmissive) {
		boolean slim = delegate.isSlim();
		float swingAmount = delegate.getLimbSwing();
		delegate.bind(drawingEmissive ? TexSource.EMISSIVE_SKIN : TexSource.SKIN);
		if (drawingEmissive) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				drawVanillaCuboid(delegate, 0, 0, 8, 8, 8, 0);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.TORSO);
				drawVanillaCuboid(delegate, 16, 16, 8, 12, 4, 0);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.LEFT_ARM);
				drawVanillaCuboid(delegate, 32, 48, slim ? 3 : 4, 12, 4, 0);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.RIGHT_ARM);
				drawVanillaCuboid(delegate, 40, 16, slim ? 3 : 4, 12, 4, 0);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.LEFT_LEG);
				drawVanillaCuboid(delegate, 16, 48, 4, 12, 4, 0);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.RIGHT_LEG);
				drawVanillaCuboid(delegate, 0, 16, 4, 12, 4, 0);
			delegate.pop();
		}
		if (delegate.needsSecondaryLayersDrawn() || drawingEmissive) {
			if (drawingEmissive) {
				delegate.push();
					delegate.anchorTo(BodyPart.HEAD);
					delegate.translate(0, 1f, 0);
					drawVanillaCuboid(delegate, 32, 0, 8, 8, 8, 0.5f);
				delegate.pop();
			}
			delegate.push();
				delegate.anchorTo(BodyPart.TORSO);
				delegate.translate(0, 0.5f, 0);
				drawVanillaCuboid(delegate, 16, 32, 8, 12, 4, 0.25f);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.LEFT_ARM);
				delegate.translate(0, 0.5f, 0);
				drawVanillaCuboid(delegate, 48, 48, slim ? 3 : 4, 12, 4, 0.25f);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.RIGHT_ARM);
				delegate.translate(0, 0.5f, 0);
				drawVanillaCuboid(delegate, 40, 32, slim ? 3 : 4, 12, 4, 0.25f);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.LEFT_LEG);
				delegate.translate(0, 0.5f, 0);
				drawVanillaCuboid(delegate, 0, 48, 4, 12, 4, 0.25f);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.RIGHT_LEG);
				delegate.translate(0, 0.5f, 0);
				drawVanillaCuboid(delegate, 0, 32, 4, 12, 4, 0.25f);
			delegate.pop();
		}
		
		if (features != null && features.enabled) {
			if (p == 1 && delegate instanceof IndirectEarsRenderDelegate) {
				((IndirectEarsRenderDelegate<?,?,?,?,?>)delegate).beginTranslucent();
			}
			delegate.bind(drawingEmissive ? TexSource.EMISSIVE_SKIN : TexSource.SKIN);
			
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
				} else if (earMode == EarMode.TALL) {
					delegate.push();
						delegate.anchorTo(BodyPart.HEAD);
						delegate.translate(0, -8, 0);
						if (earAnchor == EarAnchor.CENTER) {
							delegate.translate(0, 0, 4);
						} else if (earAnchor == EarAnchor.BACK) {
							delegate.translate(0, 0, 8);
						}
						
						float ang = -6;
						
						double dX = delegate.getCapeX()-delegate.getX();
						double dZ = delegate.getCapeZ()-delegate.getZ();
						
						float yaw = delegate.getBodyYaw();
						double yawX = Math.sin(Math.toRadians(yaw));
						double yawZ = -Math.cos(Math.toRadians(yaw));
						float dForward = (float)(dX * yawX + dZ * yawZ) * 25.0F;
						if (dForward > 80) dForward = 80;
						if (dForward < -80) dForward = -80;
						ang -= dForward;
						
						delegate.rotate(ang/3, 1, 0, 0);
						delegate.translate(0, -4, 0);
						delegate.renderFront(24, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
						delegate.renderBack(56, 40, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						
						delegate.rotate(ang, 1, 0, 0);
						delegate.translate(0, -4, 0);
						delegate.renderFront(28, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
						delegate.renderBack(56, 36, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						
						delegate.rotate(ang/2, 1, 0, 0);
						delegate.translate(0, -4, 0);
						delegate.renderFront(32, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
						delegate.renderBack(56, 32, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						
						delegate.rotate(ang, 1, 0, 0);
						delegate.translate(0, -4, 0);
						delegate.renderFront(36, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
						delegate.renderBack(56, 28, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					delegate.pop();
				} else if (earMode == EarMode.TALL_CROSS) {
					delegate.push();
						delegate.anchorTo(BodyPart.HEAD);
						if (earAnchor == EarAnchor.CENTER) {
							delegate.translate(0, 0, 4);
						} else if (earAnchor == EarAnchor.BACK) {
							delegate.translate(0, 0, 8);
						}
						delegate.translate(4, -24, 0);
						delegate.push();
							delegate.rotate(45, 0, 1, 0);
							delegate.translate(-4, 0, 0);
							delegate.renderFront(24, 0, 8, 16, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
							delegate.renderBack(56, 28, 8, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						delegate.pop();
						delegate.push();
							delegate.rotate(-45, 0, 1, 0);
							delegate.translate(-4, 0, 0);
							delegate.renderFront(24, 0, 8, 16, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
							delegate.renderBack(56, 28, 8, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						delegate.pop();
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
					if (isActive(delegate, EarsStateType.GLIDING)) {
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
						if (segments <= 0) segments = 1;
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
					if (!isActive(delegate, EarsStateType.WEARING_BOOTS)) {
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
			
			if (chestSize > 0 &&
					(!isActive(delegate, EarsStateType.WEARING_CHESTPLATE) || delegate.canBind(TexSource.CHESTPLATE)) &&
					(
							p == 0 ||
							(p == 1 && delegate.isJacketEnabled()) ||
							((p == 2 || (p == 3 && delegate.canBind(TexSource.GLINT_CHESTPLATE))) &&
									isActive(delegate, EarsStateType.WEARING_CHESTPLATE) && !drawingEmissive && delegate.canBind(TexSource.CHESTPLATE)))
					&& !isInhibited(delegate, EarsFeatureType.CHEST)) {
				delegate.push();
					delegate.anchorTo(BodyPart.TORSO);
					delegate.translate(0, -10, 0);
					delegate.rotate(-chestSize*45, 1, 0, 0);
					
					if (p == 2) {
						delegate.bind(TexSource.CHESTPLATE);
					} else if (p == 3) {
						delegate.bind(TexSource.GLINT_CHESTPLATE);
					}
					
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
					} else if (p == 2 || p == 3) {
						delegate.push();
							delegate.translate(0, 1, -1f);
							delegate.renderFront(20, 24, 8, 3, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
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
						} else if (p == 2 || p == 3) {
							delegate.push();
								delegate.translate(0, 0, -1f);
								delegate.renderFront(20, 25, 8, 3, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
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
						} else if (p == 2 || p == 3) {
							delegate.push();
								delegate.translate(0, 0, -1f);
								delegate.renderFront(16, 20, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
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
						} else if (p == 2 || p == 3) {
							delegate.push();
								delegate.translate(0, 0, -1f);
								delegate.renderFront(16, 20, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
							delegate.pop();
						}
					delegate.pop();
				delegate.pop();
			}
			
			if (p == 0 && !isInhibited(delegate, EarsFeatureType.WINGS)) {
				WingMode wingMode = features.wingMode;
	
				if (wingMode != WingMode.NONE) {
					boolean g = isActive(delegate, EarsStateType.GLIDING);
					boolean f = isActive(delegate, EarsStateType.CREATIVE_FLYING);
					delegate.push();
						float wiggle;
						if (features.animateWings) {
							wiggle = g ? -40 : ((float)(Math.sin((delegate.getTime()+8)/(f ? 2 : 12))*(f ? 20 : 2)))+(swingAmount*10);
						} else {
							wiggle = 0;
						}
						delegate.anchorTo(BodyPart.TORSO);
						delegate.bind(drawingEmissive ? TexSource.EMISSIVE_WING : TexSource.WING);
						delegate.translate(2, -14, 4);
						if (wingMode == WingMode.SYMMETRIC_DUAL || wingMode == WingMode.ASYMMETRIC_R) {
							delegate.push();
								delegate.rotate(-120+wiggle, 0, 1, 0);
								delegate.renderDoubleSided(0, 0, 20, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							delegate.pop();
						}
						if (wingMode == WingMode.SYMMETRIC_DUAL || wingMode == WingMode.ASYMMETRIC_L) {
							delegate.translate(4, 0, 0);
							delegate.push();
								delegate.rotate(-60-wiggle, 0, 1, 0);
								delegate.renderDoubleSided(0, 0, 20, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							delegate.pop();
						}
						if (wingMode == WingMode.SYMMETRIC_SINGLE) {
							delegate.translate(2, 0, 0);
							delegate.push();
								delegate.rotate(-90+wiggle, 0, 1, 0);
								delegate.renderDoubleSided(0, 0, 20, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
							delegate.pop();
						}
					delegate.pop();
				}
			}
			
			if (!drawingEmissive && p == 0 && features.capeEnabled && !isInhibited(delegate, EarsFeatureType.CAPE) && !isActive(delegate, EarsStateType.WEARING_ELYTRA)) {
				delegate.push();
					delegate.anchorTo(BodyPart.TORSO);
					delegate.translate(4, -12, 5);
					double dX = delegate.getCapeX() - delegate.getX();
					double dY = delegate.getCapeY() - delegate.getY();
					double dZ = delegate.getCapeZ() - delegate.getZ();
					float yaw = delegate.getBodyYaw();
					double yawX = Math.sin(Math.toRadians(yaw));
					double yawZ = -Math.cos(Math.toRadians(yaw));
					float dUp = (float)dY * 10;
					dUp = clamp(dUp, -6, 32);
					float dForward = (float)(dX * yawX + dZ * yawZ) * 100f;
					dForward = clamp(dForward, 0, 150);
					float dSide = (float)(dX * yawZ - dZ * yawX) * 100f;
					dSide = clamp(dSide, -20, 20);
					if (dForward < 0) {
						dForward = 0;
					}

					float stride = delegate.getStride();
					dUp += Math.sin(delegate.getHorizontalSpeed() * 6) * 32 * stride;

					delegate.rotate(6.0F + dForward / 2.0F + dUp, 1, 0, 0);
					delegate.rotate(dSide / 2.0F, 0, 0, 1);
					delegate.rotate(180.0F - dSide / 2.0F, 0, 1, 0);
					
					delegate.bind(TexSource.CAPE);
					delegate.translate(-5, 0, 0);
					// front
					delegate.renderDoubleSided(0, 0, 10, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					delegate.push();
						// left
						delegate.translate(10, 0, 1);
						delegate.rotate(90, 0, 1, 0);
						delegate.renderDoubleSided(0, 0, 1, 16, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
						// back
						delegate.translate(0, 0, 0);
						delegate.rotate(90, 0, 1, 0);
						delegate.renderDoubleSided(10, 0, 10, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
						// right
						delegate.translate(10, 0, 1);
						delegate.rotate(90, 0, 1, 0);
						delegate.renderDoubleSided(9, 0, 1, 16, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
					delegate.pop();
					
					// top
					delegate.rotate(90, 1, 0, 0);
					delegate.renderDoubleSided(0, 0, 10, 1, TexRotation.NONE, TexFlip.VERTICAL, QuadGrow.NONE);
					
					// bottom
					delegate.translate(0, 0, -16);
					delegate.renderDoubleSided(10, 15, 10, 1, TexRotation.NONE, TexFlip.VERTICAL, QuadGrow.NONE);
					delegate.bind(TexSource.SKIN);
				delegate.pop();
			}
		}
	}

	private static float clamp(float v, float min, float max) {
		return Math.max(Math.min(v, max), min);
	}

	private static void drawVanillaCuboid(EarsRenderDelegate delegate, int u, int v, int w, int h, int d, float g) {
		float g2 = g*2;
		delegate.translate(w/2f, h/2f, d/2f);
		delegate.scale((w+g2)/w, (h+g2)/h, (d+g2)/d);
		delegate.translate(-w/2f, -h*1.5f, -d/2f);
		// front
		delegate.renderDoubleSided(u+d, v+d, w, h, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
		delegate.push();
			// left
			delegate.translate(w, 0, d);
			delegate.rotate(90, 0, 1, 0);
			delegate.renderDoubleSided(u+d+w, v+d, d, h, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
			// back
			delegate.translate(0, 0, 0);
			delegate.rotate(90, 0, 1, 0);
			delegate.renderDoubleSided(u+d+w+d, v+d, w, h, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
			// right
			delegate.translate(w, 0, d);
			delegate.rotate(90, 0, 1, 0);
			delegate.renderDoubleSided(u, v+d, d, h, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
		delegate.pop();
		
		// top
		delegate.rotate(90, 1, 0, 0);
		delegate.renderDoubleSided(u+d, v, w, d, TexRotation.NONE, TexFlip.VERTICAL, QuadGrow.NONE);
		
		// bottom
		delegate.translate(0, 0, -h);
		delegate.renderDoubleSided(u+d+w, v, w, d, TexRotation.NONE, TexFlip.VERTICAL, QuadGrow.NONE);
	}

	private static boolean isInhibited(EarsRenderDelegate delegate, EarsFeatureType feature) {
		String namespace = EarsInhibitorRegistry.isInhibited(feature, delegate.getPeer());
		if (namespace != null) {
			EarsLog.debug(EarsLog.Tag.COMMON_API, "Rendering of feature {} is being inhibited by {}", feature, namespace);
			return true;
		}
		return false;
	}

	private static boolean isActive(EarsRenderDelegate delegate, EarsStateType state) {
		boolean def = false;
		switch (state) {
			case CREATIVE_FLYING:
				def = delegate.isFlying();
				break;
			case GLIDING:
				def = delegate.isGliding();
				break;
			case WEARING_BOOTS:
				def = delegate.isWearingBoots();
				break;
			case WEARING_CHESTPLATE:
				def = delegate.isWearingChestplate();
				break;
			case WEARING_ELYTRA:
				def = delegate.isWearingElytra();
				break;
			case WEARING_HELMET:
				def = false;
				break;
			case WEARING_LEGGINGS:
				def = false;
				break;
			default:
				break;
		}
		Identified<Boolean> id = EarsStateOverriderRegistry.isActive(state, delegate.getPeer(), def);
		if (id.getNamespace() != null) {
			EarsLog.debug(EarsLog.Tag.COMMON_API, "State of {} is being overridden to {} from {} by {}", state, id.getValue(), def, id.getNamespace());
		}
		return id.getValue();
	}

}
