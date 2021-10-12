package com.unascribed.ears;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.mixin.accessor.AccessorImageProcessorImpl;

import net.minecraft.client.ImageProcessorImpl;

public class SkinStripAlpha implements EarsCommon.StripAlphaMethod {
	ImageProcessorImpl subject;

	public SkinStripAlpha(ImageProcessorImpl subject) {
		this.subject = subject;
	}

	@Override
	public void stripAlpha(int _x1, int _y1, int _x2, int _y2) {
		((AccessorImageProcessorImpl) subject).setAreaOpaque(_x1, _y1, _x2, _y2);
	}
}
