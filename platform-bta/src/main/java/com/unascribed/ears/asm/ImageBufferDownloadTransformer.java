package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("rr")
public class ImageBufferDownloadTransformer extends MiniTransformer {
	
	@Patch.Method("a(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;")
	@Patch.Method.AffectsControlFlow
	public void patchParseUserSkin(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com_unascribed_ears_Ears", "interceptParseUserSkin", "(Lrr;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;"),
			ARETURN()
		);
	}
	
}
