package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.ImageBufferDownload")
public class ImageBufferDownloadTransformer extends MiniTransformer {
	
	@Patch.Method("func_78432_a(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;")
	@Patch.Method.AffectsControlFlow
	public void patchParseUserSkin(PatchContext ctx) {
		ctx.jumpToStart();
		// return EarsMod.interceptParseUserSkin(this, ...);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "interceptParseUserSkin", "(Lnet/minecraft/client/renderer/ImageBufferDownload;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;"),
			ARETURN()
		);
	}

}
