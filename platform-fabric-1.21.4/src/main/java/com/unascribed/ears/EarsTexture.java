package com.unascribed.ears;

import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class EarsTexture extends NativeImageBackedTexture implements EarsFeaturesHolder {
	private EarsFeatures earsFeatures;
	
	@SuppressWarnings("resource")
	public EarsTexture(NativeImage image, AlfalfaData alfaalfa) {
		super(image);
		
		earsFeatures = EarsFeaturesParser.detect(new NativeImageAdapter(image), alfaalfa,
				data -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data))));
	}

	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures;
	}
}