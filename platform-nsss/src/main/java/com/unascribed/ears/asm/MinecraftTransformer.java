package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("com.mojang.minecraft.Minecraft")
public class MinecraftTransformer extends MiniTransformer {

	@Patch.Method("<init>(Ljava/awt/Component;Ljava/awt/Canvas;Lcom/mojang/minecraft/MinecraftApplet;IIZ)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "init", "(Lcom/mojang/minecraft/Minecraft;)V")
		);
	}
	
}
