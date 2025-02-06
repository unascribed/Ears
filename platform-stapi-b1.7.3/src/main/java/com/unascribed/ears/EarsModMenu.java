package com.unascribed.ears;

import java.awt.Desktop;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import com.unascribed.ears.common.EarsCommon;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class EarsModMenu implements ModMenuApi {

	@Override
	public String getModId() {
		return "ears";
	}
	
	@Override
	public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
		return Optional.of(() -> new Screen() {
			@Override
			public void init() {
				try {
					Desktop.getDesktop().browse(URI.create(EarsCommon.getConfigUrl(minecraft.session.username, null)));
				} catch (Throwable t) {}
				minecraft.setScreen(screen);
			}
		});
	}

}
