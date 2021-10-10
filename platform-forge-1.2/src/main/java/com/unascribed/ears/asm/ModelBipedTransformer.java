package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("xg")
public class ModelBipedTransformer extends MiniTransformer {

	@Patch.Method("<init>(FF)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.search(PUTFIELD("xg", "r", "I")).jumpAfter();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "modelPreconstruct", "(Lxg;)V")
		);
	}
	
}
