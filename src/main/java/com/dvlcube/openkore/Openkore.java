package com.dvlcube.openkore;

import static com.dvlcube.openkore.adaptations.Perl.die;
import static com.dvlcube.openkore.adaptations.Perl.exit;
import static com.dvlcube.openkore.adaptations.Perl.print;
import static com.dvlcube.openkore.src.Settings.sys;

import com.dvlcube.openkore.src.Benchmark;
import com.dvlcube.openkore.src.Interface;
import com.dvlcube.openkore.src.Log;
import com.dvlcube.openkore.src.Plugins;
import com.dvlcube.openkore.src.Settings;
import com.dvlcube.openkore.src.Translation;
import com.dvlcube.openkore.src.interfaces.console.Win32;

public class Openkore {
	public static final Interface $interface = new Win32();
	public static boolean DEBUG = true;

	public static int __start(String[] args) throws Exception {
		parseArguments(args);
		Settings.loadSysConfig();
		Translation.initDefault(null, sys.get("locale"));
		$interface.title(Settings.NAME);
		selfCheck();

		Benchmark.begin("Real Time");
		$interface.mainLoop();
		Benchmark.end("Real time");

		shutdown();
		return 0;
	}

	public static void main(String[] args) throws Exception {
		__start(args);
	}

	/**
	 * Parse command-line arguments.
	 * 
	 * @param args
	 * @throws Exception
	 * @since 12/01/2013
	 * @author Ulisses Lima
	 */
	public static void parseArguments(String[] args) throws Exception {
		try {
			if (!Settings.parseArguments(args)) {
				print(Settings.getUsageText());
				exit(1);
			}
		} catch (IllegalArgumentException e) {
			print(Settings.getUsageText());
			exit(1);
		} catch (Exception e) {
			print(e);
			die(e);
		}
	}

	private static void selfCheck() {

	}

	private static void shutdown() {
		Plugins.unloadAll();
		// Translation Comment: Kore's exit message
		Log.message(Translation.T("Bye!\n"));
		Log.message(Settings.versionText);

		// save debug results
	}
}
