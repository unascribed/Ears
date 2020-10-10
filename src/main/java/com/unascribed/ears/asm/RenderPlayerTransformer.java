package com.unascribed.ears.asm;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import com.elytradev.mini.MiniTransformer;
import com.elytradev.mini.PatchContext;
import com.elytradev.mini.annotation.Patch;

@Patch.Class("net.minecraft.client.renderer.entity.RenderPlayer")
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method(srg="<init>", mcp="<init>", descriptor="(Lnet/minecraft/client/renderer/entity/RenderManager;)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		// EarsMod.addLayer(this);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"addLayer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V", false));
	}

}
