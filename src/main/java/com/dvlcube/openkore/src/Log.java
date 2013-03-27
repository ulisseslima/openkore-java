package com.dvlcube.openkore.src;

public class Log {
	public static void debug(String message) {
		debug(message, "default-domain", 1);
	}

	/**
	 * Log::debug(message, [domain], [level]) <br>
	 * Requires: $message must be encoded in UTF-8. <br>
	 * <br>
	 * Prints a debugging message. See the description for Log.pm for more
	 * details about the parameters.
	 * 
	 * @since 23/01/2013
	 * @author Ulisses Lima
	 */
	public static void debug(String message, String domain, int level) {
		System.out.println("#" + level + "#" + domain + "#");
		System.out.println(message);
	}

	public static void message(Object message) {
		System.out.println(message);
	}

}
