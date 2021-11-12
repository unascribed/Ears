/**
 * MCAuthLib
 * 
 * Copyright (C) 2013-2021 Steveice10
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.unascribed.ears.legacy.mcauthlib.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.unascribed.ears.legacy.mcauthlib.data.GameProfile;
import com.unascribed.ears.legacy.mcauthlib.util.HTTP;
import com.unascribed.ears.legacy.mcauthlib.util.UUIDSerializer;
import com.unascribed.ears.legacy.mcauthlib.util.HTTP.Function;
import com.unascribed.ears.legacy.nanojson.JsonArray;
import com.unascribed.ears.legacy.nanojson.JsonObject;

/**
 * Service used for session-related queries.
 */
public class SessionService extends Service {
	private static final URI DEFAULT_BASE_URI = URI.create("https://sessionserver.mojang.com/session/minecraft/");
	private static final String PROFILE_ENDPOINT = "profile";

	/**
	 * Creates a new SessionService instance.
	 */
	public SessionService() {
		super(DEFAULT_BASE_URI);
	}

	/**
	 * Fills in the properties of a profile.
	 *
	 * @param profile Profile to fill in the properties of.
	 * @return The given profile, after filling in its properties.
	 * @throws NoSuchElementException If the property lookup fails.
	 */
	public GameProfile fillProfileProperties(GameProfile profile) throws NoSuchElementException {
		if(profile.getId() == null) {
			return profile;
		}

		try {
			MinecraftProfileResponse response = HTTP.makeRequest(this.getProxy(), this.getEndpointUri(PROFILE_ENDPOINT + "/" + UUIDSerializer.fromUUID(profile.getId()), Collections.singletonMap("unsigned", "false")), null, MinecraftProfileResponse.FUNC);
			if(response == null) {
				throw new NoSuchElementException("Couldn't fetch profile properties for " + profile + " as the profile does not exist.");
			}

			profile.setProperties(response.properties);
			return profile;
		} catch(IOException e) {
			throw (NoSuchElementException)new NoSuchElementException("Couldn't look up profile properties for " + profile + ".").initCause(e);
		}
	}

	@Override
	public String toString() {
		return "SessionService{}";
	}

	private static class MinecraftProfileResponse {
		public static final Function<Object, MinecraftProfileResponse> FUNC = new Function<Object, SessionService.MinecraftProfileResponse>() {
			@Override
			public MinecraftProfileResponse apply(Object t) {
				if (!(t instanceof JsonObject)) return null;
				return from((JsonObject)t);
			}
		};
		
		public UUID id;
		public String name;
		public List<GameProfile.Property> properties;

		public static MinecraftProfileResponse from(JsonObject obj) {
			MinecraftProfileResponse mpr = new MinecraftProfileResponse();
			mpr.id = UUIDSerializer.fromString(obj.getString("id"));
			mpr.name = obj.getString("name");
			mpr.properties = new ArrayList<GameProfile.Property>();
			JsonArray props = obj.getArray("properties");
			if (props != null) {
				for (Object o : props) {
					if (o instanceof JsonObject) {
						mpr.properties.add(GameProfile.Property.from((JsonObject)o));
					}
				}
			}
			return mpr;
		}
	}
}
