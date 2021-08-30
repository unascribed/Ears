package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.entity.RenderPlayer")
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method("<init>()V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		// Ears.amendPlayerRenderer(this);
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "amendPlayerRenderer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V")
		);
	}
	
	@Patch.Method("b(Lsq;)V")
	public void patchRenderFirstPersonArm(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "beforeRender", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/entity/player/EntityPlayer;)V")
		);
		
		ctx.jumpToLastReturn();
		// Ears.renderFirstPersonArm(this, arg1);
		ctx.add(
				ALOAD(0),
				ALOAD(1),
				INVOKESTATIC("com/unascribed/ears/Ears", "renderFirstPersonArm", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/entity/player/EntityPlayer;)V")
			);
	}

}
