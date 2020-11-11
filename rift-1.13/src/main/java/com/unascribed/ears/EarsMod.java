package com.unascribed.ears;

import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;


public class EarsMod implements InitializationListener {

	@Override
	public void onInitialization() {
		MixinBootstrap.init();
		Mixins.addConfiguration("ears.mixins.json");
	}
	
}
