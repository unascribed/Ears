package com.unascribed.ears.common.mixin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.Config;

import com.unascribed.ears.common.debug.EarsLog;

public class EarsMixinConfigPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {
		for (Config config : Mixins.getConfigs()) {
			IMixinConfig imc = config.getConfig();
			if (imc.getMixinPackage().startsWith("dev.tr7zw.firstperson.") || imc.getMixinPackage().startsWith("de.tr7zw.firstperson.")) {
				EarsLog.debug(EarsLog.Tag.COMMON_MIXIN, "Found FirstPersonModel. Scanning...");
				// Broken conflict-y mixins that do the same thing as Ears' own mixins for reducing
				// skin opacity overspill.
				List<String> mixinClassesClient = pluck(imc.getClass(), imc, "mixinClassesClient");
				if (mixinClassesClient.remove("DownloadingTextureMixin")) {
					EarsLog.debug(EarsLog.Tag.COMMON_MIXIN, "Suppressing MCP-named skin transparency mixin.");
				} else if (mixinClassesClient.remove("PlayerSkinTextureMixin")) {
					EarsLog.debug(EarsLog.Tag.COMMON_MIXIN, "Suppressing Yarn-named skin transparency mixin.");
				} else {
					EarsLog.debug(EarsLog.Tag.COMMON_MIXIN, "Didn't find any mixins to suppress.");
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T pluck(Class<?> clazz, Object inst, String field) {
		Field f = null;
		Class<?> cursor = clazz;
		while (f == null && cursor != null) {
			try {
				f = cursor.getDeclaredField(field);
			} catch (NoSuchFieldException ignore) {}
			cursor = cursor.getSuperclass();
		}
		if (f == null) throw new NoSuchFieldError(field);
		f.setAccessible(true);
		try {
			return (T)f.get(inst);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}

	
	
}
