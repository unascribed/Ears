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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.unascribed.ears.common.EarsLog;

import static org.objectweb.asm.Opcodes.*;

public class NFCAgent {

	public static void premain(String arg, Instrumentation ins) {
		System.out.println("Hello from Ears!");
		EarsLog.debug("Platform:Inject", "Agent created");
		ins.addTransformer(new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				if ("ds".equals(className)) {
					return doTransform("RenderPlayer", className, 2, classfileBuffer, (mn) -> {
						if ("a".equals(mn.name) && "(Lgs;F)V".equals(mn.desc)) { // ~renderSpecials(player, partialTicks)
							AbstractInsnNode ret = search(mn.instructions, InsnNode.class, (insn) -> insn.getOpcode() == RETURN, 0);
							if (ret == null) return TransformResult.FAILURE;
							InsnList inject = new InsnList();
							inject.add(new VarInsnNode(ALOAD, 0));
							inject.add(new VarInsnNode(ALOAD, 1));
							inject.add(new VarInsnNode(FLOAD, 2));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com_unascribed_ears_Ears", "postRenderSpecials", "(Lds;Lgs;F)V"));
							mn.instructions.insertBefore(ret, inject);
							return TransformResult.SUCCESS;
						}
						if ("drawFirstPersonHand".equals(mn.name) && "(Lgs;)V".equals(mn.desc)) {
							AbstractInsnNode ret = search(mn.instructions, InsnNode.class, (insn) -> insn.getOpcode() == RETURN, 0);
							if (ret == null) return TransformResult.FAILURE;
							InsnList inject = new InsnList();
							inject.add(new VarInsnNode(ALOAD, 0));
							inject.add(new VarInsnNode(ALOAD, 1));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com_unascribed_ears_Ears", "postDrawFirstPersonHand", "(Lds;Lgs;)V"));
							mn.instructions.insertBefore(ret, inject);
							return TransformResult.SUCCESS;
						}
						return TransformResult.PASS;
					});
				} else if ("rr".equals(className)) {
					return doTransform("ImageBufferDownload", className, 1, classfileBuffer, (mn) -> {
						if ("setAreaOpaque".equals(mn.name) && "(IIII)V".equals(mn.desc)) {
							InsnList inject = new InsnList();
							inject.add(new VarInsnNode(ALOAD, 0));
							inject.add(new VarInsnNode(ILOAD, 1));
							inject.add(new VarInsnNode(ILOAD, 2));
							inject.add(new VarInsnNode(ILOAD, 3));
							inject.add(new VarInsnNode(ILOAD, 4));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com_unascribed_ears_Ears", "interceptSetAreaOpaque", "(Lrr;IIII)Z"));
							LabelNode afterRet = new LabelNode();
							inject.add(new JumpInsnNode(IFEQ, afterRet));
							inject.add(new InsnNode(RETURN));
							inject.add(afterRet);
							mn.instructions.insert(inject);
							return TransformResult.SUCCESS_NEEDFRAMES;
						}
						return TransformResult.PASS;
					});
				} else if ("tj".equals(className)) {
					return doTransform("ThreadDownloadImage", className, 1, classfileBuffer, (mn) -> {
						if ("run".equals(mn.name) && "()V".equals(mn.desc)) {
							AbstractInsnNode inj = search(mn.instructions, FieldInsnNode.class,
									insn -> insn.getOpcode() == PUTFIELD && "ek".equals(insn.owner) && "a".equals(insn.name), 1);
							if (inj == null) return TransformResult.FAILURE;
							InsnList inject = new InsnList();
							inject.add(new VarInsnNode(ALOAD, 0));
							inject.add(new FieldInsnNode(GETFIELD, "tj", "a", "Ljava/lang/String;"));
							inject.add(new VarInsnNode(ALOAD, 0));
							inject.add(new FieldInsnNode(GETFIELD, "tj", "c", "Lek;"));
							inject.add(new FieldInsnNode(GETFIELD, "ek", "a", "Ljava/awt/image/BufferedImage;"));
							inject.add(new MethodInsnNode(INVOKESTATIC, "com_unascribed_ears_Ears", "checkSkin", "(Ljava/lang/String;Ljava/awt/image/BufferedImage;)V"));
							mn.instructions.insert(inj, inject);
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
				throw new AssertionError("Failed to find injection point in "+name+"."+mn.name+mn.desc);
			}
		}
		if (requiredInjections != inj) {
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
