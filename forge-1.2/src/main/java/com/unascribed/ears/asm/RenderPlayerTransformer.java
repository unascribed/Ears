package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;

@Patch.Class("we")
public class RenderPlayerTransformer extends MiniTransformer {

	@Patch.Method("<init>()V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "amendPlayerRenderer", "(Lwe;)V")
		);
	}
	
	@Patch.Method("a(Lyw;DDDFF)V")
	public void patchRenderPlayer(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "beforeRender", "(Lwe;Lyw;)V")
		);
	}
	
	@Patch.Method("a(Lyw;F)V")
	public void patchRenderSpecials(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			FLOAD(2),
			INVOKESTATIC("com/unascribed/ears/Ears", "renderSpecials", "(Lwe;Lyw;F)V")
		);
	}
	
	@Patch.Method("b()V")
	public void patchRenderFirstPersonArm(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "beforeRender", "(Lwe;)V")
		);
		
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "renderFirstPersonArm", "(Lwe;)V")
		);
	}
	
}
