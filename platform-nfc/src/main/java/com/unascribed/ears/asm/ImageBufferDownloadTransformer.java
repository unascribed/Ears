package com.unascribed.ears.asm;

import com.unascribed.ears.common.agent.mini.MiniTransformer;
import com.unascribed.ears.common.agent.mini.PatchContext;
import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.agent.mini.asm.tree.LabelNode;

@Patch.Class("rr")
public class ImageBufferDownloadTransformer extends MiniTransformer {
	
	@Patch.Method("a(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;")
	public void patchParseUserSkin(PatchContext ctx) {
		// return bufferedimage;
		ctx.search(
			ALOAD(2),
			ARETURN()
		).jumpBefore();
		// EarsMod.preprocessSkin(this, image, bufferedimage);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			ALOAD(2),
			INVOKESTATIC("com_unascribed_ears_Ears", "preprocessSkin", "(Lrr;Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)V")
		);
	}

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
