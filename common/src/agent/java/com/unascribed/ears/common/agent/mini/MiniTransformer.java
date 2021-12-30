/*
 * Mini - an ASM-based class transformer reminiscent of MalisisCore and Mixin
 * 
 * The MIT License
 *
 * Copyright (c) 2017-2021 Una Thompson (unascribed) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.unascribed.ears.common.agent.mini;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.Type;

import com.unascribed.ears.common.agent.mini.annotation.Patch;
import com.unascribed.ears.common.debug.EarsLog;

import static org.objectweb.asm.Opcodes.*;

public abstract class MiniTransformer {

	private static final boolean DUMP = Boolean.getBoolean("ears.debug.dump");
	static {
		Path p = new File(".ears.out").toPath();
		if (Files.exists(p)) {
			try {
				Files.walkFileTree(p, new FileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
						EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "IO error cleaning output directory", exc);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "IO error cleaning output directory", e);
			}
		}
	}
	
	private interface PatchMethod {
		boolean patch(PatchContext ctx) throws Throwable;
	}
	
	private final Set<String> classes = new HashSet<String>();
	private final Map<String, List<PatchMethod>> methods = new HashMap<String, List<PatchMethod>>();
	private final Set<String> requiredMethods = new HashSet<String>();
	
	public MiniTransformer() {
		for (Patch.Class a : getClass().getAnnotationsByType(Patch.Class.class)) {
			classes.add(a.value().replace('.', '/'));
		}
		for (final Method m : getClass().getMethods()) {
			final String name = m.getName();
			for (final Patch.Method a : m.getAnnotationsByType(Patch.Method.class)) {
				final String desc = a.value();
				if (!methods.containsKey(desc)) {
					methods.put(desc, new ArrayList<PatchMethod>());
				}
				final boolean frames = m.getAnnotation(Patch.Method.AffectsControlFlow.class) != null;
				methods.get(desc).add(new PatchMethod() {
					@Override
					public boolean patch(PatchContext ctx) throws Throwable {
						try {
							m.invoke(MiniTransformer.this, ctx);
						} catch (InvocationTargetException e) {
							throw e.getCause();
						}
						return frames;
					}
					@Override
					public String toString() {
						return name;
					}
				});
				if (m.getAnnotation(Patch.Method.Optional.class) == null) {
					requiredMethods.add(desc);
				}
			}
		}
	}
	
	public final byte[] transform(String className, byte[] basicClass) {
		className = className.replace('.', '/');
		if (!classes.contains(className)) return basicClass;
		
		if (DUMP) {
			dump(className, basicClass, "before");
		}
		
		ClassReader reader = new ClassReader(basicClass);
		ClassNode clazz = new ClassNode();
		reader.accept(clazz, 0);
		
		boolean frames = false;
		
		List<String> foundMethods = new ArrayList<String>();
		Set<String> requiredsNotSeen = new HashSet<String>(requiredMethods.size());
		requiredsNotSeen.addAll(requiredMethods);
		
		for (MethodNode mn : clazz.methods) {
			String name = mn.name+mn.desc;
			foundMethods.add(name);
			List<PatchMethod> li = methods.get(name);
			if (li != null) {
				for (PatchMethod pm : li) {
					try {
						PatchContext ctx = new PatchContext(mn);
						frames |= pm.patch(ctx);
						ctx.finish();
					} catch (Throwable t) {
						throw new Error("Failed to patch "+className+"."+mn.name+mn.desc+" via "+pm, t);
					}
					EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "[{}] Successfully transformed {}.{}{} via {}", getClass().getName(), className, mn.name, mn.desc, pm);
				}
			}
			requiredsNotSeen.remove(name);
		}
		
		if (!requiredsNotSeen.isEmpty()) {
			StringBuilder msg = new StringBuilder();
			msg.append(requiredsNotSeen.size());
			msg.append(" required method");
			msg.append(requiredsNotSeen.size() == 1 ? " was" : "s were");
			msg.append(" not found while patching ");
			msg.append(className);
			msg.append("!");
			for (String name : requiredsNotSeen) {
				msg.append(" ");
				msg.append(name);
				msg.append(",");
			}
			msg.deleteCharAt(msg.length()-1);
			msg.append("\nThe following methods were found:");
			for (String name : foundMethods) {
				msg.append(" ");
				msg.append(name);
				msg.append(",");
			}
			msg.deleteCharAt(msg.length()-1);
			String msgS = msg.toString();
			EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "[{}] {}", getClass().getName(), msgS);
			throw new Error(msgS);
		}
		
		int flags = ClassWriter.COMPUTE_MAXS;
		if (frames) {
			flags |= ClassWriter.COMPUTE_FRAMES;
		}
		ClassWriter writer = new ClassWriter(flags);
		clazz.accept(writer);
		byte[] bys = writer.toByteArray();
		if (DUMP) {
			dump(className, bys, "after");
		}
		return bys;
	}
	
	private static void dump(String className, byte[] bys, String phase) {
		Path root = new File(".ears.out").toPath();
		Path f = root.resolve(phase).resolve(className.replace('.', '/')+".class");
		if (!f.toAbsolutePath().startsWith(root.toAbsolutePath())) {
			EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "Cowardly refusing to dump {} ({}) as it would escape the output directory", className, phase);
			return;
		}
		try {
			Files.createDirectories(f.getParent());
			Files.write(f, bys);
		} catch (IOException e) {
			EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "IO error while dumping {} ({})", className, phase, e);
		}
	}

	protected static InsnNode NOP() { return new InsnNode(NOP); }
	protected static InsnNode ACONST_NULL() { return new InsnNode(ACONST_NULL); }
	protected static InsnNode ICONST_M1() { return new InsnNode(ICONST_M1); }
	protected static InsnNode ICONST_0() { return new InsnNode(ICONST_0); }
	protected static InsnNode ICONST_1() { return new InsnNode(ICONST_1); }
	protected static InsnNode ICONST_2() { return new InsnNode(ICONST_2); }
	protected static InsnNode ICONST_3() { return new InsnNode(ICONST_3); }
	protected static InsnNode ICONST_4() { return new InsnNode(ICONST_4); }
	protected static InsnNode ICONST_5() { return new InsnNode(ICONST_5); }
	protected static InsnNode LCONST_0() { return new InsnNode(LCONST_0); }
	protected static InsnNode LCONST_1() { return new InsnNode(LCONST_1); }
	protected static InsnNode FCONST_0() { return new InsnNode(FCONST_0); }
	protected static InsnNode FCONST_1() { return new InsnNode(FCONST_1); }
	protected static InsnNode FCONST_2() { return new InsnNode(FCONST_2); }
	protected static InsnNode DCONST_0() { return new InsnNode(DCONST_0); }
	protected static InsnNode DCONST_1() { return new InsnNode(DCONST_1); }
	protected static IntInsnNode BIPUSH(int i) { return new IntInsnNode(BIPUSH, i); }
	protected static IntInsnNode SIPUSH(int i) { return new IntInsnNode(SIPUSH, i); }
	protected static LdcInsnNode LDC(int v) { return new LdcInsnNode(v); }
	protected static LdcInsnNode LDC(float v) { return new LdcInsnNode(v); }
	protected static LdcInsnNode LDC(long v) { return new LdcInsnNode(v); }
	protected static LdcInsnNode LDC(double v) { return new LdcInsnNode(v); }
	protected static LdcInsnNode LDC(String v) { return new LdcInsnNode(v); }
	protected static LdcInsnNode LDC(Type v) { return new LdcInsnNode(v); }
	protected static VarInsnNode ILOAD(int var) { return new VarInsnNode(ILOAD, var); }
	protected static VarInsnNode LLOAD(int var) { return new VarInsnNode(LLOAD, var); }
	protected static VarInsnNode FLOAD(int var) { return new VarInsnNode(FLOAD, var); }
	protected static VarInsnNode DLOAD(int var) { return new VarInsnNode(DLOAD, var); }
	protected static VarInsnNode ALOAD(int var) { return new VarInsnNode(ALOAD, var); }
	protected static InsnNode IALOAD() { return new InsnNode(IALOAD); }
	protected static InsnNode LALOAD() { return new InsnNode(LALOAD); }
	protected static InsnNode FALOAD() { return new InsnNode(FALOAD); }
	protected static InsnNode DALOAD() { return new InsnNode(DALOAD); }
	protected static InsnNode AALOAD() { return new InsnNode(AALOAD); }
	protected static InsnNode BALOAD() { return new InsnNode(BALOAD); }
	protected static InsnNode CALOAD() { return new InsnNode(CALOAD); }
	protected static InsnNode SALOAD() { return new InsnNode(SALOAD); }
	protected static VarInsnNode ISTORE(int var) { return new VarInsnNode(ISTORE, var); }
	protected static VarInsnNode LSTORE(int var) { return new VarInsnNode(LSTORE, var); }
	protected static VarInsnNode FSTORE(int var) { return new VarInsnNode(FSTORE, var); }
	protected static VarInsnNode DSTORE(int var) { return new VarInsnNode(DSTORE, var); }
	protected static VarInsnNode ASTORE(int var) { return new VarInsnNode(ASTORE, var); }
	protected static InsnNode IASTORE() { return new InsnNode(IASTORE); }
	protected static InsnNode LASTORE() { return new InsnNode(LASTORE); }
	protected static InsnNode FASTORE() { return new InsnNode(FASTORE); }
	protected static InsnNode DASTORE() { return new InsnNode(DASTORE); }
	protected static InsnNode AASTORE() { return new InsnNode(AASTORE); }
	protected static InsnNode BASTORE() { return new InsnNode(BASTORE); }
	protected static InsnNode CASTORE() { return new InsnNode(CASTORE); }
	protected static InsnNode SASTORE() { return new InsnNode(SASTORE); }
	protected static InsnNode POP() { return new InsnNode(POP); }
	protected static InsnNode POP2() { return new InsnNode(POP2); }
	protected static InsnNode DUP() { return new InsnNode(DUP); }
	protected static InsnNode DUP_X1() { return new InsnNode(DUP_X1); }
	protected static InsnNode DUP_X2() { return new InsnNode(DUP_X2); }
	protected static InsnNode DUP2() { return new InsnNode(DUP2); }
	protected static InsnNode DUP2_X1() { return new InsnNode(DUP2_X1); }
	protected static InsnNode DUP2_X2() { return new InsnNode(DUP2_X2); }
	protected static InsnNode SWAP() { return new InsnNode(SWAP); }
	protected static InsnNode IADD() { return new InsnNode(IADD); }
	protected static InsnNode LADD() { return new InsnNode(LADD); }
	protected static InsnNode FADD() { return new InsnNode(FADD); }
	protected static InsnNode DADD() { return new InsnNode(DADD); }
	protected static InsnNode ISUB() { return new InsnNode(ISUB); }
	protected static InsnNode LSUB() { return new InsnNode(LSUB); }
	protected static InsnNode FSUB() { return new InsnNode(FSUB); }
	protected static InsnNode DSUB() { return new InsnNode(DSUB); }
	protected static InsnNode IMUL() { return new InsnNode(IMUL); }
	protected static InsnNode LMUL() { return new InsnNode(LMUL); }
	protected static InsnNode FMUL() { return new InsnNode(FMUL); }
	protected static InsnNode DMUL() { return new InsnNode(DMUL); }
	protected static InsnNode IDIV() { return new InsnNode(IDIV); }
	protected static InsnNode LDIV() { return new InsnNode(LDIV); }
	protected static InsnNode FDIV() { return new InsnNode(FDIV); }
	protected static InsnNode DDIV() { return new InsnNode(DDIV); }
	protected static InsnNode IREM() { return new InsnNode(IREM); }
	protected static InsnNode LREM() { return new InsnNode(LREM); }
	protected static InsnNode FREM() { return new InsnNode(FREM); }
	protected static InsnNode DREM() { return new InsnNode(DREM); }
	protected static InsnNode INEG() { return new InsnNode(INEG); }
	protected static InsnNode LNEG() { return new InsnNode(LNEG); }
	protected static InsnNode FNEG() { return new InsnNode(FNEG); }
	protected static InsnNode DNEG() { return new InsnNode(DNEG); }
	protected static InsnNode ISHL() { return new InsnNode(ISHL); }
	protected static InsnNode LSHL() { return new InsnNode(LSHL); }
	protected static InsnNode ISHR() { return new InsnNode(ISHR); }
	protected static InsnNode LSHR() { return new InsnNode(LSHR); }
	protected static InsnNode IUSHR() { return new InsnNode(IUSHR); }
	protected static InsnNode LUSHR() { return new InsnNode(LUSHR); }
	protected static InsnNode IAND() { return new InsnNode(IAND); }
	protected static InsnNode LAND() { return new InsnNode(LAND); }
	protected static InsnNode IOR() { return new InsnNode(IOR); }
	protected static InsnNode LOR() { return new InsnNode(LOR); }
	protected static InsnNode IXOR() { return new InsnNode(IXOR); }
	protected static InsnNode LXOR() { return new InsnNode(LXOR); }
	protected static IincInsnNode IINC(int var, int incr) { return new IincInsnNode(var, incr); }
	protected static InsnNode I2L() { return new InsnNode(I2L); }
	protected static InsnNode I2F() { return new InsnNode(I2F); }
	protected static InsnNode I2D() { return new InsnNode(I2D); }
	protected static InsnNode L2I() { return new InsnNode(L2I); }
	protected static InsnNode L2F() { return new InsnNode(L2F); }
	protected static InsnNode L2D() { return new InsnNode(L2D); }
	protected static InsnNode F2I() { return new InsnNode(F2I); }
	protected static InsnNode F2L() { return new InsnNode(F2L); }
	protected static InsnNode F2D() { return new InsnNode(F2D); }
	protected static InsnNode D2I() { return new InsnNode(D2I); }
	protected static InsnNode D2L() { return new InsnNode(D2L); }
	protected static InsnNode D2F() { return new InsnNode(D2F); }
	protected static InsnNode I2B() { return new InsnNode(I2B); }
	protected static InsnNode I2C() { return new InsnNode(I2C); }
	protected static InsnNode I2S() { return new InsnNode(I2S); }
	protected static InsnNode LCMP() { return new InsnNode(LCMP); }
	protected static InsnNode FCMPL() { return new InsnNode(FCMPL); }
	protected static InsnNode FCMPG() { return new InsnNode(FCMPG); }
	protected static InsnNode DCMPL() { return new InsnNode(DCMPL); }
	protected static InsnNode DCMPG() { return new InsnNode(DCMPG); }
	protected static JumpInsnNode IFEQ(LabelNode label) { return new JumpInsnNode(IFEQ, label); }
	protected static JumpInsnNode IFNE(LabelNode label) { return new JumpInsnNode(IFNE, label); }
	protected static JumpInsnNode IFLT(LabelNode label) { return new JumpInsnNode(IFLT, label); }
	protected static JumpInsnNode IFGE(LabelNode label) { return new JumpInsnNode(IFGE, label); }
	protected static JumpInsnNode IFGT(LabelNode label) { return new JumpInsnNode(IFGT, label); }
	protected static JumpInsnNode IFLE(LabelNode label) { return new JumpInsnNode(IFLE, label); }
	protected static JumpInsnNode IF_ICMPEQ(LabelNode label) { return new JumpInsnNode(IF_ICMPEQ, label); }
	protected static JumpInsnNode IF_ICMPNE(LabelNode label) { return new JumpInsnNode(IF_ICMPNE, label); }
	protected static JumpInsnNode IF_ICMPLT(LabelNode label) { return new JumpInsnNode(IF_ICMPLT, label); }
	protected static JumpInsnNode IF_ICMPGE(LabelNode label) { return new JumpInsnNode(IF_ICMPGE, label); }
	protected static JumpInsnNode IF_ICMPGT(LabelNode label) { return new JumpInsnNode(IF_ICMPGT, label); }
	protected static JumpInsnNode IF_ICMPLE(LabelNode label) { return new JumpInsnNode(IF_ICMPLE, label); }
	protected static JumpInsnNode IF_ACMPEQ(LabelNode label) { return new JumpInsnNode(IF_ACMPEQ, label); }
	protected static JumpInsnNode IF_ACMPNE(LabelNode label) { return new JumpInsnNode(IF_ACMPNE, label); }
	protected static JumpInsnNode GOTO(LabelNode label) { return new JumpInsnNode(GOTO, label); }
	protected static JumpInsnNode JSR(LabelNode label) { return new JumpInsnNode(JSR, label); }
	protected static VarInsnNode RET(int i) { return new VarInsnNode(RET, i); }
	protected static TableSwitchInsnNode TABLESWITCH(int min, int max, LabelNode dflt, LabelNode... labels) { return new TableSwitchInsnNode(min, max, dflt, labels); }
	protected static LookupSwitchInsnNode LOOKUPSWITCH(LabelNode dflt, int[] keys, LabelNode[] labels) { return new LookupSwitchInsnNode(dflt, keys, labels); }
	protected static InsnNode IRETURN() { return new InsnNode(IRETURN); }
	protected static InsnNode LRETURN() { return new InsnNode(LRETURN); }
	protected static InsnNode FRETURN() { return new InsnNode(FRETURN); }
	protected static InsnNode DRETURN() { return new InsnNode(DRETURN); }
	protected static InsnNode ARETURN() { return new InsnNode(ARETURN); }
	protected static InsnNode RETURN() { return new InsnNode(RETURN); }
	protected static FieldInsnNode GETSTATIC(String owner, String name, String desc) { return new FieldInsnNode(GETSTATIC, owner, name, desc); }
	protected static FieldInsnNode PUTSTATIC(String owner, String name, String desc) { return new FieldInsnNode(PUTSTATIC, owner, name, desc); }
	protected static FieldInsnNode GETFIELD(String owner, String name, String desc) { return new FieldInsnNode(GETFIELD, owner, name, desc); }
	protected static FieldInsnNode PUTFIELD(String owner, String name, String desc) { return new FieldInsnNode(PUTFIELD, owner, name, desc); }
	protected static MethodInsnNode INVOKEVIRTUAL(String owner, String name, String desc) { return new MethodInsnNode(INVOKEVIRTUAL, owner, name, desc); }
	protected static MethodInsnNode INVOKESPECIAL(String owner, String name, String desc) { return new MethodInsnNode(INVOKESPECIAL, owner, name, desc); }
	protected static MethodInsnNode INVOKESTATIC(String owner, String name, String desc) { return new MethodInsnNode(INVOKESTATIC, owner, name, desc); }
	protected static MethodInsnNode INVOKEINTERFACE(String owner, String name, String desc) { return new MethodInsnNode(INVOKEINTERFACE, owner, name, desc); }
	protected static InvokeDynamicInsnNode INVOKEDYNAMIC(String name, String desc, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) { return new InvokeDynamicInsnNode(name, desc, bootstrapMethodHandle, bootstrapMethodArguments); }
	protected static TypeInsnNode NEW(String desc) { return new TypeInsnNode(NEW, desc); }
	protected static IntInsnNode NEWARRAY(int i) { return new IntInsnNode(NEWARRAY, i); }
	protected static TypeInsnNode ANEWARRAY(String desc) { return new TypeInsnNode(ANEWARRAY, desc); }
	protected static InsnNode ARRAYLENGTH() { return new InsnNode(ARRAYLENGTH); }
	protected static InsnNode ATHROW() { return new InsnNode(ATHROW); }
	protected static TypeInsnNode CHECKCAST(String desc) { return new TypeInsnNode(CHECKCAST, desc); }
	protected static TypeInsnNode INSTANCEOF(String desc) { return new TypeInsnNode(INSTANCEOF, desc); }
	protected static InsnNode MONITORENTER() { return new InsnNode(MONITORENTER); }
	protected static InsnNode MONITOREXIT() { return new InsnNode(MONITOREXIT); }
	protected static MultiANewArrayInsnNode MULTIANEWARRAY(String desc, int dim) { return new MultiANewArrayInsnNode(desc, dim); }
	protected static JumpInsnNode IFNULL(LabelNode label) { return new JumpInsnNode(IFNULL, label); }
	protected static JumpInsnNode IFNONNULL(LabelNode label) { return new JumpInsnNode(IFNONNULL, label); }

}
