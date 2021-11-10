package com.unascribed.ears;

import java.util.Optional;
import java.util.function.Supplier;

import com.unascribed.ears.common.EarsCommon;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Session;
import net.minecraft.util.Util;

public class EarsModMenu implements ModMenuApi {

	@Override
	public String getModId() {
		return "ears";
	}
	
	@Override
	public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
		Session s = MinecraftClient.getInstance().getSession();
		return Optional.of(() -> new ConfirmChatLinkScreen(
				clicked -> {
					if (clicked) {
						Util.getOperatingSystem().open(EarsCommon.getConfigUrl(s.getUsername(), s.getUuid()));
					}
					MinecraftClient.getInstance().openScreen(screen);
				},
				EarsCommon.getConfigPreviewUrl(), true) {
					@Override
					public void copyToClipboard() {
						minecraft.keyboard.setClipboard(EarsCommon.getConfigUrl(s.getUsername(), s.getUuid()));
					}
				});
	}

}
