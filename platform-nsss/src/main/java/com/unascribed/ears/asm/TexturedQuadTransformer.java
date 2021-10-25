package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("com.mojang.minecraft.render.TexturedQuad")
public class TexturedQuadTransformer extends MiniTransformer {

	@Patch.Method("<init>([Lcom/mojang/minecraft/render/PositionTexureVertex;IIII)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
				ALOAD(0),
				ALOAD(1),
				ILOAD(2),
				ILOAD(3),
				ILOAD(4),
				ILOAD(5),
				INVOKESTATIC("com/unascribed/ears/Ears", "amendTexturedQuad", "(Lcom/mojang/minecraft/render/TexturedQuad;[Lcom/mojang/minecraft/render/PositionTexureVertex;IIII)V")
		);
	}
	
}
