package com.unascribed.ears.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.teavm.interop.Remove;
import org.teavm.jso.JSBody;

public class EarsLog {
	
	private static final EarsLogger IMPL = isJS() ? defaultImplJs() : defaultImpl();
	public static final boolean DEBUG = checkDebug();
	private static final String lineSep = isJS() ? "" : "\r\n";
	
	private static final Pattern BRACES_PATTERN = Pattern.compile("{}", Pattern.LITERAL);
	private static final long START = System.nanoTime();
	private static final long NANOS_TO_SECONDS = 1000000000;
	private static final long NANOS_TO_MILLIS = 1000000;
	
	static {
		if (DEBUG) {
			debug("Common", "Hello, World!");
		}
	}

	public static void debugva(String tag, String fmt, Object... arg) {
		if (DEBUG) {
			StringBuffer buf = new StringBuffer();
			Matcher m = BRACES_PATTERN.matcher(fmt);
			int i = 0;
			while (m.find()) {
				m.appendReplacement(buf, arg != null && i < arg.length ? String.valueOf(arg[i]).replace("\\", "\\\\").replace("$", "\\$") : "{}");
				i++;
			}
			m.appendTail(buf);
			long diff = System.nanoTime()-START;
			int secs = (int)(diff/NANOS_TO_SECONDS);
			int millis = (int)((diff/NANOS_TO_MILLIS)%1000);
			IMPL.log(buildMsg(secs, millis, tag, buf.toString()));
		}
	}
	
	// String.format pulls in a lot of stuff we do not need, so reimplement it for JS target
	// this saves 200K without minification and 100K after minification (!)
	@JSBody(params={"secs", "millis", "tag", "msg"},
			script="return \"[T+\"+(\"000\"+secs).slice(-3)+\".\"+(\"000\"+millis).slice(-3)+\"] (\"+tag+\"): \"+msg")
	private static String buildMsg(int secs, int millis, String tag, String msg) {
		return String.format("[T+%03d.%03d] (%s): %s"+lineSep, secs, millis, tag, msg);
	}

	@JSBody(script="return window.EarsDebug")
	private static boolean checkDebug() {
		return Boolean.getBoolean("com.unascribed.ears.Debug") || Boolean.getBoolean("ears.debug");
	}

	@JSBody(script="return true")
	private static boolean isJS() {
		return false;
	}
	
	@Remove
	private static EarsLogger defaultImpl() {
		return new FileEarsLogger();
	}
	
	@SuppressWarnings("unused")
	private static EarsLogger defaultImplJs() {
		return new ConsoleEarsLogger();
	}

	// various purpose-built overloads prevent boxing and array allocation when debugging is off

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
	
	public static void debug(String tag, String fmt, Object arg1, float arg2) {
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
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3, float arg4) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, Object arg2, Object arg3, Object arg4) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3, float arg4, float arg5) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, int arg2, int arg3, int arg4, int arg5) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(String tag, String fmt, int arg1, int arg2, int arg3, int arg4, boolean arg5) {
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
	
	public static void debug(String tag, String fmt, int arg1, int arg2, int arg3, int arg4, Object arg5, Object arg6, Object arg7) {
		if (DEBUG) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
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
