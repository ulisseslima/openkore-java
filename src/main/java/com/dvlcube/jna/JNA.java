package com.dvlcube.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class JNA {

	public interface CLibrary extends Library {
		CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "kernel32" : "c"), CLibrary.class);

		boolean SetConsoleTitleA(String title);
	}

	public static void setConsoleTitle(String title) {
		CLibrary.INSTANCE.SetConsoleTitleA(title);
	}
}