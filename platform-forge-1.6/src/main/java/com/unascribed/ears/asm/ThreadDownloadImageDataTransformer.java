package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.debug.EarsLog;

@Patch.Class("net.minecraft.client.renderer.ThreadDownloadImageData")
public class ThreadDownloadImageDataTransformer extends MiniTransformer {
	
	@Patch.Method("func_110556_a(Ljava/awt/image/BufferedImage;)V")
	public void patchSetBufferedImage(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching setBufferedImage");
		ctx.jumpToStart();
		// EarsMod.checkSkin(this, ...);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "checkSkin", "(Lnet/minecraft/client/renderer/ThreadDownloadImageData;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
	// extremely convenient synthetic method
	@Patch.Method("func_110554_a(Lnet/minecraft/client/renderer/ThreadDownloadImageData;)Ljava/lang/String;")
	public void patchGetImageUrl(PatchContext ctx) {
		// last return is an ARETURN, so we can just stuff a call in right here
		ctx.jumpToLastReturn();
		ctx.add(
			INVOKESTATIC("com/unascribed/ears/Ears", "amendSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;")
		);
	}
	
}
