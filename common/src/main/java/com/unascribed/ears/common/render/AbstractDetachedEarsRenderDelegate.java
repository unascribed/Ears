package com.unascribed.ears.common.render;

/**
 * A "detached" delegate that does not run in a copy of Minecraft, so all game state uses default
 * values.
 */
public abstract class AbstractDetachedEarsRenderDelegate implements EarsRenderDelegate {
	
	@Override
	public void setUp() {}

	@Override
	public void tearDown() {}
	
	@Override
	public Object getPeer() {
		return null;
	}

	@Override
	public float getTime() {
		return 0;
	}

	@Override
	public boolean isFlying() {
		return false;
	}

	@Override
	public boolean isGliding() {
		return false;
	}

	@Override
	public boolean isWearingElytra() {
		return false;
	}

	@Override
	public boolean isWearingChestplate() {
		return false;
	}

	@Override
	public boolean isWearingBoots() {
		return false;
	}
	
	@Override
	public boolean needsSecondaryLayersDrawn() {
		return false;
	}
	
	@Override
	public boolean canBind(TexSource tex) {
		return tex == TexSource.SKIN || !tex.isBuiltIn();
	}
	
	@Override
	public float getLimbSwing() {
		return 0;
	}

	@Override
	public float getHorizontalSpeed() {
		return 0;
	}

	@Override
	public float getStride() {
		return 0;
	}

	@Override
	public float getBodyYaw() {
		return 0;
	}

	@Override
	public double getX() {
		return 0;
	}

	@Override
	public double getY() {
		return 0;
	}

	@Override
	public double getZ() {
		return 0;
	}

	@Override
	public double getCapeX() {
		return 0;
	}

	@Override
	public double getCapeY() {
		return 0;
	}

	@Override
	public double getCapeZ() {
		return 0;
	}

}
