package com.unascribed.ears;

import java.util.Map;
import java.util.WeakHashMap;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPart;

public class EarsMod implements ClientModInitializer {
	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();

	public static final LayerEars layer = new LayerEars();

	public static Minecraft client;
	
	public static ModelPart slimLeftArm;
	public static ModelPart slimRightArm;
	public static ModelPart fatLeftArm;
	public static ModelPart fatRightArm;

	@Override
	public void onInitializeClient() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft b1.7.3 / Fabric {}; Env={}",
					FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(),
					FabricLoader.getInstance().getEnvironmentType());
		}
	}
}