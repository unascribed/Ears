package com.unascribed.ears;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;

public class EarsModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		Session s = MinecraftClient.getInstance().getSession();
		return screen -> new ConfirmLinkScreen(
				clicked -> {
					if (clicked) {
						Util.getOperatingSystem().open(EarsCommon.getConfigUrl(s.getUsername(), s.getUuidOrNull().toString()));
					}
					MinecraftClient.getInstance().setScreen(screen);
				},
				EarsCommon.getConfigPreviewUrl(), true) {
					@Override
					public void copyToClipboard() {
						client.keyboard.setClipboard(EarsCommon.getConfigUrl(s.getUsername(), s.getUuidOrNull().toString()));
					}
				};
	}

}
