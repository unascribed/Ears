package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.mini.asm.tree.LabelNode;

@Patch.Class("rr")
public class ImageBufferDownloadTransformer extends MiniTransformer {

	@Patch.Method("setAreaOpaque(IIII)V")
	@Patch.Method.AffectsControlFlow
	public void patchSetAreaOpaque(PatchContext ctx) {
		ctx.jumpToStart();
		LabelNode afterRet = new LabelNode();
		ctx.add(
			ALOAD(0),
			ILOAD(1),
			ILOAD(2),
			ILOAD(3),
			ILOAD(4),
			INVOKESTATIC("com_unascribed_ears_Ears", "interceptSetAreaOpaque", "(Lrr;IIII)Z"),
			IFEQ(afterRet),
			RETURN(),
			afterRet
		);
	}
	
}
