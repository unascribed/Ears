package com.unascribed.ears;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.function.Function;
import java.util.function.Predicate;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.unascribed.ears.common.debug.EarsLog;

import static org.objectweb.asm.Opcodes.*;

public class OneteufyvAgent {
	
	public static boolean initialized = false;

	public static void premain(String arg, Instrumentation ins) {
		System.out.println("Hello from Ears!");
		initialized = true;
		EarsLog.debug("Platform:Inject", "Agent created");
		ins.addTransformer(new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				if ("we".equals(className)) {
					return doTransform("RenderPlayer", className, 4, classfileBuffer, (mn) -> {
						if ("<init>".equals(mn.name)) {
							AbstractInsnNode ret = search(mn.instructions, InsnNode.class, (insn) -> insn.getOpcode() == RETURN, 0);
							if (ret == null) return TransformResult.FAILURE;
							InsnList inject = new InsnList();
							inject.add(new IntInsnNode(ALOAD, 0));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
									"amendPlayerRenderer", "(Lwe;)V"));
							mn.instructions.insertBefore(ret, inject);
							return TransformResult.SUCCESS;
						}
						if ("a".equals(mn.name) && "(Lyw;DDDFF)V".equals(mn.desc)) { // renderPlayer(...)
							InsnList inject = new InsnList();
							inject.add(new IntInsnNode(ALOAD, 0));
							inject.add(new IntInsnNode(ALOAD, 1));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
									"beforeRender", "(Lwe;Lyw;)V"));
							mn.instructions.insert(inject);
							return TransformResult.SUCCESS;
						}
						if ("a".equals(mn.name) && "(Lyw;F)V".equals(mn.desc)) { // renderSpecials(player, partialTicks)
							AbstractInsnNode ret = search(mn.instructions, InsnNode.class, (insn) -> insn.getOpcode() == RETURN, 0);
							if (ret == null) return TransformResult.FAILURE;
							InsnList inject = new InsnList();
							inject.add(new VarInsnNode(ALOAD, 0));
							inject.add(new VarInsnNode(ALOAD, 1));
							inject.add(new VarInsnNode(FLOAD, 2));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears", "renderSpecials", "(Lwe;Lyw;F)V"));
							mn.instructions.insertBefore(ret, inject);
							return TransformResult.SUCCESS;
						}
						if ("b".equals(mn.name) && "()V".equals(mn.desc)) { // renderFirstPersonArm()
							InsnList inject1 = new InsnList();
							inject1.add(new VarInsnNode(ALOAD, 0));
							inject1.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears", "beforeRender", "(Lwe;)V"));
							mn.instructions.insert(inject1);
							
							AbstractInsnNode ret = search(mn.instructions, InsnNode.class, (insn) -> insn.getOpcode() == RETURN, 0);
							if (ret == null) return TransformResult.FAILURE;
							InsnList inject2 = new InsnList();
							inject2.add(new VarInsnNode(ALOAD, 0));
							inject2.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears", "renderFirstPersonArm", "(Lwe;)V"));
							mn.instructions.insertBefore(ret, inject2);
							return TransformResult.SUCCESS;
						}
						return TransformResult.PASS;
					});
				} else if ("mx".equals(className)) {
					return doTransform("ImageBufferDownload", className, 1, classfileBuffer, (mn) -> {
						if ("a".equals(mn.name) && "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;".equals(mn.desc)) { // parseUserSkin(skin)
							InsnList inject = new InsnList();
							inject.add(new IntInsnNode(ALOAD, 0));
							inject.add(new IntInsnNode(ALOAD, 1));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
									"interceptParseUserSkin", "(Lmx;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;"));
							inject.add(new InsnNode(ARETURN));
							mn.instructions.insert(inject);
							return TransformResult.SUCCESS_NEEDFRAMES;
						}
						return TransformResult.PASS;
					});
				} else if ("ahw".equals(className)) {
					return doTransform("ThreadDownloadImage", className, 1, classfileBuffer, (mn) -> {
						if ("run".equals(mn.name) && "()V".equals(mn.desc)) {
							AbstractInsnNode get = search(mn.instructions, FieldInsnNode.class, (insn) ->
								insn.getOpcode() == GETFIELD && insn.owner.equals("ahw") && insn.name.equals("a") && insn.desc.equals("Ljava/lang/String;"), 0);
							if (get == null) return TransformResult.FAILURE;
							// new URL(location) -> new URL(EarsMod.amendSkinUrl(location))
							mn.instructions.insert(get, new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
									"amendSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;"));
							AbstractInsnNode put = search(mn.instructions, FieldInsnNode.class, (insn) ->
								insn.getOpcode() == PUTFIELD && insn.owner.equals("dm") && insn.name.equals("a") && insn.desc.equals("Ljava/awt/image/BufferedImage;"), 1);
							if (put == null) return TransformResult.FAILURE;
							// EarsMod.checkSkin(this, this.imageData.image);
							InsnList inject = new InsnList();
							inject.add(new IntInsnNode(ALOAD, 0));
							inject.add(new IntInsnNode(ALOAD, 0));
							inject.add(new FieldInsnNode(GETFIELD, "ahw", "c", "Ldm;"));
							inject.add(new FieldInsnNode(GETFIELD, "dm", "a", "Ljava/awt/image/BufferedImage;"));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
									"checkSkin", "(Ljava/lang/Object;Ljava/awt/image/BufferedImage;)V"));
							mn.instructions.insert(put, inject);
							return TransformResult.SUCCESS;
						}
						return TransformResult.PASS;
					});
				} else if ("xg".equals(className)) {
					return doTransform("ModelBiped", className, 1, classfileBuffer, (mn) -> {
						if ("<init>".equals(mn.name) && "(FF)V".equals(mn.desc)) {
							InsnList inject = new InsnList();
							AbstractInsnNode held = search(mn.instructions, FieldInsnNode.class, (insn) ->
									insn.getOpcode() == PUTFIELD && insn.owner.equals("xg") && insn.name.equals("r") && insn.desc.equals("I"), 0);
							if (held == null) return TransformResult.FAILURE;
							inject.add(new IntInsnNode(ALOAD, 0));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/ears/Ears",
									"modelPreconstruct", "(Lxg;)V"));
							mn.instructions.insert(held, inject);
							return TransformResult.SUCCESS;
						}
						return TransformResult.PASS;
					});
				}
				return null;
			}

		});
	}
	
	private static <T extends AbstractInsnNode> AbstractInsnNode search(InsnList haystack, Class<T> clazz, Predicate<T> predicate, int n) {
		int i = 0;
		for (AbstractInsnNode insn : haystack) {
			if (clazz.isInstance(insn)) {
				if (predicate.test((T)insn)) {
					if (i == n) {
						return insn;
					}
					i++;
				}
			}
		}
		return null;
	}

	private enum TransformResult {
		PASS, FAILURE, SUCCESS, SUCCESS_NEEDFRAMES;
	}
	
	private static byte[] doTransform(String name, String realName, int requiredInjections, byte[] clazzBytes, Function<MethodNode, TransformResult> trans) {
		EarsLog.debug("Platform:Inject", "Transforming {} ({})", name, realName);
		ClassReader cr = new ClassReader(clazzBytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		int inj = 0;
		boolean needframes = false;
		for (MethodNode mn : cn.methods) {
			TransformResult res = trans.apply(mn);
			if (res == TransformResult.SUCCESS_NEEDFRAMES) {
				inj++;
				needframes = true;
				EarsLog.debug("Platform:Inject", "Successfully transformed method {}.{}{} ({}) - need frames recompute", name, mn.name, mn.desc, realName);
			} else if (res == TransformResult.SUCCESS) {
				inj++;
				EarsLog.debug("Platform:Inject", "Successfully transformed method {}.{}{} ({})", name, mn.name, mn.desc, realName);
			} else if (res == TransformResult.FAILURE) {
				EarsLog.debug("Platform:Inject", "Failed to find injection point in {}.{}{}", name, mn.name, mn.desc);
				throw new AssertionError("Failed to find injection point in "+name+"."+mn.name+mn.desc);
			}
		}
		if (requiredInjections != inj) {
			EarsLog.debug("Platform:Inject", "Failed to find all expected methods in {}", cn.name);
			throw new AssertionError("Failed to find all expected methods in "+cn.name);
		}
		if (inj == 0) return null;
		int flags = ClassWriter.COMPUTE_MAXS;
		if (needframes) flags |= ClassWriter.COMPUTE_FRAMES;
		ClassWriter cw = new ClassWriter(flags);
		cn.accept(cw);
		EarsLog.debug("Platform:Inject", "Transform of {} ({}) successful", name, realName);
		
		return cw.toByteArray();
	}
	
}
