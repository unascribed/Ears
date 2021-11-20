package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC")
public class GalacticraftRenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method("<init>()V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		// Ears.amendPlayerRenderer(this);
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "amendPlayerRenderer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V")
		);
	}

}
