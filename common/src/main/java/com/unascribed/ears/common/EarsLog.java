package com.unascribed.ears.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EarsLog {

	public static final boolean DEBUG = Boolean.getBoolean("com.unascribed.ears.Debug");
	private static final PrintStream debugStream;
	private static final Pattern BRACES_PATTERN = Pattern.compile("{}", Pattern.LITERAL);
	private static final long START = System.nanoTime();
	private static final long NANOS_TO_SECONDS = TimeUnit.SECONDS.toNanos(1);
	private static final long NANOS_TO_MILLIS = TimeUnit.MILLISECONDS.toNanos(1);
	
	static {
		if (DEBUG) {
			try {
				debugStream = new PrintStream(new File("ears-debug.log"));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			debug("Common", "Hello, World!");
		} else {
			debugStream = null;
		}
	}

	public static void debugva(String tag, String fmt, Object... arg) {
		if (DEBUG) {
			StringBuffer buf = new StringBuffer();
			Matcher m = BRACES_PATTERN.matcher(fmt);
			int i = 0;
			while (m.find()) {
				m.appendReplacement(buf, arg != null && i < arg.length ? String.valueOf(arg[i]) : "{}");
				i++;
			}
			m.appendTail(buf);
			long diff = System.nanoTime()-START;
			long secs = diff/NANOS_TO_SECONDS;
			long millis = (diff/NANOS_TO_MILLIS)%1000;
			debugStream.printf("[T+%03d.%03d] (%s): %s\r\n", secs, millis, tag, buf);
		}
	}

	public static void debug(String tag, String fmt) {
		if (DEBUG) {
			debugva(tag, fmt);
		}
	}

	public static void debug(String tag, String fmt, Object arg1) {
		if (DEBUG) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(String tag, String fmt, int arg1) {
		if (DEBUG) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, Object arg2) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, Object arg2, float arg3) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, float arg1, float arg2, float arg3) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, int arg1, int arg2, Object arg3) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, float arg1, float arg2, float arg3, float arg4) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, int arg2, int arg3, int arg4, int arg5) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, int arg2, int arg3, int arg4, int arg5, boolean arg6) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}

	public static void debug(String tag, String fmt, int arg1, int arg2, int arg3, int arg4, Object arg5, Object arg6) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, Object arg2, int arg3, Object arg4, float arg5, float arg6, float arg7, float arg8, float arg9, float arg10) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
	}

}