package com.dvlcube.openkore.src.interfaces.console;

import static com.dvlcube.openkore.adaptations.Perl.defined;

import java.io.Console;

import com.dvlcube.jna.JNA;
import com.dvlcube.openkore.adaptations.Perl.$;
import com.dvlcube.openkore.adaptations.PerlList;
import com.dvlcube.openkore.adaptations.PerlString;
import com.dvlcube.openkore.src.Interface;

public class Win32 extends Interface {
	private static Console console = System.console();
	private PerlString currentTitle = $.s();
	private PerlList input_lines = new PerlList<>();
	private PerlList input_list = new PerlList<>();
	private int input_offset = 0;
	private PerlString input_part = $.s();
	private int last_line_end = 1;

	@Override
	public void beep() {
		System.out.println("\n");
		System.out.println("@##########@@@@#########@");
		System.out.println("@##########BEEP#########@");
		System.out.println("@##########@@@@#########@");
		System.out.println("\n");
	}

	@Override
	public PerlString getInput(float timeout) {
		return $.s(console.readLine());
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	public void iterate() {
		System.out.println("i'm iterating");
	}

	@Override
	public String title(String title) {
		if (defined(title)) {
			if (!defined(currentTitle) || currentTitle.ne(title)) {
				JNA.setConsoleTitle(title);
				currentTitle = $.s(title);
				System.out.println("\n");
				System.out.println("-------------------------");
				System.out.println(currentTitle);
				System.out.println("-------------------------");
				System.out.println("\n");
			}
		} else {
			return currentTitle.value;
		}
		return null;
	}

	@Override
	public void writeOutput(String type, String message) {
		writeOutput(type, message, "defaul");
	}

	@Override
	public void writeOutput(String type, String message, String domain) {
		StringBuilder builder = new StringBuilder();
		builder.append("#" + type);
		builder.append(message);
		builder.append("--" + domain);
		System.out.println(builder.toString());
	}
}
