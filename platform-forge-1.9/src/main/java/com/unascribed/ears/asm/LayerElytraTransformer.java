package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.asm.tree.LabelNode;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;


@Patch.Class("net.minecraft.client.renderer.entity.layers.LayerElytra")
public class LayerElytraTransformer extends MiniTransformer {

	@Patch.Method("func_177141_a(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V")
	@Patch.Method.AffectsControlFlow
	public void patchDoRenderLayer(PatchContext ctx) {
		ctx.jumpToStart();
		LabelNode label = new LabelNode();
		ctx.add(
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "shouldSuppressElytra", "(Lnet/minecraft/entity/EntityLivingBase;)Z"),
			IFNE(label),
			RETURN(),
			label
		);
	}
	
}
