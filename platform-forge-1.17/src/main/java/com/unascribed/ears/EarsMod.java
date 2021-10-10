package com.unascribed.ears;

import com.unascribed.ears.common.debug.EarsLog;
import net.minecraft.SharedConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;

@Mod("ears")
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft {} / Forge {}; Env={}:{}",
					SharedConstants.getCurrentVersion().getName(), ForgeVersion.getVersion(), FMLEnvironment.dist, FMLEnvironment.naming);
		}
	}
	
}
