package com.unascribed.ears.asm;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import com.unascribed.ears.asm.mini.MiniTransformer;
import com.unascribed.ears.asm.mini.PatchContext;
import com.unascribed.ears.asm.mini.annotation.Patch;
import com.unascribed.ears.common.EarsLog;

@Patch.Class("net.minecraft.client.renderer.entity.RenderPlayer")
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method(srg="<init>", mcp="<init>", descriptor="()V")
	public void patchConstructor(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer constructor");
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		// Ears.amendPlayerRenderer(this);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"amendPlayerRenderer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V", false));
	}
	
	@Patch.Method(srg="func_82441_a", mcp="renderFirstPersonArm", descriptor="(Lnet/minecraft/entity/player/EntityPlayer;)V")
	public void patchRenderFirstPersonArm(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer arm");
		ctx.jumpToStart();
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new IntInsnNode(ALOAD, 1));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"beforeRender", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
		
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		// Ears.renderFirstPersonArm(this, arg1);
		ctx.add(new IntInsnNode(ALOAD, 0));
		ctx.add(new IntInsnNode(ALOAD, 1));
		ctx.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
				"renderFirstPersonArm", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
	}

}
