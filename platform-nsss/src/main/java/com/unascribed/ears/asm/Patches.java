package com.unascribed.ears.asm;

import java.util.List;

import com.unascribed.ears.common.agent.mini.MiniTransformer;

public class Patches {

	public static void addTransformers(List<MiniTransformer> out) {
		out.add(new MinecraftTransformer());
		out.add(new SkinUrlGetPostTransformer());
		out.add(new TexturedQuadTransformer());
		out.add(new ImageBufferDownloadTransformer());
		out.add(new RenderPlayerTransformer());
		out.add(new ThreadDownloadImageTransformer());
		out.add(new EntityPlayerTransformer());
	}
	
}
