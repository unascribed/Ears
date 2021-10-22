package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("net.minecraft.client.model.ModelRenderer")
public class ModelRendererTransformer extends MiniTransformer {

	@Patch.Method("func_78785_a(F)V")
	public void patchRender(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "preRender","(Lnet/minecraft/client/model/ModelRenderer;)V")
		);
		
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "postRender","(Lnet/minecraft/client/model/ModelRenderer;)V")
		);
	}
	
}
