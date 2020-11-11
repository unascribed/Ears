package com.unascribed.ears.asm;

import com.elytradev.mini.MiniCoremod;
import com.unascribed.ears.common.EarsLog;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("Ears")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"com.unascribed.ears.asm", "com.elytradev.mini"})
public class EarsLoadingPlugin extends MiniCoremod {

	public EarsLoadingPlugin() {
		super(ImageBufferDownloadTransformer.class, RenderPlayerTransformer.class, ThreadDownloadImageDataTransformer.class);
		EarsLog.debug("Platform:Inject", "Coremod constructed");
	}

}
