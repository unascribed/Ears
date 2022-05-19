package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("tz")
public class TexturedQuadTransformer extends MiniTransformer {

	@Patch.Method("<init>([Lib;IIII)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
				ALOAD(0),
				ALOAD(1),
				ILOAD(2),
				ILOAD(3),
				ILOAD(4),
				ILOAD(5),
				INVOKESTATIC("com_unascribed_ears_Ears", "amendTexturedQuad", "(Ltz;[Lib;IIII)V")
		);
	}
	
}
