package com.dvlcube.openkore.src;

import static com.dvlcube.openkore.adaptations.Perl.defined;
import static com.dvlcube.openkore.adaptations.Perl.length;
import static com.dvlcube.openkore.adaptations.Perl.sprintf;
import static com.dvlcube.openkore.adaptations.Perl.usleep;
import static com.dvlcube.openkore.src.Globals.$quit;
import static com.dvlcube.openkore.src.Globals.config;
import static com.dvlcube.openkore.src.Translation.T;
import static com.dvlcube.openkore.src.Translation.TF;

import com.dvlcube.openkore.adaptations.Perl.$;
import com.dvlcube.openkore.adaptations.PerlList;
import com.dvlcube.openkore.adaptations.PerlMap;
import com.dvlcube.openkore.adaptations.PerlString;

public abstract class Interface {

	/**
	 * void $interface->beep() <br>
	 * <br>
	 * Emit a beep on the available audio device.
	 * 
	 * @since 17/01/2013
	 * @author Ulisses Lima
	 */
	public abstract void beep();

	/**
	 * void $interface->errorDialog(String message, [boolean fatal = true]) <br>
	 * message: The error message to display. <br>
	 * fatal: Indicate that this is a fatal error (meaning that the application
	 * will <br>
	 * exit after this dialog is closed). If set, the console interfaces <br>
	 * will warn the user that the app is about to exit. <br>
	 * Requires: defined($message) <br>
	 * <br>
	 * Display an error dialog. This function blocks until the user has closed
	 * the <br>
	 * dialog. <br>
	 * <br>
	 * Consider using Log::error() if your message is not a fatal error, because <br>
	 * Log::error() does not require any user interaction.
	 * 
	 * @param message
	 * @since 17/01/2013
	 * @author Ulisses Lima
	 */
	public void errorDialog(String message) {
		errorDialog(message, true);
	}

	/**
	 * void $interface->errorDialog(String message, [boolean fatal = true]) <br>
	 * message: The error message to display. <br>
	 * fatal: Indicate that this is a fatal error (meaning that the application
	 * will <br>
	 * exit after this dialog is closed). If set, the console interfaces <br>
	 * will warn the user that the app is about to exit. <br>
	 * Requires: defined($message) <br>
	 * <br>
	 * Display an error dialog. This function blocks until the user has closed
	 * the <br>
	 * dialog. <br>
	 * <br>
	 * Consider using Log::error() if your message is not a fatal error, because <br>
	 * Log::error() does not require any user interaction.
	 * 
	 * @param message
	 * @param isFatal
	 * @since 17/01/2013
	 * @author Ulisses Lima
	 */
	public void errorDialog(String message, Boolean isFatal) {
		boolean fatal = defined(isFatal) ? isFatal : true;

		writeOutput("error", message + "\n", "error");
		if (fatal) {
			writeOutput("message", Translation.T("Press ENTER to exit this program.\n"), "console");
		} else {
			writeOutput("message", Translation.T("Press ENTER to continue...\n"), "console");
		}
		getInput(-1);
	}

	/**
	 * String $interface->getInput(float timeout) timeout: Number of second to
	 * wait until keyboard data is available. Negative numbers will wait
	 * forever, 0 will not wait at all. Returns: The keyboard data (excluding
	 * newline), or undef if there's no keyboard data available.
	 * 
	 * Reads keyboard data.
	 * 
	 * @param timeout
	 * @return
	 * @since 13/01/2013
	 * @author Ulisses Lima
	 */
	public abstract PerlString getInput(float timeout);

	public abstract String getName();

	/**
	 * void $interface->iterate()
	 * 
	 * Process messages in the user interface message queue. In other words:
	 * make sure the user interface updates itself. (redraw controls when
	 * necessary, etc.)
	 * 
	 * @since 13/01/2013
	 * @author Ulisses Lima
	 */
	public abstract void iterate();

	/**
	 * void $interface->mainLoop() <br>
	 * <br>
	 * Enter the interface's main loop.
	 * 
	 * @since 17/01/2013
	 * @author Ulisses Lima
	 */
	public void mainLoop() {
		while (!$quit) {
			usleep(config.get("sleepTime"), 10000);
			iterate();
			Functions.mainLoop();
		}
	}

