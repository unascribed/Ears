package com.unascribed.ears.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.unascribed.ears.common.debug.EarsLog;

import net.fabricmc.loader.api.FabricLoader;

public class EarsMixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean isMojFixInstalled = FabricLoader.getInstance().isModLoaded("mojangfixstationapi");
        boolean isCPMInstalled = FabricLoader.getInstance().isModLoaded("cpm");

        if (mixinClassName.startsWith("com.unascribed.ears.mixin.skinfix")) {
            if (isMojFixInstalled) {
                EarsLog.debug(EarsLog.Tag.PLATFORM_LOAD, "MojangFix is installed, disabling Ears skinfix.");
            }
            if (isCPMInstalled) {
                EarsLog.debug(EarsLog.Tag.PLATFORM_LOAD, "Customizable Player Models is installed, disabling Ears skinfix.");
            }
            return !(isMojFixInstalled || isCPMInstalled);
        }

        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
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
