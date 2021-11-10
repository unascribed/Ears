package com.unascribed.ears;

import java.util.function.Function;

import com.unascribed.ears.common.EarsCommon;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Session;
import net.minecraft.util.Util;

public class EarsModMenu implements ModMenuApi {

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		Session s = MinecraftClient.getInstance().getSession();
		return screen -> new ConfirmChatLinkScreen(
				clicked -> {
					if (clicked) {
						Util.getOperatingSystem().open(EarsCommon.getConfigUrl(s.getUsername(), s.getUuid()));
					}
					MinecraftClient.getInstance().openScreen(screen);
				},
				EarsCommon.getConfigPreviewUrl(), true) {
					@Override
					public void copyToClipboard() {
						client.keyboard.setClipboard(EarsCommon.getConfigUrl(s.getUsername(), s.getUuid()));
					}
				};
	}

}
