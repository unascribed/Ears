package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("com.mojang.minecraft.util.SkinUrlGetPost")
public class SkinUrlGetPostTransformer extends MiniTransformer {

	@Patch.Method("getPostUrl(Ljava/lang/String;)Ljava/lang/String;")
	@Patch.Method.AffectsControlFlow
	public void patchGetPostUrl(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "amendSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;"),
			ARETURN()
		);
	}
	
}
