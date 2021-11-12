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

package com.unascribed.ears.legacy.mcauthlib.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.unascribed.ears.legacy.nanojson.JsonObject;
import com.unascribed.ears.legacy.nanojson.JsonParser;
import com.unascribed.ears.legacy.nanojson.JsonParserException;
import com.unascribed.ears.legacy.nanojson.JsonWriter;

/**
 * Utilities for making HTTP requests.
 */
public class HTTP {

	private HTTP() {
	}
	
	public interface Function<T, R> {
		R apply(T t);
	}

	/**
	 * Makes an HTTP request.
	 *
	 * @param proxy Proxy to use when making the request.
	 * @param uri   URI to make the request to.
	 * @param input Input to provide in the request.
	 * @throws IllegalArgumentException If the given proxy or URI is null.
	 * @throws IOException If an error occurs while making the request.
	 */
	public static void makeRequest(Proxy proxy, URI uri, Object input) throws IOException {
		makeRequest(proxy, uri, input, null);
	}

	/**
	 * Makes an HTTP request.
	 *
	 * @param proxy        Proxy to use when making the request.
	 * @param uri          URI to make the request to.
	 * @param input        Input to provide in the request.
	 * @param parser       Function to parse resultant JSON object.
	 * @param <T>          Type to provide the response as.
	 * @param extraHeaders Extra headers to add to the request.
	 * @return The response of the request.
	 * @throws IllegalArgumentException If the given proxy or URI is null.
	 * @throws IOException If an error occurs while making the request.
	 */
	public static <T> T makeRequest(Proxy proxy, URI uri, Object input, Function<Object, T> parser, Map<String, String> extraHeaders) throws IOException {
		if(proxy == null) {
			throw new IllegalArgumentException("Proxy cannot be null.");
		} else if(uri == null) {
			throw new IllegalArgumentException("URI cannot be null.");
		}

		Object response;
		try {
			response = input == null ? performGetRequest(proxy, uri, extraHeaders) : performPostRequest(proxy, uri, extraHeaders, JsonWriter.string(input), "application/json");
		} catch(IOException e) {
			throw new IOException("Could not make request to '" + uri + "'.", e);
		}

		if(response != null) {
			checkForError(response);

			if(parser != null) {
				return parser.apply(response);
			}
		}

		return null;
	}

	public static <T> T makeRequest(Proxy proxy, URI uri, Object input, Function<Object, T> parser) throws IOException {
		return makeRequest(proxy, uri, input, parser, new HashMap<String, String>());
	}

	/**
	 * Makes an HTTP request as a from.
	 *
	 * @param proxy        Proxy to use when making the request.
	 * @param uri          URI to make the request to.
	 * @param input        Input to provide in the request.
	 * @param parser       Function to parse resultant JSON object.
	 * @param <T>          Type to provide the response as.
	 * @return The response of the request.
	 * @throws IllegalArgumentException If the given proxy or URI is null.
	 * @throws IOException If an error occurs while making the request.
	 */
	public static <T> T makeRequestForm(Proxy proxy, URI uri, Map<String, String> input, Function<Object, T> parser) throws IOException {
		if(proxy == null) {
			throw new IllegalArgumentException("Proxy cannot be null.");
		} else if(uri == null) {
			throw new IllegalArgumentException("URI cannot be null.");
		}

		String inputString = formMapToString(input);

		Object response;
		try {
			response = performPostRequest(proxy, uri, new HashMap<String, String>(), inputString, "application/x-www-form-urlencoded");
		} catch(IOException e) {
			throw new IOException("Could not make request to '" + uri + "'.", e);
		}

		if(response != null) {
			checkForError(response);

			if(parser != null) {
				return parser.apply(response);
			}
		}

		return null;
	}

	public static String formMapToString(Map<String, String> input) {
		StringBuilder inputString = new StringBuilder();
		for (Map.Entry<String, String> inputField : input.entrySet()) {
			if (inputString.length() > 0) {
				inputString.append("&");
			}

			try {
				inputString.append(URLEncoder.encode(inputField.getKey(), "UTF-8"));
				inputString.append("=");
				inputString.append(URLEncoder.encode(inputField.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException ignored) { }
		}

		return inputString.toString();
	}

	private static void checkForError(Object response) throws IOException {
		if(response instanceof JsonObject) {
			JsonObject object = (JsonObject)response;
			if(object.has("error")) {
				String error = object.getString("error");
				String cause = object.has("cause") ? object.getString("cause") : "";
				String errorMessage = object.has("errorMessage") ? object.getString("errorMessage") : "";
				errorMessage = object.has("error_description") ? object.getString("error_description") : errorMessage;
				if(!error.equals("")) {
					if(error.equals("ForbiddenOperationException")) {
						if (cause != null && cause.equals("UserMigratedException")) {
							throw new IOException("User migrated: "+errorMessage);
						} else {
							throw new IOException("Invalid credentials: "+errorMessage);
						}
					} else if (error.equals("authorization_pending")) {
						throw new IOException("Authorization pending: "+errorMessage);
					} else {
						throw new IOException(errorMessage);
					}
				}
			}
		}
	}

	private static Object performGetRequest(Proxy proxy, URI uri, Map<String, String> extraHeaders) throws IOException {
		HttpURLConnection connection = createUrlConnection(proxy, uri);
		for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
			connection.setRequestProperty(header.getKey(), header.getValue());
		}
		connection.setDoInput(true);

		return processResponse(connection);
	}

	private static Object performPostRequest(Proxy proxy, URI uri, Map<String, String> extraHeaders, String post, String type) throws IOException {
		byte[] bytes = post.getBytes("UTF-8");

		HttpURLConnection connection = createUrlConnection(proxy, uri);
		connection.setRequestProperty("Content-Type", type + "; charset=utf-8");
		connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
			connection.setRequestProperty(header.getKey(), header.getValue());
		}
		connection.setDoInput(true);
		connection.setDoOutput(true);

		OutputStream out = connection.getOutputStream();
		try {
			out.write(bytes);
		} finally {
			out.close();
		}

		return processResponse(connection);
	}

	public static HttpURLConnection createUrlConnection(Proxy proxy, URI uri) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection(proxy);
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(15000);
		connection.setUseCaches(false);
		return connection;
	}

	private static Object processResponse(HttpURLConnection connection) throws IOException {
		InputStream in = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
		try {
			return in != null ? JsonParser.any().from(new InputStreamReader(in)) : null;
		} catch (JsonParserException e) {
			throw new IOException(e);
		} finally {
			in.close();
		}
	}
}
