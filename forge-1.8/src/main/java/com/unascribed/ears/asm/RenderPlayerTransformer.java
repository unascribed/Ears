package com.unascribed.ears.asm;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.unascribed.ears.asm.mini.annotation.Patch;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.asm.mini.MiniTransformer;
import com.unascribed.ears.asm.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.entity.RenderPlayer")
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method(srg="<init>", mcp="<init>", descriptor="(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V")
	public void patchConstructor(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer constructor");
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		// EarsMod.addLayer(this);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"addLayer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V", false));
	}
	
	@Patch.Method(srg="func_177139_c", mcp="renderLeftArm", descriptor="(Lnet/minecraft/client/entity/AbstractClientPlayer;)V")
	public void patchRenderLeftArm(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer left arm");
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		// EarsMod.renderLeftArm(this, arg1);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new IntInsnNode(ALOAD, 1));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"renderLeftArm", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", false));
	}
	
	@Patch.Method(srg="func_177138_b", mcp="renderRightArm", descriptor="(Lnet/minecraft/client/entity/AbstractClientPlayer;)V")
	public void patchRenderRightArm(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer right arm");
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		// EarsMod.renderRightArm(this, arg1);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new IntInsnNode(ALOAD, 1));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"renderRightArm", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", false));
	}

}
