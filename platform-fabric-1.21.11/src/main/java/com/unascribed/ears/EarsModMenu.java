package com.unascribed.ears;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.unascribed.ears.common.EarsCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;

public class EarsModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		User s = Minecraft.getInstance().getUser();
		return screen -> new ConfirmLinkScreen(
				clicked -> {
					if (clicked) {
						Util.getPlatform().openUri(EarsCommon.getConfigUrl(s.getName(), s.getProfileId().toString()));
					}
					Minecraft.getInstance().setScreen(screen);
				},
				EarsCommon.getConfigPreviewUrl(), true) {
			@Override
			public void copyToClipboard() {
				minecraft.keyboardHandler.setClipboard(EarsCommon.getConfigUrl(s.getName(), s.getProfileId().toString()));
			}
		};
	}

}
