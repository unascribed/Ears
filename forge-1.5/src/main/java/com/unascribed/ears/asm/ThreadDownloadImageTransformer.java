package com.unascribed.ears.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.unascribed.ears.asm.mini.annotation.Patch;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.asm.mini.MiniTransformer;
import com.unascribed.ears.asm.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.ThreadDownloadImage")
public class ThreadDownloadImageTransformer extends MiniTransformer {

	@Patch.Method(descriptor="()V", mcp="run", srg="run")
	public void patchSetBufferedImage(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching run");
		ctx.search(new FieldInsnNode(Opcodes.GETFIELD, "bfv", "a", "Ljava/lang/String;")).jumpAfter();
		// new URL(location) -> new URL(EarsMod.amendSkinUrl(location))
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"amendSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;"));
		ctx.search(new FieldInsnNode(Opcodes.PUTFIELD, "bfu", "a", "Ljava/awt/image/BufferedImage;")).next().jumpAfter();
		// EarsMod.checkSkin(this, this.imageData.image);
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new FieldInsnNode(Opcodes.GETFIELD, "bfv", "c", "Lbfu;"));
		ctx.add(new FieldInsnNode(Opcodes.GETFIELD, "bfu", "a", "Ljava/awt/image/BufferedImage;"));
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"checkSkin", "(Ljava/lang/Object;Ljava/awt/image/BufferedImage;)V"));
	}
	
}
