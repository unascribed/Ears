package com.unascribed.ears;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.github.steveice10.mc.auth.service.ProfileService;
import com.github.steveice10.mc.auth.service.SessionService;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPart;

public class EarsMod implements ClientModInitializer {
	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();

	public static final SessionService sessionService = new SessionService();
	public static final ProfileService profileService = new ProfileService();
	public static LayerEars layer;

	public static Minecraft client;
	
	public static final Set<String> slimUsers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	public static ModelPart slimLeftArm;
	public static ModelPart slimRightArm;
	public static ModelPart fatLeftArm;
	public static ModelPart fatRightArm;

	public static ModelPart slimLeftSleeve;
	public static ModelPart slimRightSleeve;
	public static ModelPart fatLeftSleeve;
	public static ModelPart fatRightSleeve;


	@Override
	public void onInitializeClient() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft b1.7.3 / Fabric {}; Env={}",
					FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(),
					FabricLoader.getInstance().getEnvironmentType());
		}

		layer = new LayerEars();
	}
}