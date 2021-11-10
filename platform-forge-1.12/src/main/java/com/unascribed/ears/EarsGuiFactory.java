package com.unascribed.ears;

import java.awt.Desktop;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Session;
import net.minecraftforge.fml.client.IModGuiFactory;

public class EarsGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen screen) {
		Session s = Minecraft.getMinecraft().getSession();
		return new GuiConfirmOpenLink(
				(clicked, i) -> {
					if (clicked) {
						try {
							Desktop.getDesktop().browse(URI.create(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID())));
						} catch (Throwable t) {}
					}
					Minecraft.getMinecraft().displayGuiScreen(screen);
				},
				EarsCommon.getConfigPreviewUrl(), 0, true) {
					{disableSecurityWarning();}
					@Override
					public void copyLinkToClipboard() {
						setClipboardString(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID()));
					}
				};
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}

}
