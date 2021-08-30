package com.unascribed.ears.common.agent;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

// Annotations ignored prior to 1.7 or 1.6 or so
@IFMLLoadingPlugin.Name("Ears")
@IFMLLoadingPlugin.TransformerExclusions("com.unascribed.ears")
@IFMLLoadingPlugin.SortingIndex(1001)
public class OldForgeCoremod implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"com.unascribed.ears.asm.PlatformTransformerAdapter"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
