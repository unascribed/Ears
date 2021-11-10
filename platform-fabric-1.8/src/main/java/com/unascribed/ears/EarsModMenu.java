package com.unascribed.ears;

import java.awt.Desktop;
import java.net.URI;
import java.util.function.Function;

import com.unascribed.ears.common.EarsCommon;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Session;

public class EarsModMenu implements ModMenuApi {

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		Session s = MinecraftClient.getInstance().getSession();
		return screen -> new ConfirmChatLinkScreen(
				(clicked, i) -> {
					if (clicked) {
						try {
							Desktop.getDesktop().browse(URI.create(EarsCommon.getConfigUrl(s.getUsername(), s.getUuid())));
						} catch (Throwable t) {}
					}
					MinecraftClient.getInstance().openScreen(screen);
				},
				EarsCommon.getConfigPreviewUrl(), 0, true) {
					{disableWarning();}
					@Override
					public void copyToClipboard() {
						setClipboard(EarsCommon.getConfigUrl(s.getUsername(), s.getUuid()));
					}
				};
	}

	@Override
	public String getModId() {
		return "ears";
	}

}
