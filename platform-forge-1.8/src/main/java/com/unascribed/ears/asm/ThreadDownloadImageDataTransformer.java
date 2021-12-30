package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.debug.EarsLog;

@Patch.Class("net.minecraft.client.renderer.ThreadDownloadImageData")
public class ThreadDownloadImageDataTransformer extends MiniTransformer {

	@Patch.Method("func_147641_a(Ljava/awt/image/BufferedImage;)V")
	public void patchSetBufferedImage(PatchContext ctx) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Patching setBufferedImage");
		ctx.jumpToStart();
		// EarsMod.checkSkin(this, ...);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "checkSkin", "(Lnet/minecraft/client/renderer/ThreadDownloadImageData;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
}
