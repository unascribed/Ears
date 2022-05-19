package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("tj")
public class ThreadDownloadImageTransformer extends MiniTransformer {

	@Patch.Method("run()V")
	public void patchRun(PatchContext ctx) {
		ctx.search(GETFIELD("tj", "a", "Ljava/lang/String;")).jumpAfter();
		// new URL(location) -> new URL(EarsMod.amendSkinUrl(location))
		ctx.add(
			INVOKESTATIC("com_unascribed_ears_Ears", "amendSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;")
		);
		
		ctx.search(PUTFIELD("ek", "a", "Ljava/awt/image/BufferedImage;")).next().jumpAfter();
		// EarsMod.checkSkin(this, this.imageData.image);
		ctx.add(
			ALOAD(0),
			GETFIELD("tj", "a", "Ljava/lang/String;"),
			ALOAD(0),
			GETFIELD("tj", "c", "Lek;"),
			GETFIELD("ek", "a", "Ljava/awt/image/BufferedImage;"),
			INVOKESTATIC("com_unascribed_ears_Ears", "checkSkin", "(Ljava/lang/String;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
}
