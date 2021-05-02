package com.unascribed.ears.asm;

import com.unascribed.ears.asm.mini.MiniCoremod;
import com.unascribed.ears.common.EarsLog;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions("com.unascribed.ears.asm")
@MCVersion("1.5.2")
public class EarsLoadingPlugin extends MiniCoremod {

	public EarsLoadingPlugin() {
		super(ImageBufferDownloadTransformer.class, ThreadDownloadImageTransformer.class, RenderPlayerTransformer.class);
		EarsLog.debug("Platform:Inject", "Coremod constructed");
	}
	
}
