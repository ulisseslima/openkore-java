package com.dvlcube.openkore.src;

import static com.dvlcube.openkore.Openkore.$interface;
import static com.dvlcube.openkore.Openkore.DEBUG;
import static com.dvlcube.openkore.adaptations.Perl.defined;
import static com.dvlcube.openkore.src.Log.debug;

import com.dvlcube.openkore.adaptations.PerlString;

public class Functions {
	private static volatile Integer state;

	public static final byte //
			STATE_LOAD_PLUGINS = 0, //
			STATE_LOAD_DATA_FILES = 1, //
			STATE_INIT_NETWORKING = 2, //
			STATE_INIT_PORTALS_DATABASE = 3, //
			STATE_PROMPT = 4, //
			STATE_FINAL_INIT = 5, //
			STATE_INITIALIZED = 6;

	public static void mainLoop() {
		if (DEBUG) {
			Benchmark.begin("mainLoop");
		}
		state = defined(state) ? state : STATE_LOAD_PLUGINS;

		// Parse command input
		PerlString input;
		if (defined(input = $interface.getInput(0))) {
			Misc.checkValidity();
			parseInput(input);
		}

	}

	private static void parseInput(PerlString input) {
		String printType;
		String hook, msg;
		// $printType = shift if ($net && $net->clientAlive);

		debug("Input: " + input, "parseInput", 2);
		$interface.writeOutput("console", input + "\n");

	}
}
