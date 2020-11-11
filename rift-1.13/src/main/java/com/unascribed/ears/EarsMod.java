package com.unascribed.ears;

import org.dimdev.riftloader.RiftLoader;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.unascribed.ears.common.EarsLog;

import net.minecraft.client.Minecraft;


public class EarsMod implements InitializationListener {

	@Override
	public void onInitialization() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft {} / Rift vWhoCares; Side={}",
					Minecraft.getInstance().getVersion(), RiftLoader.instance.getSide());
		}
		MixinBootstrap.init();
		Mixins.addConfiguration("ears.mixins.json");
	}
	
}
