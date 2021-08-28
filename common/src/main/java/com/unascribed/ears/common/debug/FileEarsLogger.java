package com.unascribed.ears.common.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.teavm.interop.UnsupportedOn;

@UnsupportedOn("javascript")
class FileEarsLogger implements EarsLogger {

	private final PrintStream debugStream;
	
	public FileEarsLogger() {
		try {
			debugStream = new PrintStream(new File("ears-debug.log"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void log(String msg) {
		debugStream.println(msg);
	}
	
}
