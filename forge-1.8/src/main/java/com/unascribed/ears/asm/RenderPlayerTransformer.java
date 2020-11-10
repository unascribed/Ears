package com.unascribed.ears.asm;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.unascribed.ears.asm.mini.annotation.Patch;
import com.unascribed.ears.asm.mini.MiniTransformer;
import com.unascribed.ears.asm.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.entity.RenderPlayer")
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method(srg="<init>", mcp="<init>", descriptor="(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		// EarsMod.addLayer(this);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"addLayer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V", false));
	}

}
