package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("tj")
public class ThreadDownloadImageTransformer extends MiniTransformer {

	@Patch.Method("run()V")
	public void patchRun(PatchContext ctx) {
		ctx.search(PUTFIELD("ek", "a", "Ljava/awt/image/BufferedImage;")).next().jumpAfter();
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
