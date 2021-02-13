package com.unascribed.ears.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.unascribed.ears.asm.mini.annotation.Patch;
import com.unascribed.ears.common.EarsLog;
import com.unascribed.ears.asm.mini.MiniTransformer;
import com.unascribed.ears.asm.mini.PatchContext;

@Patch.Class("bax") // net/minecraft/src/ImageBufferDownload
public class ImageBufferDownloadTransformer extends MiniTransformer {
	
	@Patch.Method(descriptor="(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", mcp="parseUserSkin", srg="a")
	public void patchParseUserSkin(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching parseUserSkin");
		ctx.jumpToStart();
		// return EarsMod.interceptParseUserSkin(this, ...);
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 1));
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"interceptParseUserSkin", "(Lbax;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;"));
		ctx.add(new InsnNode(Opcodes.ARETURN));
	}

}
