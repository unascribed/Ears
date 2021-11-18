package com.unascribed.ears.api.registry;

import java.util.ArrayList;
import java.util.List;

import com.unascribed.ears.Identified;
import com.unascribed.ears.api.Cork;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.iface.EarsInhibitor;

public final class EarsInhibitorRegistry {

	private static final List<Identified<EarsInhibitor>> inhibitors = new ArrayList<Identified<EarsInhibitor>>();
	
	
	/**
	 * Registers an inhibitor that will be checked before rendering Ears features.
	 * <p>
	 * This allows you to suppress the drawing of Ears features should you want to. Your Inhibitor
	 * will receive two arguments; the {@link EarsFeatureType type of the feature} being checked,
	 * and the "peer" object, which can be safely cast to EntityPlayer (or your mappings' equivalent).
	 * The Ears API is provided by Ears common code, which has no access to the EntityPlayer type.
	 * <p>
	 * You can use this to, for example, hide all Ears features when a player is wearing a
	 * hermetically sealed suit. The EarsFeatureType enum has an "isAnchoredTo" method you can use
	 * to check what part of the body the feature protrudes from.
	 * @param namespace Your mod's namespace. This is equivalent to "modid".
	 * @param inhibitor The EarsInhibitor to register.
	 * @return a Cork you can use to unregister this inhibitor later if needed
	 */
	public static Cork register(String namespace, EarsInhibitor inhibitor) {
		synchronized(inhibitors) {
			final Identified<EarsInhibitor> iei = Identified.of(namespace, inhibitor);
			inhibitors.add(iei);
			return new Cork() {
				@Override
				public void cork() {
					synchronized(inhibitors) {
						inhibitors.remove(iei);
					}
				}
			};
		}
	}
	
	/**
	 * @return {@code null} if the feature is not inhibited for this peer, otherwise the namespace
	 * 		of the inhibitor
	 */
	public static String isInhibited(EarsFeatureType feature, Object peer) {
		synchronized(inhibitors) {
			for (Identified<EarsInhibitor> iei : inhibitors) {
				try {
					if (iei.getValue().shouldInhibit(feature, peer)) {
						return iei.getNamespace();
					}
				} catch (Throwable t) {
					t.printStackTrace();
					System.err.println("[Ears] An inhibitor registered by "+iei.getNamespace()+" threw an exception while checking if "+feature+" should be inhibited for "+peer);
				}
			}
		}
		return null;
	}
	
	private EarsInhibitorRegistry() {}
	
}
