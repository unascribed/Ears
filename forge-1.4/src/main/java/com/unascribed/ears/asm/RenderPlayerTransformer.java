package com.unascribed.ears.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import com.unascribed.ears.asm.mini.MiniTransformer;
import com.unascribed.ears.asm.mini.PatchContext;
import com.unascribed.ears.asm.mini.annotation.Patch;
import com.unascribed.ears.common.EarsLog;

@Patch.Class("bco") // net/minecraft/src/RenderPlayer
public class RenderPlayerTransformer extends MiniTransformer {
	
	@Patch.Method(srg="<init>", mcp="<init>", descriptor="()V")
	public void patchConstructor(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer constructor");
		ctx.search(new InsnNode(Opcodes.RETURN)).jumpBefore();
		// Ears.amendPlayerRenderer(this);
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"amendPlayerRenderer", "(Lbco;)V"));
	}
	
	@Patch.Method(srg="a", mcp="renderPlayer", descriptor="(Lqx;DDDFF)V")
	public void patchRenderPlayer(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer render");
		ctx.jumpToStart();
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 1));
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"beforeRender", "(Lbco;Lqx;)V"));
	}
	
	@Patch.Method(srg="a", mcp="renderFirstPersonArm", descriptor="(Lqx;)V")
	public void patchRenderFirstPersonArm(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching player renderer arm");
		ctx.jumpToStart();
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 1));
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"beforeRender", "(Lbco;Lqx;)V"));
		
		ctx.search(new InsnNode(Opcodes.RETURN)).jumpBefore();
		// Ears.renderFirstPersonArm(this, arg1);
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 1));
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"renderFirstPersonArm", "(Lbco;Lqx;)V"));
	}
	
	@Patch.Method(srg="a", mcp="renderSpecials", descriptor="(Lqx;F)V")
	public void patchRenderSpecials(PatchContext ctx) {
		ctx.search(new InsnNode(Opcodes.RETURN)).jumpBefore();
		// Ears.renderSpecials(this, arg1, arg2);
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 0));
		ctx.add(new IntInsnNode(Opcodes.ALOAD, 1));
		ctx.add(new IntInsnNode(Opcodes.FLOAD, 2));
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/ears/Ears",
				"renderSpecials", "(Lbco;Lqx;F)V"));
	}

}
