package com.unascribed.ears.asm;


import com.unascribed.ears.asm.mini.MiniCoremod;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("Ears")
@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"com.unascribed.ears.asm", "com.elytradev.mini"})
public class EarsLoadingPlugin extends MiniCoremod {

	public EarsLoadingPlugin() {
		super(ImageBufferDownloadTransformer.class, RenderPlayerTransformer.class, ThreadDownloadImageDataTransformer.class);
	}

}
