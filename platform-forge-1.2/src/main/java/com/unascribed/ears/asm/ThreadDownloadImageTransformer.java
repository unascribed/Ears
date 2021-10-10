package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("ahw")
public class ThreadDownloadImageTransformer extends MiniTransformer {

	@Patch.Method("run()V")
	public void patchRun(PatchContext ctx) {
		ctx.search(GETFIELD("ahw", "a", "Ljava/lang/String;")).jumpAfter();
		// new URL(location) -> new URL(EarsMod.amendSkinUrl(location))
		ctx.add(
			INVOKESTATIC("com/unascribed/ears/Ears", "amendSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;")
		);
		
		ctx.search(PUTFIELD("dm", "a", "Ljava/awt/image/BufferedImage;")).next().jumpAfter();
		// EarsMod.checkSkin(this, this.imageData.image);
		ctx.add(
			ALOAD(0),
			ALOAD(0),
			GETFIELD("ahw", "c", "Ldm;"),
			GETFIELD("dm", "a", "Ljava/awt/image/BufferedImage;"),
			INVOKESTATIC("com/unascribed/ears/Ears", "checkSkin", "(Ljava/lang/Object;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
}
