package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("bax") // net/minecraft/src/ImageBufferDownload
public class ImageBufferDownloadTransformer extends MiniTransformer {
	
	@Patch.Method("a(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;")
	@Patch.Method.AffectsControlFlow
	public void patchParseUserSkin(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "interceptParseUserSkin", "(Lbax;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;"),
			ARETURN()
		);
	}

}
