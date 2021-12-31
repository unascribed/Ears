package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("net.minecraft.client.Minecraft")
public class MinecraftTransformer extends MiniTransformer {

	@Patch.Method("<init>(Ljava/awt/Component;Ljava/awt/Canvas;Lnet/minecraft/client/MinecraftApplet;IIZ)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com_unascribed_ears_Ears", "init", "(Lnet/minecraft/client/Minecraft;)V")
		);
	}
	
}
