package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.debug.EarsLog;

@Patch.Class("bco") // net/minecraft/src/RenderPlayer
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method("<init>()V")
	public void patchConstructor(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer constructor");
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC("com/unascribed/ears/Ears", "amendPlayerRenderer", "(Lbco;)V")
		);
	}
	
	@Patch.Method("a(Lqx;DDDFF)V")
	public void patchRenderPlayer(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer render");
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "beforeRender", "(Lbco;Lqx;)V")
		);
	}
	
	@Patch.Method("a(Lqx;)V")
	public void patchRenderFirstPersonArm(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer arm");
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "beforeRender", "(Lbco;Lqx;)V")
		);
		
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("com/unascribed/ears/Ears", "renderFirstPersonArm", "(Lbco;Lqx;)V")
		);
	}
	
	@Patch.Method("a(Lqx;F)V")
	public void patchRenderSpecials(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			FLOAD(2),
			INVOKESTATIC("com/unascribed/ears/Ears", "renderSpecials", "(Lbco;Lqx;F)V")
		);
	}

}
