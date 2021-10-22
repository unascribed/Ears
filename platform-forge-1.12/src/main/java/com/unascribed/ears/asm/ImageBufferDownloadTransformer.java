package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.asm.tree.LabelNode;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("net.minecraft.client.renderer.ImageBufferDownload")
public class ImageBufferDownloadTransformer extends MiniTransformer {
	
	@Patch.Method("func_78432_a(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;")
	public void patchParseUserSkin(PatchContext ctx) {
		// return bufferedimage;
		ctx.search(
			ALOAD(2),
			ARETURN()
		).jumpBefore();
		// EarsMod.preprocessSkin(this, image, bufferedimage);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			ALOAD(2),
			INVOKESTATIC("com/unascribed/ears/Ears", "preprocessSkin","(Lnet/minecraft/client/renderer/ImageBufferDownload;Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
	@Patch.Method("func_78433_b(IIII)V")
	@Patch.Method.AffectsControlFlow
	public void patchSetAreaOpaque(PatchContext ctx) {
		ctx.jumpToStart();
		// if (EarsMod.interceptSetAreaOpaque(this, ...)) return;
		LabelNode label = new LabelNode();
		ctx.add(
			ALOAD(0),
			ILOAD(1),
			ILOAD(2),
			ILOAD(3),
			ILOAD(4),
			INVOKESTATIC("com/unascribed/ears/Ears", "interceptSetAreaOpaque", "(Lnet/minecraft/client/renderer/ImageBufferDownload;IIII)Z"),
			IFNE(label),
			RETURN(),
			label
		);
	}

}
