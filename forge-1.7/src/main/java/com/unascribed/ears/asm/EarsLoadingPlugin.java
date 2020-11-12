package com.unascribed.ears.asm;


import com.unascribed.ears.asm.mini.MiniCoremod;
import com.unascribed.ears.common.EarsLog;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;;

@IFMLLoadingPlugin.Name("Ears")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"com.unascribed.ears.asm", "com.elytradev.mini"})
public class EarsLoadingPlugin extends MiniCoremod {

	public EarsLoadingPlugin() {
		super(ImageBufferDownloadTransformer.class, ThreadDownloadImageDataTransformer.class, RenderPlayerTransformer.class);
		EarsLog.debug("Platform:Inject", "Coremod constructed");
	}

}
