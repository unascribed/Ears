package com.unascribed.ears;

import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Mod(modid="ears", name="Ears", version="@VERSION@", useMetadata=true, clientSideOnly=true)
public class Ears {
	
	public static final Map<ITextureObject, Boolean> earsSkinStatuses = new WeakHashMap<>();
	
	public static boolean interceptSetAreaOpaque(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		if (x1 == 0 && y1 == 0 && x2 == 32 && y2 == 16) {
			// Leave the unused corners of the head texture transparent-capable for ears.
			setAreaOpaque(subject, 8, 0, 24, 8);
			setAreaOpaque(subject, 0, 8, 32, 8);
			return false;
		}
		if (x1 == 0 && y1 == 16 && x2 == 64 && y2 == 32) {
			// Leave the unused space to the right of the body texture transparent-capable for ears.
			setAreaOpaque(subject, 0, 16, 56, 32);
			return false;
		}
		return true;
	}
	
	public static void checkSkin(ThreadDownloadImageData tdid, BufferedImage img) {
		if (img.getHeight() == 64) {
			boolean allMatch = true;
			out: for (int x = 0; x < 4; x++) {
				for (int y = 32; y < 36; y++) {
					if ((img.getRGB(x, y)&0x00FFFFFF) != 0x3F23D8) {
						allMatch = false;
						break out;
					}
				}
			}
			earsSkinStatuses.put(tdid, allMatch);
		}
	}
	
	public static void addLayer(RenderPlayer rp) {
		rp.addLayer(new LayerEars(rp));
	}
	
	private static final MethodHandle setAreaOpaque;
	static {
		try {
			Method jlr = ObfuscationReflectionHelper.findMethod(ImageBufferDownload.class, "func_78433_b", void.class, int.class, int.class, int.class, int.class);
			jlr.setAccessible(true);
			setAreaOpaque = MethodHandles.lookup().unreflect(jlr);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	private static void setAreaOpaque(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		try {
			setAreaOpaque.invokeExact(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	
}
