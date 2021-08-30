package com.unascribed.ears.common.agent;

/**
 * Platform implementations that use a non-agent system of patching (e.g. FML coremods) need to
 * provide a class that extends this and implements the platform's IClassTransformer interface or
 * equivalent. The class must have a FQN of com.unascribed.ears.asm.PlatformTransformerAdapter.
 */
public class TransformerAdapter {

	// Post-SRG FML (1.5+)
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		return EarsAgent.transform(transformedName, basicClass);
	}

	// Pre-SRG FML (1.4-)
	public byte[] transform(String name, byte[] basicClass) {
		return EarsAgent.transform(name, basicClass);
	}
	
}
