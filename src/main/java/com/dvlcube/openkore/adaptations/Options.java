package com.dvlcube.openkore.adaptations;

public class Options {
	private static final PerlMap options = new PerlMap();

	private Options() {

	}

	public static PerlMap parse(String[] args) {
		for (String string : args) {
			String[] option = string.split("=");
			options.put(option[0], option[1]);
		}
		return options;
	}
}
