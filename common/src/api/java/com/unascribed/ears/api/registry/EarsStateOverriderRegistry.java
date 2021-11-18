package com.unascribed.ears.api.registry;

import java.util.ArrayList;
import java.util.List;

import com.unascribed.ears.Identified;
import com.unascribed.ears.api.Cork;
import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.OverrideResult;
import com.unascribed.ears.api.iface.EarsStateOverrider;

public final class EarsStateOverriderRegistry {

	private static final List<Identified<EarsStateOverrider>> overriders = new ArrayList<Identified<EarsStateOverrider>>();
	
	
	/**
	 * Registers a state overrider that will be checked before rendering Ears features.
	 * <p>
	 * This allows you to un-prevent the drawing of Ears features that are normally suppressed by
	 * the wearing of certain types of equipment should you want to, or disable detection of various
	 * other things. Your StateOverrider will receive two arguments; the
	 * {@link EarsStateType type of state} being checked, and the "peer" object, which can be safely
	 * cast to EntityPlayer (or your mappings' equivalent). The Ears API is provided by Ears common
	 * code, which has no access to the EntityPlayer type.
	 * <p>
	 * You can use this to, for example, force claws to render when wearing boots if your mod is
	 * causing boots to be hidden.
	 * @param namespace Your mod's namespace. This is equivalent to "modid".
	 * @param overrider The EarsStateOverrider to register.
	 * @return a Cork you can use to unregister this overrider later if needed
	 */
	public static Cork register(String namespace, EarsStateOverrider overrider) {
		synchronized(overriders) {
			final Identified<EarsStateOverrider> ieeo = Identified.of(namespace, overrider);
			overriders.add(ieeo);
			return new Cork() {
				@Override
				public void cork() {
					synchronized(overriders) {
						overriders.remove(ieeo);
					}
				}
			};
		}
	}
	
	/**
	 * @return {@code true} if the given state is active, plus the namespace if an
	 * 		override was applied
	 */
	public static Identified<Boolean> isActive(EarsStateType state, Object peer, boolean def) {
		synchronized(overriders) {
			for (Identified<EarsStateOverrider> ieeo : overriders) {
				try {
					OverrideResult res = ieeo.getValue().isActive(state, peer);
					if (res == null || res == OverrideResult.DEFAULT) continue;
					return Identified.of(ieeo.getNamespace(), res == OverrideResult.TRUE);
				} catch (Throwable t) {
					t.printStackTrace();
					System.err.println("[Ears] An overrider registered by "+ieeo.getNamespace()+" threw an exception while checking if "+state+" is equipped on "+peer);
				}
			}
		}
		return Identified.of(null, def);
	}
	
	private EarsStateOverriderRegistry() {}
	
}
