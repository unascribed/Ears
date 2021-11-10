package com.unascribed.ears;

import java.awt.Desktop;
import java.net.URI;

import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Session;

public class GuiEarsConfig extends GuiConfirmOpenLink {

	public GuiEarsConfig(GuiScreen screen) {
		super(
				(clicked, i) -> {
					if (clicked) {
						try {
							Session s = Minecraft.getMinecraft().getSession();
							Desktop.getDesktop().browse(URI.create(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID())));
						} catch (Throwable t) {}
					}
					Minecraft.getMinecraft().displayGuiScreen(screen);
				},
				EarsCommon.getConfigPreviewUrl(), 0, true);
		disableSecurityWarning();
	}
	
	@Override
	public void copyLinkToClipboard() {
		Session s = Minecraft.getMinecraft().getSession();
		setClipboardString(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID()));
	}
	
}
