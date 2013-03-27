package com.dvlcube.openkore.src;

import static com.dvlcube.openkore.adaptations.Perl.close;
import static com.dvlcube.openkore.adaptations.Perl.defined;
import static com.dvlcube.openkore.adaptations.Perl.eof;
import static com.dvlcube.openkore.adaptations.Perl.mkdir;
import static com.dvlcube.openkore.adaptations.Perl.open;
import static com.dvlcube.openkore.adaptations.Perl.print;
import static com.dvlcube.openkore.adaptations.Perl.push;
import static com.dvlcube.openkore.adaptations.Perl.split;

import com.dvlcube.openkore.adaptations.Options;
import com.dvlcube.openkore.adaptations.Perl.$;
import com.dvlcube.openkore.adaptations.PerlFile;
import com.dvlcube.openkore.adaptations.PerlList;
import com.dvlcube.openkore.adaptations.PerlMap;
import com.dvlcube.openkore.adaptations.PerlString;

public class Settings {
	public enum FileType {
		CONTROL_FILE_TYPE, TABLE_FILE_TYPE;
		public String $ = name();
	}

	// ////////////////////////////////////////////////////////////////////
	// //// CATEGORY: Constants
	// ////////////////////////////////////////////////////////////////////

	// //
	// String Settings::NAME
	//
	// The name of this program, usually "OpenKore".

	// //
	// String Settings::VERSION
	//
	// The version number of this program.

	public static String $interface;
	public static String chat_log_file;
	public static String config_file;
	// Data file folders.
	public static PerlList<String> controlFolders;
	public static String fields_folder;

	// The registered data files.
	public static String files;
	public static String item_log_file;
	public static String items_control_file;
	public static String lockdown;
	public static String logs_folder;
	public static String maps_folder;

	public static String mon_control_file;
	public static String monster_log_file;
	// Translation Comment: Strings for the name and version of the application
	public static String NAME = "OpenKore";

	public static String no_connect;
	public static PerlList<String> options;
	public static String pathDelimiter = "getPathDelimiter(/*return : for linux and ; for windows*/)";
	public static PerlList<String> pluginsFolders;
	public static String recvpackets_name;

	public static String shop_file;
	public static String shop_log_file;
	public static String storage_log_file;
	// System configuration.
	public static PerlMap sys;
	public static String sys_file;
	public static PerlList<String> tablesFolders;

	public static String VERSION = "what-will-become-2.1";
	// Translation Comment: Version String
	public static String versionText = "*** NAME ${VERSION} ( r" + ("?") + " ) - "
			+ /* T */("Custom Ragnarok Online client") + " ***\n***   WEBSITE   ***\n";
	// Translation Comment: Version String
	// public static String SVN = T(" (SVN Version) ");
	public static String WEBSITE = "http://www.openkore.com/";

	public static String welcomeText = Translation.TF("Welcome to %s.", NAME);

	private static void _assertNameIsBasename(String baseName) {
		PerlString file = $.s(PerlFile.Spec.splitpath(baseName)[2]);
		if (file.ne(baseName)) {
			throw new IllegalArgumentException("Name must be a valid file base name.");
		}
	}

	private static String _findFileFromFolders(String name, String... folders) {
		_assertNameIsBasename(name);
		for (String dir : folders) {
			String filename = PerlFile.Spec.catfile(dir, name);
			if ($.f(filename)) {
				return filename;
			}
		}
		return null;
	}

	private static void _processSysConfig(int wMode) {
		boolean writeMode = $.b(wMode);
		PerlFile f = new PerlFile();
		PerlList<String> lines = new PerlList<>();
		PerlMap keysNotWritten = new PerlMap();
		String sysFile = getSysFilename();
		if (!defined(sysFile) || !open(f, "<:utf8", sysFile)) {
			return;
		}

		if (writeMode) {
			for (String key : sys.keySet()) {
				keysNotWritten.put(key, 1);
			}
		}

		while (!eof(f)) {
			PerlString line, key = $.s(), val = $.s();
			line = f.line;
			line.s("[\r\n]");

			if (line.eq("") || line._("#.*")) {
				if (writeMode) {
					push(lines, line);
				} else {
					continue;
				}
			}

			new $(key, val).split(" ", line, 2);
			if (writeMode) {
				if (sys.containsKey(key)) {
					push(lines, key, " ", sys.get(key));
					keysNotWritten.remove(key);
				}
			} else {
				sys.put(key, val);
			}
		}
		close(f);

		if (writeMode && open(f, ">:utf8", sysFile)) {
			for (String line : lines) {
				print(f, line, "\n");
			}
			for (String key : keysNotWritten.keySet()) {
				print(f, key, " ", sys.get(key));
			}
			close(f);
		}
	}

