package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("ds")
public class RenderPlayerTransformer extends MiniTransformer {

	@Patch.Method("a(Lgs;F)V")
	public void patchRenderSpecials(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			FLOAD(2),
			INVOKESTATIC("com_unascribed_ears_Ears", "postRenderSpecials", "(Lds;Lgs;F)V")
		);
	}
	
	@Patch.Method("drawFirstPersonHand(Lgs;)V")
	public void patchDrawFirstPersonHand(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com_unascribed_ears_Ears", "postDrawFirstPersonHand", "(Lds;Lgs;)V")
		);
	}
	
}
