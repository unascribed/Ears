package com.unascribed.ears.legacy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.unascribed.ears.legacy.mcauthlib.data.GameProfile;
import com.unascribed.ears.legacy.mcauthlib.service.ProfileService;
import com.unascribed.ears.legacy.mcauthlib.service.SessionService;

public class LegacyHelper {

	public static final ProfileService profileService = new ProfileService();
	public static final SessionService sessionService = new SessionService();
	
	private static final Set<String> slimUsers = Collections.synchronizedSet(new HashSet<String>());
	private static final Map<String, String> skinUrls = Collections.synchronizedMap(new HashMap<String, String>());
	
	public static boolean isSlimArms(String username) {
		return slimUsers.contains(username);
	}
	
	public static String getSkinUrl(String username) {
		if (!skinUrls.containsKey(username)) {
			doLookup(username);
		}
		return skinUrls.get(username);
	}

	private static void doLookup(final String username) {
		profileService.findProfilesByName(new String[]{username}, new ProfileService.ProfileLookupCallback() {
			@Override
			public void onProfileLookupSucceeded(GameProfile profile) {
				try {
					sessionService.fillProfileProperties(profile);
					if (profile.getTexture(GameProfile.TextureType.SKIN).getModel() == GameProfile.TextureModel.SLIM) {
						slimUsers.add(username);
					} else {
						slimUsers.remove(username);
					}
					skinUrls.put(username, profile.getTexture(GameProfile.TextureType.SKIN, false).getURL());
				} catch (Throwable t) {
					t.printStackTrace();
					System.err.println("[Ears] Profile lookup failed");
				}
			}

			@Override
			public void onProfileLookupFailed(GameProfile profile, Exception e) {
				e.printStackTrace();
				System.err.println("[Ears] Profile lookup failed");
			}
		}, false);
	}
	
}
