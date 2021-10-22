package com.unascribed.ears.common.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.unascribed.ears.common.EarsVersion;

public class EarsLog {
	
	public static final boolean DEBUG = EarsLogImpl.checkDebug();
	private static final Set<String> ONLY_DEBUG = EarsLogImpl.checkOnlyDebug();
	
	private static final Pattern BRACES_PATTERN = Pattern.compile("{}", Pattern.LITERAL);
	private static final long START = System.nanoTime();
	private static final long NANOS_TO_SECONDS = 1000000000;
	private static final long NANOS_TO_MILLIS = 1000000;
	
	static {
		if (DEBUG) {
			if (EarsVersion.PLATFORM != null) {
				debug("Common", "Hello, World! Ears Common v{}, {} Platform v{}", EarsVersion.COMMON, EarsVersion.PLATFORM_KIND, EarsVersion.PLATFORM);
			} else {
				debug("Common", "Hello, World! Ears Common v{}", EarsVersion.COMMON);
			}
			if (ONLY_DEBUG != null) {
				debug("Common", "Debugging is enabled with allowed tags {}", ONLY_DEBUG);
			} else {
				debug("Common", "Debugging is enabled with all tags");
			}
		}
	}

	/**
	 * @return {@code true} if debugging and the given debug tag are enabled
	 */
	@SuppressWarnings("unused")
	public static boolean shouldLog(String tag) {
		return DEBUG && (ONLY_DEBUG == null || ONLY_DEBUG.contains(tag));
	}
	
	/**
	 * Print a log message to the Ears log file (or stdout, if configured).
	 * <p>
	 * Prefer one of the many specializations of this method that don't allocate an array; Ears
	 * tends to inject into hot paths in the Minecraft renderer, so it's good to avoid the array
	 * allocation if debugging is off.
	 */
	public static void debugva(String tag, String fmt, Object... arg) {
		if (shouldLog(tag)) {
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
			if (arg != null && arg.length > 0 && arg[arg.length-1] instanceof Throwable) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				((Throwable)arg[arg.length-1]).printStackTrace(pw);
				pw.flush();
				buf.insert(0, sw.toString()+"\r\n");
			}
			EarsLogImpl.log(EarsLogImpl.buildMsg(secs, millis, tag, buf.toString()));
		}
	}
	
	public static void debug(String tag, String fmt) {
		if (shouldLog(tag)) {
			debugva(tag, fmt);
		}
	}

	public static void debug(String tag, String fmt, Object arg1) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(String tag, String fmt, int arg1) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, Object arg2) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, float arg2) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, Object arg2, float arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, Object arg2, Object arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, float arg1, float arg2, float arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, int arg1, int arg2, Object arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(String tag, String fmt, float arg1, float arg2, float arg3, float arg4) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3, float arg4) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, Object arg2, Object arg3, Object arg4) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3, float arg4, float arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, int arg2, int arg3, int arg4, int arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(String tag, String fmt, int arg1, int arg2, int arg3, int arg4, boolean arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, int arg2, int arg3, int arg4, int arg5, boolean arg6) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}
	
	public static void debug(String tag, String fmt, Object arg1, Object arg2, int arg3, int arg4, int arg5, int arg6) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}

	public static void debug(String tag, String fmt, int arg1, int arg2, int arg3, int arg4, Object arg5, Object arg6) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}
	
	public static void debug(String tag, String fmt, int arg1, int arg2, int arg3, int arg4, Object arg5, Object arg6, Object arg7) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
	}

	public static void debug(String tag, String fmt, Object arg1, Object arg2, int arg3, Object arg4, float arg5, float arg6, float arg7, float arg8, float arg9, float arg10) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
	}

}
