package com.unascribed.ears.asm;

import com.unascribed.ears.asm.mini.MiniCoremod;
import com.unascribed.ears.common.debug.EarsLog;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions("com.unascribed.ears.asm")
public class EarsLoadingPlugin extends MiniCoremod {

	public EarsLoadingPlugin() {
		super(ImageBufferDownloadTransformer.class, ThreadDownloadImageTransformer.class, RenderPlayerTransformer.class);
		EarsLog.debug("Platform:Inject", "Coremod constructed");
	}

}
