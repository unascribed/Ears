package com.unascribed.ears.common.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.unascribed.ears.common.EarsVersion;

public class EarsLog {
	
	public static class Tag {
		
		private static final Set<String> ALL = new HashSet<String>();
		
		/** Nonspecific common code, ignores ONLY_DEBUG */
		public static final Tag COMMON_ALWAYS = new Tag("Common");
		
		/** Nonspecific common code */
		public static final Tag COMMON = new Tag("Common");
		/** Common code related to agent patching */
		public static final Tag COMMON_AGENT = new Tag("Common:Agent");
		/** Common code related to the API */
		public static final Tag COMMON_API = new Tag("Common:Api");
		/** Common code related to feature detection */
		public static final Tag COMMON_FEATURES = new Tag("Common:Features");
		/** Common code related to mixin patching */
		public static final Tag COMMON_MIXIN = new Tag("Common:Mixin");
		/** Common code related to rendering */
		public static final Tag COMMON_RENDERER = new Tag("Common:Renderer");
		/** Dummy tag to enable debug dots in the renderer */
		public static final Tag COMMON_RENDERER_DOTS = new Tag("Common:Renderer:Dots");
		
		/** Nonspecific platform code */
		public static final Tag PLATFORM = new Tag("Platform");
		/** Platform code related to loading images */
		public static final Tag PLATFORM_LOAD = new Tag("Platform:Load");
		/** Platform code related to rendering and render hooking */
		public static final Tag PLATFORM_RENDERER = new Tag("Platform:Renderer");
		/** Platform code related to implementing the common render delegate */
		public static final Tag PLATFORM_RENDERER_DELEGATE = new Tag("Platform:Renderer:Delegate");
		/** Platform code relating to patches and injections */
		public static final Tag PLATFORM_INJECT = new Tag("Platform:Inject");
		/** Platform code relating to injected render hooks */
		public static final Tag PLATFORM_INJECT_RENDERER = new Tag("Platform:Inject:Renderer");
		
		private final String tag;
		
		private Tag(String tag) {
			this.tag = tag;
			ALL.add(tag);
		}
		
		@Override
		public String toString() {
			return tag;
		}
	}
	
	public static final boolean DEBUG = EarsLogImpl.checkDebug();
	private static final Set<String> ONLY_DEBUG = EarsLogImpl.checkOnlyDebug();
	
	private static final Pattern BRACES_PATTERN = Pattern.compile("{}", Pattern.LITERAL);
	private static final long START = System.nanoTime();
	private static final long NANOS_TO_SECONDS = 1000000000;
	private static final long NANOS_TO_MILLIS = 1000000;
	
	static {
		if (DEBUG) {
			if (EarsVersion.PLATFORM != null) {
				debug(Tag.COMMON_ALWAYS, "Hello, World! Ears Common v{}, {} Platform v{}", EarsVersion.COMMON, EarsVersion.PLATFORM_KIND, EarsVersion.PLATFORM);
			} else {
				debug(Tag.COMMON_ALWAYS, "Hello, World! Ears Common v{}", EarsVersion.COMMON);
			}
			if (ONLY_DEBUG != null) {
				debug(Tag.COMMON_ALWAYS, "Debugging is enabled with allowed tags {}", ONLY_DEBUG);
				for (String s : ONLY_DEBUG) {
					if (!Tag.ALL.contains(s)) {
						debug(Tag.COMMON_ALWAYS, "Tag {} does not exist", s);
					}
				}
			} else {
				debug(Tag.COMMON_ALWAYS, "Debugging is enabled with all tags");
			}
		}
	}

	/**
	 * @return {@code true} if debugging and the given debug tag are enabled
	 */
	@SuppressWarnings("unused")
	public static boolean shouldLog(Tag tag) {
		return DEBUG && (tag == Tag.COMMON_ALWAYS || ONLY_DEBUG == null || ONLY_DEBUG.contains(tag.toString()));
	}
	
	/**
	 * Print a log message to the Ears log file (or stdout, if configured).
	 * <p>
	 * Prefer one of the many specializations of this method that don't allocate an array; Ears
	 * tends to inject into hot paths in the Minecraft renderer, so it's good to avoid the array
	 * allocation if debugging is off.
	 */
	public static void debugva(Tag tag, String fmt, Object... arg) {
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
				EarsLogImpl.log(sw.toString().trim());
			}
			EarsLogImpl.log(EarsLogImpl.buildMsg(secs, millis, tag.toString(), buf.toString()));
		}
	}
	
	public static void debug(Tag tag, String fmt) {
		if (shouldLog(tag)) {
			debugva(tag, fmt);
		}
	}

	public static void debug(Tag tag, String fmt, Object arg1) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(Tag tag, String fmt, float arg1) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(Tag tag, String fmt, double arg1) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(Tag tag, String fmt, boolean arg1) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(Tag tag, String fmt, int arg1) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1);
		}
	}

	public static void debug(Tag tag, String fmt, int arg1, int arg2) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2);
		}
	}

	public static void debug(Tag tag, String fmt, int arg1, Object arg2) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, Object arg2) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, float arg2) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2);
		}
	}

	public static void debug(Tag tag, String fmt, Object arg1, Object arg2, float arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(Tag tag, String fmt, int arg1, Object arg2, boolean arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, Object arg2, Object arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}
	
	public static void debug(Tag tag, String fmt, int arg1, Object arg2, Object arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, float arg2, float arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(Tag tag, String fmt, float arg1, float arg2, float arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(Tag tag, String fmt, int arg1, int arg2, Object arg3) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3);
		}
	}

	public static void debug(Tag tag, String fmt, float arg1, float arg2, float arg3, float arg4) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, float arg2, float arg3, float arg4) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, Object arg2, Object arg3, Object arg4) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4);
		}
	}

	public static void debug(Tag tag, String fmt, Object arg1, float arg2, float arg3, float arg4, float arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, int arg2, int arg3, int arg4, int arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(Tag tag, String fmt, int arg1, int arg2, int arg3, int arg4, boolean arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, int arg2, int arg3, int arg4, int arg5, boolean arg6) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}
	
	public static void debug(Tag tag, String fmt, Object arg1, Object arg2, int arg3, int arg4, int arg5, int arg6) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}

	public static void debug(Tag tag, String fmt, int arg1, int arg2, int arg3, int arg4, Object arg5, Object arg6) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6);
		}
	}
	
	public static void debug(Tag tag, String fmt, int arg1, int arg2, int arg3, int arg4, Object arg5, Object arg6, Object arg7) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
	}

	public static void debug(Tag tag, String fmt, Object arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}
	}

	public static void debug(Tag tag, String fmt, Object arg1, Object arg2, int arg3, Object arg4, float arg5, float arg6, float arg7, float arg8, float arg9, float arg10) {
		if (shouldLog(tag)) {
			debugva(tag, fmt, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
		}
	}

}
