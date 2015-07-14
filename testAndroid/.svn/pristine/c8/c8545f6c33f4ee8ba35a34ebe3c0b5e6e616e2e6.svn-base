package com.gorillalogic.monkeytalk.java;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Helper to log output to {@code stdout}.
 */
public class Logger {
	private static PrintStream OUT;

	static {
		// System.out isn't UTF-8 by default, make it so!
		try {
			OUT = new PrintStream(System.out, true, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			OUT = null;
		}
		System.setOut(OUT);
	}

	/** Instantiate the logger targeting {@code stdout}. */
	public Logger() {
	}

	/** Log the given message, without adding a line terminator. */
	public void print(Object msg) {
		System.out.print(msg);
	}

	/** Log the given message, then terminate the line. */
	public void println(Object msg) {
		System.out.println(msg);
	}
}
