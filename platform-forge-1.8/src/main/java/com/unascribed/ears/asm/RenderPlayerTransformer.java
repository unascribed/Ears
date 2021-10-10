package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.entity.RenderPlayer")
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method("<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		// EarsMod.addLayer(this);
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "addLayer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V")
		);
	}
	
	@Patch.Method("func_177139_c(Lnet/minecraft/client/entity/AbstractClientPlayer;)V")
	public void patchRenderLeftArm(PatchContext ctx) {
		ctx.jumpToLastReturn();
		// EarsMod.renderLeftArm(this, arg1);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "renderLeftArm", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/client/entity/AbstractClientPlayer;)V")
		);
	}
	
	@Patch.Method("func_177138_b(Lnet/minecraft/client/entity/AbstractClientPlayer;)V")
	public void patchRenderRightArm(PatchContext ctx) {
		ctx.jumpToLastReturn();
		// EarsMod.renderRightArm(this, arg1);
		ctx.add(
				ALOAD(0),
				ALOAD(1),
				INVOKESTATIC("com/unascribed/ears/Ears", "renderRightArm", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/client/entity/AbstractClientPlayer;)V")
			);
	}

}
