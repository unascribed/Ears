package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("com.mojang.minecraft.entity.render.RenderPlayer")
public class RenderPlayerTransformer extends MiniTransformer {

	@Patch.Method("<init>()V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "amendPlayerRenderer", "(Lcom/mojang/minecraft/entity/render/RenderPlayer;)V")
		);
	}
	
	@Patch.Method("func_188_a(Lcom/mojang/minecraft/entity/EntityPlayer;DDDFF)V")
	public void patchRenderPlayer(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "beforeRender", "(Lcom/mojang/minecraft/entity/render/RenderPlayer;Lcom/mojang/minecraft/entity/EntityPlayer;)V")
		);
	}
	
	@Patch.Method("func_189_a(Lcom/mojang/minecraft/entity/EntityPlayer;F)V")
	public void patchRenderSpecials(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			FLOAD(2),
			INVOKESTATIC("com/unascribed/ears/Ears", "renderSpecials", "(Lcom/mojang/minecraft/entity/render/RenderPlayer;Lcom/mojang/minecraft/entity/EntityPlayer;F)V")
		);
	}
	
	@Patch.Method("func_185_b()V")
	public void patchRenderFirstPersonArm(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "beforeRender", "(Lcom/mojang/minecraft/entity/render/RenderPlayer;)V")
		);
		
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "renderFirstPersonArm", "(Lcom/mojang/minecraft/entity/render/RenderPlayer;)V")
		);
	}
	
}
