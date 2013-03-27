package com.dvlcube.openkore.adaptations;

import static com.dvlcube.openkore.adaptations.Perl.defined;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class PerlFile {
	public static class Mode {
		public static byte read = 1;
		public static byte write = 2;
		public boolean append;
		private byte permissions = 0;

		public Mode(String mode) {
			if (mode.contains("<")) {
				permissions |= read;
			}
			if (mode.contains(">")) {
				permissions |= write;
			}
			if (mode.contains(">>")) {
				append = true;
			}
		}

		public boolean canRead() {
			return (permissions & read) == read;
		}

		public boolean canWrite() {
			return (permissions & write) == write;
		}

		public void grant(byte permission) {
			permissions |= permission;
		}

		public void revoke(byte permission) {
			permissions &= (byte) ~permission;
		}
	}

	public static class Spec {
		public static String catdir(String... strings) {
			File file = catpath(strings);
			// if (!file.isDirectory())
			// throw new IllegalArgumentException("Not a directory: " + file);
			return file.getAbsolutePath();
		}

		public static String catfile(String... strings) {
			File file = catpath(strings);
			// if (!file.isFile())
			// throw new IllegalArgumentException("Not a file: " + file);
			return file.getAbsolutePath();
		}

		public static File catpath(String... strings) {
			StringBuilder builder = new StringBuilder();
			for (String string : strings) {
				builder.append("/").append(string);
			}
			File file = new File(builder.toString().replaceFirst("/", ""));
			return file;
		}

		public static String[] splitpath(String path) {
			return splitpath(path, false);
		}

		public static String[] splitpath(String path, boolean noFile) {
			String[] fileName = new String[3];

			if (path.contains(":")) {
				String volume = path.split(":")[0];
				fileName[0] = volume + ":"; // volume
				path = path.split(":")[1];
			} else {
				fileName[0] = "";
			}

			File file = new File(path);
			String dir = file.getParent();
			fileName[1] = defined(dir) ? dir : ""; // directory
			fileName[2] = file.getName(); // file

			if (noFile) {
				return new String[] { fileName[0], fileName[1], "" };
			} else {
				return new String[] { fileName[0], fileName[1], fileName[2] };
			}
		}
	}

	public PerlString line;

	public Mode mode;
	BufferedReader reader;
	PrintWriter writer;

	public void echo(String string) {
		print(string + "\n");
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void print(String string) {
		Writer w = getWriter();
		if (w instanceof PrintWriter) {
			((PrintWriter) w).print(string);
		} else if (w instanceof BufferedWriter) {
			try {
				w.write(string);
			} catch (IOException e) {
				Perl.print(e);
			}
		} else {
			throw new UnsupportedOperationException(
					"This writer cannot write: " + w);
		}
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
}
