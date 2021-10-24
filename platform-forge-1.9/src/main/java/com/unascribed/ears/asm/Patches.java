package com.unascribed.ears.asm;

import java.util.List;

import com.unascribed.ears.common.agent.mini.MiniTransformer;

import net.minecraftforge.fml.common.Loader;

public class Patches {

	public static void addTransformers(List<MiniTransformer> out) {
		out.add(new ImageBufferDownloadTransformer());
		out.add(new RenderPlayerTransformer());
		out.add(new ThreadDownloadImageDataTransformer());
		if (Loader.instance().getMCVersionString().startsWith("Minecraft 1.11")) {
			out.add(new LayerElytraTransformer_11());
		} else {
			out.add(new LayerElytraTransformer_9_10());
		}
	}
	
}
