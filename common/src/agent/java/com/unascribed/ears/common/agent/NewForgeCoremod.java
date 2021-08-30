package com.unascribed.ears.common.agent;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("Ears")
@IFMLLoadingPlugin.TransformerExclusions("com.unascribed.ears")
@IFMLLoadingPlugin.SortingIndex(1001)
public class NewForgeCoremod implements IFMLLoadingPlugin {

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
	public String getAccessTransformerClass() {
		return null;
	}

}
