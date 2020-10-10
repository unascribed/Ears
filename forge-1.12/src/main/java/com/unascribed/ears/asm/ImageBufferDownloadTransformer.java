package com.unascribed.ears.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.elytradev.mini.MiniTransformer;
import com.elytradev.mini.PatchContext;
import com.elytradev.mini.annotation.Patch;

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
	
	@Patch.Method(descriptor="(IIII)V", mcp="setAreaOpaque", srg="func_78433_b")
	public void patchSetAreaOpaque(PatchContext ctx) {
		ctx.jumpToStart();
		// if (EarsMod.interceptSetAreaOpaque(this, ...)) return;
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new IntInsnNode(ILOAD, 1));
		ctx.add(new IntInsnNode(ILOAD, 2));
		ctx.add(new IntInsnNode(ILOAD, 3));
		ctx.add(new IntInsnNode(ILOAD, 4));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"interceptSetAreaOpaque", "(Lnet/minecraft/client/renderer/ImageBufferDownload;IIII)Z", false));
		LabelNode label = new LabelNode();
		ctx.add(new JumpInsnNode(IFNE, label));
		ctx.add(new InsnNode(RETURN));
		ctx.add(label);
	}

}
