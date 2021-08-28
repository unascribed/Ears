package com.unascribed.ears.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.unascribed.ears.asm.mini.annotation.Patch;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.asm.mini.MiniTransformer;
import com.unascribed.ears.asm.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.ImageBufferDownload")
public class ImageBufferDownloadTransformer extends MiniTransformer {

	// Intercepting control flow means we need to compute frames; Mini doesn't do this by default
	// and doesn't have a tuneable
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		byte[] sup = super.transform(name, transformedName, basicClass);
		if (sup != basicClass) {
			ClassReader reader = new ClassReader(sup);
			ClassNode clazz = new ClassNode();
			reader.accept(clazz, 0);
			
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			clazz.accept(writer);
			return writer.toByteArray();
		} else {
			return basicClass;
		}
	}
	
	@Patch.Method(descriptor="(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", mcp="parseUserSkin", srg="func_78432_a")
	public void patchParseUserSkin(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching parseUserSkin");
		ctx.jumpToStart();
		// return EarsMod.interceptParseUserSkin(this, ...);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new IntInsnNode(ALOAD, 1));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"interceptParseUserSkin", "(Lnet/minecraft/client/renderer/ImageBufferDownload;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false));
		ctx.add(new InsnNode(ARETURN));
	}

}
