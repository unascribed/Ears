package com.unascribed.ears.legacy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.unascribed.ears.legacy.mcauthlib.data.GameProfile;
import com.unascribed.ears.legacy.mcauthlib.service.ProfileService;
import com.unascribed.ears.legacy.mcauthlib.service.SessionService;
import com.unascribed.ears.legacy.nanojson.JsonObject;
import com.unascribed.ears.legacy.nanojson.JsonParser;
import com.unascribed.ears.legacy.nanojson.JsonWriter;

public class LegacyHelper {

	private static final class CacheEntry {
		private final long expires;
		private final UUID id;
		
		public CacheEntry(long expires, UUID id) {
			this.expires = expires;
			this.id = id;
		}
		
		public static CacheEntry fromJson(JsonObject obj) {
			return new CacheEntry(obj.getLong("expires"), UUID.fromString(obj.getString("id")));
		}
		
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.put("expires", expires);
			obj.put("id", id.toString());
			return obj;
		}
	}
	
	public static final ProfileService profileService = new ProfileService();
	public static final SessionService sessionService = new SessionService();
	
	private static final Set<UUID> slimUsers = Collections.synchronizedSet(new HashSet<UUID>());
	private static final Map<UUID, String> skinUrls = Collections.synchronizedMap(new HashMap<UUID, String>());
	
	private static final Map<String, CacheEntry> cache = Collections.synchronizedMap(new HashMap<String, CacheEntry>());
	
	private static boolean loaded;
	
	public static boolean isSlimArms(String username) {
		if (!cache.containsKey(username)) return false;
		return slimUsers.contains(getUuid(username));
	}
	
	public static String getSkinUrl(String username) {
		UUID id = getUuid(username);
		if (!skinUrls.containsKey(id)) {
			try {
				GameProfile profile = new GameProfile(id, username);
				sessionService.fillProfileProperties(profile);
				if (profile.getTexture(GameProfile.TextureType.SKIN).getModel() == GameProfile.TextureModel.SLIM) {
					slimUsers.add(id);
				} else {
					slimUsers.remove(id);
				}
				skinUrls.put(id, profile.getTexture(GameProfile.TextureType.SKIN, false).getURL());
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println("[Ears] Profile lookup failed");
			}
		}
		return skinUrls.get(id);
	}
	
	public static UUID getUuid(final String username) {
		if (!loaded) load();
		if (!cache.containsKey(username)) {
			profileService.findProfilesByName(new String[]{username}, new ProfileService.ProfileLookupCallback() {
				@Override
				public void onProfileLookupSucceeded(GameProfile profile) {
					cache.put(username, new CacheEntry(System.currentTimeMillis()+TimeUnit.DAYS.toMillis(7), profile.getId()));
				}

				@Override
				public void onProfileLookupFailed(GameProfile profile, Exception e) {
					e.printStackTrace();
					System.err.println("[Ears] Profile lookup failed");
					cache.put(username, new CacheEntry(0, UUID.nameUUIDFromBytes(("OfflinePlayer:"+username).getBytes(StandardCharsets.UTF_8))));
				}
			}, false);
			save();
		}
		return cache.get(username).id;
	}

	private static void save() {
		File f = new File("ears-usercache.json");
		JsonObject obj = new JsonObject();
		for (Map.Entry<String, CacheEntry> en : cache.entrySet()) {
			if (en.getValue().expires < System.currentTimeMillis()) continue;
			obj.put(en.getKey(), en.getValue().toJson());
		}
		try {
			OutputStream out = new FileOutputStream(f);
			try {
				JsonWriter.on(out).object(obj).done();
			} finally {
				out.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("[Ears] Failed to save usercache");
		}
	}

	private static void load() {
		loaded = true;
		File f = new File("ears-usercache.json");
		if (!f.exists()) return;
		try {
			InputStream in = new FileInputStream(f);
			try {
				JsonObject obj = JsonParser.object().from(in);
				for (String key : obj.keySet()) {
					CacheEntry entry = CacheEntry.fromJson(obj.getObject(key));
					if (entry.expires < System.currentTimeMillis()) continue;
					cache.put(key, entry);
				}
			} finally {
				in.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("[Ears] Failed to load usercache");
		}
	}
	
}
