package com.unascribed.ears.common.debug;

import java.util.Set;

/**
 * Never used. Exists to allow the main module to compile.
 */
class EarsLogImpl {

	static boolean checkDebug() {
		throw new AssertionError("Stub!");
	}
	
	static Set<String> checkOnlyDebug() {
		throw new AssertionError("Stub!");
	}
	
	static String buildMsg(int secs, int millis, String tag, String msg) {
		throw new AssertionError("Stub!");
	}
	
	static void log(String msg) {
		throw new AssertionError("Stub!");
	}
	
}
