package com.unascribed.ears.common.debug;

import org.teavm.jso.JSBody;

class ConsoleEarsLogger implements EarsLogger {

	@Override
	public void log(String msg) {
		log0(msg);
	}

	@JSBody(params={"msg"}, script="console.debug(msg)")
	private static void log0(String msg) {
		System.out.println(msg);
	}

}
