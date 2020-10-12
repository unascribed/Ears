package com.unascribed.ears;

import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.Mod;

@Mod("ears")
public class EarsMod {
	
	public EarsMod() {
		try {
			Class.forName("org.spongepowered.asm.mixin.Mixins");
		} catch (Throwable t) {
			throw new LoaderException("Ears requires MixinBootstrap on versions earlier than Forge 32.0.72");
		}
	}
	
}
