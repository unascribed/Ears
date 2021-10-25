package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("com.mojang.minecraft.entity.model.ModelBiped")
public class ModelBipedTransformer extends MiniTransformer {

	@Patch.Method("setRotationAngles(FFFFFF)V")
	public void patchSetRotationAngles(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			FLOAD(1),
			FLOAD(2),
			FLOAD(3),
			FLOAD(4),
			FLOAD(5),
			FLOAD(6),
			INVOKESTATIC("com/unascribed/ears/Ears", "postSetRotationAngles", "(Lcom/mojang/minecraft/entity/model/ModelBiped;FFFFFF)V")
		);
	}
	
	@Patch.Method("setRotationAnglesAndRender(FFFFFF)V")
	public void patchSetRotationAnglesAndRender(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			FLOAD(1),
			FLOAD(2),
			FLOAD(3),
			FLOAD(4),
			FLOAD(5),
			FLOAD(6),
			INVOKESTATIC("com/unascribed/ears/Ears", "postRenderModel", "(Lcom/mojang/minecraft/entity/model/ModelBiped;FFFFFF)V")
		);
	}
	
}
