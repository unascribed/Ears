package com.unascribed.ears.common.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

class EarsLogImpl {

	private static final boolean DEBUG_TO_STDOUT = Boolean.getBoolean("ears.debug.stdout");
	
	static boolean checkDebug() {
		return Boolean.getBoolean("ears.debug");
	}
	
	static Set<String> checkOnlyDebug() {
		String s = System.getProperty("ears.debug.only");
		if (s != null) {
			Set<String> set = new HashSet<String>();
			for (String en : s.split(",")) {
				set.add(en);
			}
			return set;
		}
		return null;
	}
	
	static String buildMsg(int secs, int millis, String tag, String msg) {
		return String.format("[T+%03d.%03d] (%s): %s", secs, millis, tag, msg);
	}
	
	private static final PrintStream debugStream;
	
	static {
		try {
			debugStream = new PrintStream(new File("ears-debug.log"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	static void log(String msg) {
		debugStream.println(msg);
		if (DEBUG_TO_STDOUT) {
			System.out.println("[Ears] "+msg);
		}
	}
	
}
