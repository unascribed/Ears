package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("com.mojang.minecraft.util.ThreadDownloadImage")
public class ThreadDownloadImageTransformer extends MiniTransformer {

	@Patch.Method("run()V")
	public void patchRun(PatchContext ctx) {
		ctx.search(PUTFIELD("com/mojang/minecraft/util/DownloadImageThreadData", "field_1706_a", "Ljava/awt/image/BufferedImage;")).next().jumpAfter();
		// EarsMod.checkSkin(this, this.imageData.image);
		ctx.add(
			ALOAD(0),
			ALOAD(0),
			GETFIELD("com/mojang/minecraft/util/ThreadDownloadImage", "field_1217_c", "Lcom/mojang/minecraft/util/DownloadImageThreadData;"),
			GETFIELD("com/mojang/minecraft/util/DownloadImageThreadData", "field_1706_a", "Ljava/awt/image/BufferedImage;"),
			INVOKESTATIC("com/unascribed/ears/Ears", "checkSkin", "(Ljava/lang/Object;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
}
