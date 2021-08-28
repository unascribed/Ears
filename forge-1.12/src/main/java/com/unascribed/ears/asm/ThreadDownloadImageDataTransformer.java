package com.unascribed.ears.asm;

import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.elytradev.mini.MiniTransformer;
import com.elytradev.mini.PatchContext;
import com.elytradev.mini.annotation.Patch;
import com.unascribed.ears.common.debug.EarsLog;

@Patch.Class("net.minecraft.client.renderer.ThreadDownloadImageData")
public class ThreadDownloadImageDataTransformer extends MiniTransformer {

	@Patch.Method(descriptor="(Ljava/awt/image/BufferedImage;)V", mcp="setBufferedImage", srg="func_147641_a")
	public void patchSetBufferedImage(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching setBufferedImage");
		ctx.jumpToStart();
		// EarsMod.checkSkin(this, ...);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new IntInsnNode(ALOAD, 1));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"checkSkin", "(Lnet/minecraft/client/renderer/ThreadDownloadImageData;Ljava/awt/image/BufferedImage;)V", false));
	}
	
}
