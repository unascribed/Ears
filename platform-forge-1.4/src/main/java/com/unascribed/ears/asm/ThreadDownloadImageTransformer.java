package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("bas") // net/minecraft/src/ThreadDownloadImage
public class ThreadDownloadImageTransformer extends MiniTransformer {

	@Patch.Method("run()V")
	public void patchRun(PatchContext ctx) {
		ctx.search(GETFIELD("bas", "a", "Ljava/lang/String;")).jumpAfter();
		// new URL(location) -> new URL(EarsMod.amendSkinUrl(location))
		ctx.add(
			INVOKESTATIC("com/unascribed/ears/Ears", "amendSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;")
		);
		
		ctx.search(PUTFIELD("bar", "a", "Ljava/awt/image/BufferedImage;")).next().jumpAfter();
		// EarsMod.checkSkin(this, this.imageData.image);
		ctx.add(
			ALOAD(0),
			ALOAD(0),
			GETFIELD("bas", "c", "Lbar;"),
			GETFIELD("bar", "a", "Ljava/awt/image/BufferedImage;"),
			INVOKESTATIC("com/unascribed/ears/Ears", "checkSkin", "(Ljava/lang/Object;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
}
