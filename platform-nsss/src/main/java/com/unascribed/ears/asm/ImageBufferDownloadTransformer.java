package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("com.mojang.minecraft.render.ImageBufferDownload")
public class ImageBufferDownloadTransformer extends MiniTransformer {

	@Patch.Method("func_883_a(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;")
	@Patch.Method.AffectsControlFlow
	public void patchParseUserSkin(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "interceptParseUserSkin", "(Lcom/mojang/minecraft/render/ImageBufferDownload;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;"),
			ARETURN()
		);
	}
	
}
