package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("com.mojang.minecraft.entity.EntityPlayer")
public class EntityPlayerTransformer extends MiniTransformer {
	
	@Patch.Method("onLivingUpdate()V")
	public void patchOnLivingUpdate(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "onUpdate", "(Lcom/mojang/minecraft/entity/EntityPlayer;)V")
		);
	}
	
}
