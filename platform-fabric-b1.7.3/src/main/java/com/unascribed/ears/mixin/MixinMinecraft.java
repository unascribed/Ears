package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsMod;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
	@Inject(method = {"<init>"}, at = {@At("RETURN")})
	private void onInit(CallbackInfo info) {
		EarsMod.client = (Minecraft) (Object) this;
	}
}
