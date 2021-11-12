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
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import com.unascribed.ears.legacy.mcauthlib.data.GameProfile;
import com.unascribed.ears.legacy.mcauthlib.util.HTTP;
import com.unascribed.ears.legacy.mcauthlib.util.HTTP.Function;
import com.unascribed.ears.legacy.nanojson.JsonArray;

/**
 * Repository for looking up profiles by name.
 */
public class ProfileService extends Service {
	private static final URI DEFAULT_BASE_URI = URI.create("https://api.mojang.com/profiles/");
	private static final String SEARCH_ENDPOINT = "minecraft";
	
	private static final Function<Object, GameProfile[]> PROFILE_ARRAY_PARSER = new Function<Object, GameProfile[]>() {
		
		@Override
		public GameProfile[] apply(Object t) {
			if (!(t instanceof JsonArray)) return new GameProfile[0];
			JsonArray arr = (JsonArray)t;
			GameProfile[] out = new GameProfile[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				out[i] = GameProfile.from(arr.getObject(i));
			}
			return out;
		}
	};

	private static final int MAX_FAIL_COUNT = 3;
	private static final int DELAY_BETWEEN_PAGES = 100;
	private static final int DELAY_BETWEEN_FAILURES = 750;
	private static final int PROFILES_PER_REQUEST = 100;

	/**
	 * Creates a new ProfileService instance.
	 */
	public ProfileService() {
		super(DEFAULT_BASE_URI);
	}

	/**
	 * Locates profiles by their names.
	 *
	 * @param names    Names to look for.
	 * @param callback Callback to pass results to.
	 */
	public void findProfilesByName(String[] names, ProfileLookupCallback callback) {
		this.findProfilesByName(names, callback, false);
	}

	/**
	 * Locates profiles by their names.
	 *
	 * @param names    Names to look for.
	 * @param callback Callback to pass results to.
	 * @param async    Whether to perform requests asynchronously.
	 */
	public void findProfilesByName(final String[] names, final ProfileLookupCallback callback, final boolean async) {
		final Set<String> criteria = new HashSet<String>();
		for(String name : names) {
			if(name != null && !name.isEmpty()) {
				criteria.add(name.toLowerCase());
			}
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				for(Set<String> request : partition(criteria, PROFILES_PER_REQUEST)) {
					Exception error = null;
					int failCount = 0;
					boolean tryAgain = true;
					while(failCount < MAX_FAIL_COUNT && tryAgain) {
						tryAgain = false;
						try {
							GameProfile[] profiles = HTTP.makeRequest(getProxy(), getEndpointUri(SEARCH_ENDPOINT), new JsonArray(request), PROFILE_ARRAY_PARSER);
							failCount = 0;
							Set<String> missing = new HashSet<String>(request);
							for(GameProfile profile : profiles) {
								missing.remove(profile.getName().toLowerCase());
								callback.onProfileLookupSucceeded(profile);
							}

							for(String name : missing) {
								callback.onProfileLookupFailed(new GameProfile((UUID) null, name), new NoSuchElementException("Server could not find the requested profile."));
							}

							try {
								Thread.sleep(DELAY_BETWEEN_PAGES);
							} catch(InterruptedException ignored) {
							}
						} catch(IOException e) {
							error = e;
							failCount++;
							if(failCount >= MAX_FAIL_COUNT) {
								for(String name : request) {
									callback.onProfileLookupFailed(new GameProfile((UUID) null, name), error);
								}
							} else {
								try {
									Thread.sleep(DELAY_BETWEEN_FAILURES);
								} catch(InterruptedException ignored) {
								}

								tryAgain = true;
							}
						}
					}
				}
			}
		};

		if(async) {
			new Thread(runnable, "ProfileLookupThread").start();
		} else {
			runnable.run();
		}
	}

	private static Set<Set<String>> partition(Set<String> set, int size) {
		List<String> list = new ArrayList<String>(set);
		Set<Set<String>> ret = new HashSet<Set<String>>();
		for(int i = 0; i < list.size(); i += size) {
			Set<String> s = new HashSet<String>();
			s.addAll(list.subList(i, Math.min(i + size, list.size())));
			ret.add(s);
		}

		return ret;
	}

	/**
	 * Callback for reporting profile lookup results.
	 */
	public interface ProfileLookupCallback {
		/**
		 * Called when a profile lookup request succeeds.
		 *
		 * @param profile Profile resulting from the request.
		 */
		void onProfileLookupSucceeded(GameProfile profile);

		/**
		 * Called when a profile lookup request fails.
		 *
		 * @param profile Profile that failed to be located.
		 * @param e       Exception causing the failure.
		 */
		void onProfileLookupFailed(GameProfile profile, Exception e);
	}
}
