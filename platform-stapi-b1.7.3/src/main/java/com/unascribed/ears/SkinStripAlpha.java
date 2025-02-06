package com.unascribed.ears;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.mixin.accessor.AccessorSkinImageProcessor;

import net.minecraft.client.texture.SkinImageProcessor;

public class SkinStripAlpha implements EarsCommon.StripAlphaMethod {
	SkinImageProcessor subject;

	public SkinStripAlpha(SkinImageProcessor subject) {
		this.subject = subject;
	}

	@Override
	public void stripAlpha(int _x1, int _y1, int _x2, int _y2) {
		((AccessorSkinImageProcessor) subject).setOpaque(_x1, _y1, _x2, _y2);
	}
}
