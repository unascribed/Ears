package com.unascribed.ears;

import com.unascribed.ears.common.EarsLog;

import net.minecraft.util.SharedConstants;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;

@Mod("ears")
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft {} / Forge {}; Env={}:{}",
					SharedConstants.getVersion().getName(), ForgeVersion.getVersion(), FMLEnvironment.dist, FMLEnvironment.naming);
		}
		try {
			Class.forName("org.spongepowered.asm.mixin.Mixins");
		} catch (Throwable t) {
			throw new LoaderException("Ears requires MixinBootstrap on versions earlier than Forge 32.0.72");
		}
	}
	
}
