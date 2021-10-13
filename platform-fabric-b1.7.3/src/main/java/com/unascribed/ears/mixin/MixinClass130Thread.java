package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.unascribed.ears.common.legacy.mcauthlib.data.GameProfile;
import com.unascribed.ears.common.legacy.mcauthlib.service.ProfileService;
import com.unascribed.ears.EarsMod;

@Mixin(targets = "net/minecraft/class_130$1")
public class MixinClass130Thread {
	@ModifyArg(method = "run()V", at = @At(value = "INVOKE", target = "Ljava/net/URL;<init>(Ljava/lang/String;)V"), remap = false)
	private String amendSkinUrl(String url) {
		if (url.startsWith("http://s3.amazonaws.com/MinecraftSkins/") && url.endsWith(".png")) {
			String username = url.substring(39, url.length() - 4);
			// this is called in the download thread, so it's ok to block
			final String[] newUrl = {null};
			EarsMod.profileService.findProfilesByName(new String[]{username}, new LookupCallback(newUrl), false);
			return newUrl[0];
		}
		return url;
	}

	@Mixin(targets = "net/minecraft/class_130$1")
	public static class LookupCallback implements ProfileService.ProfileLookupCallback {
		final String[] newUrl;

		public LookupCallback(String[] newUrl) {
			this.newUrl = newUrl;
		}

		@Override
		public void onProfileLookupSucceeded(GameProfile profile) {
			try {
				EarsMod.sessionService.fillProfileProperties(profile);
				this.newUrl[0] = profile.getTexture(GameProfile.TextureType.SKIN, false).getURL();
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println("[Ears] Profile lookup failed");
			}
		}

		@Override
		public void onProfileLookupFailed(GameProfile profile, Exception e) {
			e.printStackTrace();
			System.err.println("[Ears] Profile lookup failed");
		}
	}
}