	/**
	 * String Settings::getControlFilename(String name) name: A valid base file
	 * name. <br>
	 * Returns: A valid filename, or undef if not found. <br>
	 * Ensures: if defined(result): -f result <br>
	 * <br>
	 * Get a control file by its name. This file will be looked up <br>
	 * in all possible locations, as specified by earlier calls // to
	 * Settings::setControlFolders().
	 * 
	 * @param baseName
	 * @return
	 * @since 12/01/2013
	 * @author Ulisses Lima
	 */
	private static String getControlFilename(String baseName) {
		return _findFileFromFolders(baseName, controlFolders.arr());
	}

	// //
	// int Settings::getSVNRevision()
	//
	// Return OpenKore's SVN revision number, or undef if that information
	// cannot be retrieved.
	public static int getSVNRevision() {
		// my f;
		// if (open(f, "<", "RealBin/.svn/entries")) {
		// my revision;
		// eval {
		// die unless <f> =~ /^\d+$/; // We only support the non-XML format
		// die unless <f> eq "\n"; // Empty string for current directory.
		// die unless <f> eq "dir\n"; // We expect a directory entry.
		// revision = <f>;
		// revision =~ s/[\r\n]//g;
		// undef revision unless revision =~ /^\d+$/;
		// };
		// close(f);
		// return revision;
		// } else {
		return 0;
		// }
	}

	private static String getSysFilename() {
		if (defined(sys_file)) {
			return sys_file;
		} else {
			return getControlFilename("sys.txt");
		}
	}

	public static String getUsageText() {
		return "usage: TODO";
	}

	public static void loadSysConfig() {
		_processSysConfig(0);
	}

	// //
	// int Settings::parseArguments()
	// Returns: 1 on success, 0 if a 'usage' text should be displayed.
	//
	// Parse command line arguments. Various variables within the Settings
	// module will be filled with values.
	//
	// This function will also attempt to create necessary folders. If
	// one of the folders cannot be created, then an IOException is thrown,
	// although the variables are already filled.
	//
	// If the arguments are not correct, then an ArgumentException is thrown.
	public static boolean parseArguments(String... args) {
		PerlMap options = Options.parse(args);

		fields_folder = options.get("fields=s").s();
		logs_folder = options.get("logs=s").s();
		maps_folder = options.get("maps=s").s();
		config_file = options.get("config=s").s();
		mon_control_file = options.get("mon_control=s").s();
		items_control_file = options.get("items_control=s").s();
		shop_file = options.get("shop=s").s();
		chat_log_file = options.get("chat-log=s").s();
		storage_log_file = options.get("storage-log=s").s();
		sys_file = options.get("sys=s").s();
		$interface = options.get("interface=s").s();
		lockdown = options.get("lockdown").s();
		no_connect = options.get("no-connect").s();

		if (options.has("control")) {
			setControlFolders(split(pathDelimiter, options.get("control")));
		} else {
			setControlFolders("control");
		}
		if (options.has("tables")) {
			setTablesFolders(split(pathDelimiter, options.get("tables")));
		} else {
			setTablesFolders("tables");
		}
		if (options.has("plugins")) {
			setPluginsFolders(split(pathDelimiter, options.get("plugins")));
		} else {
			setPluginsFolders("plugins");
		}

		fields_folder = !defined(fields_folder) ? "fields" : fields_folder;
		logs_folder = !defined(logs_folder) ? "logs" : logs_folder;
		maps_folder = !defined(maps_folder) ? "map" : maps_folder;
		chat_log_file = PerlFile.Spec.catfile(logs_folder, "chat.txt");
		storage_log_file = PerlFile.Spec.catfile(logs_folder, "storage.txt");
		shop_log_file = PerlFile.Spec.catfile(logs_folder, "shop_log.txt");
		monster_log_file = PerlFile.Spec.catfile(logs_folder, "monster_log.txt");
		item_log_file = PerlFile.Spec.catfile(logs_folder, "item_log.txt");
		if (!defined($interface)) {
			// if (ENV{OPENKORE_DEFAULT_INTERFACE} &&
			// ENV{OPENKORE_DEFAULT_INTERFACE} ne "") {
			// interface = ENV{OPENKORE_DEFAULT_INTERFACE};
			// } else {
			// interface = "Console"
			// }
			$interface = "Console";
		}

		return options.has("help") ? false : //
				mkdir(logs_folder);
	}

	private static void setControlFolders(String... strings) {
		controlFolders = new PerlList<String>(strings);
	}

	private static void setPluginsFolders(String... strings) {
		pluginsFolders = new PerlList<String>(strings);
	}

	private static void setTablesFolders(String... strings) {
		tablesFolders = new PerlList<String>(strings);
	}
}