	/**
	 * String $interface->query(String message, options...) <br>
	 * message: A message to display when asking for input. <br>
	 * Returns: The user input, or undef if the user cancelled. <br>
	 * Requires: defined($message) <br>
	 * <br>
	 * Ask the user to enter a one-line input text. <br>
	 * The following options are allowed: <br>
	 * `l <br>
	 * - cancelable - Whether the user is allowed to enter nothing. If this is
	 * set to true, <br>
	 * then the user will be asked the same thing over and over until he <br>
	 * replies with a non-empty input. The default is true. <br>
	 * - title - A title to display in the query dialog. The default is "Query". <br>
	 * - isPassword - Whether this query is a password query. The default is
	 * false. <br>
	 * `l`
	 * 
	 * @param $message
	 * @param $args
	 * @return
	 * @since 17/01/2013
	 * @author Ulisses Lima
	 */
	public PerlString query(PerlString $message, PerlMap $args) {
		$args.putUnlessDefined("title", "Query");
		$args.putUnlessDefined("cancelable", true);

		String $title = "------------ " + $args.get("title") + " ------------";
		String $footer = $.s("-").x($title.length());
		$message.s("\n+$");
		$message = $.s($title, "\n", $message, "\n", $footer, "\n");

		while (true) {
			writeOutput("message", $message.value, "input");
			writeOutput("message", T("Enter your answer: "), "input");
			int $mode = $args.is("isPassword") ? -9 : -1;
			PerlString $result = getInput($mode);
			if ($result.isBlank()) {
				if ($args.is("cancelable")) {
					return null;
				}
			} else {
				return $result;
			}
		}
	}

	/**
	 * int $interface->showMenu(String message, Array<String>* choices,
	 * options...) <br>
	 * message: The message to display while asking the user to make a choice. <br>
	 * choices: The possible choices. <br>
	 * Returns: The index of the chosen item, or -1 if the user cancelled. <br>
	 * Requires: <br>
	 * defined($message) <br>
	 * defined($choices) <br>
	 * for all $k in @{$choices}: defined($k) <br>
	 * Ensures: -1 <= result < @{$choices} <br>
	 * <br>
	 * Ask the user to choose an item from a menu of choices. <br>
	 * <br>
	 * The following options are allowed: <br>
	 * `l <br>
	 * - title - The title to display when presenting the choices to the user. <br>
	 * The default is 'Menu'. <br>
	 * - cancelable - Whether the user is allowed to not choose. <br>
	 * The default is true. <br>
	 * `l`
	 * 
	 * @param $message
	 * @param choices
	 * @param args
	 * @return
	 * @since 19/01/2013
	 * @author Ulisses Lima
	 */
	public int showMenu(String $message, PerlList<String> choices, PerlMap $args) {
		$args.putUnlessDefined("title", "Menu");
		$args.putUnlessDefined("cancelable", true);

		// Create a nicely formatter choice list.
		int $maxNumberLength = length(choices) + 1;
		String $format = "%-" + $maxNumberLength + "s   %-s\n";
		String $output = sprintf($format, "#", T("Choice")).value;
		int $i = 0;
		for (String $item : choices) {
			$output += sprintf($format, $i, $item);
			$i++;
		}

		$message = $output + "------------------------\n" + $message;

		while (true) {
			PerlString $choice = query($.s($message), //
					$args.sub("cancelable", //
							"title"));
			if (!defined($choice)) {
				return -1;
			} else if ($choice.m("^\\d+$") || $choice.i() < 0 || $choice.i() >= choices.size()) {
				writeOutput("error", TF("'%s' is not a valid choice number.\n", $choice), "default");
			} else {
				return $choice.i();
			}
		}
	}

	/**
	 * String $interface->title([String title]) <br>
	 * <br>
	 * If $title is given, set the interface's window's title to $title. <br>
	 * If not given, returns the current window title.
	 * 
	 * @param title
	 * @return
	 * @since 17/01/2013
	 * @author Ulisses Lima
	 */
	public abstract String title(String title);

	public abstract void writeOutput(String type, String message);

	/**
	 * void $interface->writeOutput(String type, String message, String domain) <br>
	 * Requires: defined($type) && defined($message) && defined($domain) <br>
	 * <br>
	 * Writes a message to the interface's console. <br>
	 * This method should not be used directly, use Log::message() instead.
	 * 
	 * @param type
	 * @param message
	 * @param domain
	 * @since 17/01/2013
	 * @author Ulisses Lima
	 */
	public abstract void writeOutput(String type, String message, String domain);
}
