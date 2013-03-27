package com.dvlcube.openkore.adaptations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import com.dvlcube.openkore.Openkore;
import com.dvlcube.openkore.adaptations.PerlFile.Mode;

public class Perl {
	public static class $ {
		public static boolean b(Object obj) {
			if (obj instanceof Integer) {
				Integer i = (Integer) obj;
				return i.equals(1) ? true : false;
			} else if (obj instanceof String) {
				return Boolean.valueOf((String) obj);
			}
			return false;
		}

		public static boolean d(String filename) {
			return new File(filename).isDirectory();
		}

		public static boolean f(String filename) {
			return new File(filename).isFile();
		}

		public static int i(String string) {
			return Integer.parseInt(string);
		}

		/**
		 * Converts a long value to a perl long value.
		 * 
		 * @param value
		 * @return a long value.
		 * @since 12/01/2013
		 * @author Ulisses Lima
		 */
		public static Val<Long> n(Long value) {
			return new Val<Long>(value);
		}

		public static PerlString s() {
			return new PerlString("");
		}

		/**
		 * Converts a String to a PerlString.
		 * 
		 * @param strings
		 * @return a PerlString
		 * @since 12/01/2013
		 * @author Ulisses Lima
		 */
		public static PerlString s(Object... strings) {
			StringBuilder builder = new StringBuilder();
			for (Object object : strings) {
				builder.append(object.toString());
			}
			return new PerlString(builder.toString());
		}

		private PerlString[] strings;

		public $(PerlString... values) {
			strings = values;
		}

		public void split(String regex, PerlString string, int limit) {
			split(regex, string.value, limit);
		}

		public void split(String regex, String string, int limit) {
			String[] splat = string.split(regex, limit);

			int i = 0;
			for (PerlString s : strings) {
				s.value = splat[i];
				i++;
			}
		}
	}

	public static Object undef = null;

	public static boolean bool(Boolean value) {
		return value == null ? false : value;
	}

	public static boolean close(PerlFile handle) {
		try {
			if (handle.mode.canRead()) {
				handle.getReader().close();
				handle.mode.revoke(Mode.read);
			}
			if (handle.mode.canWrite()) {
				handle.getWriter().flush();
				handle.getWriter().close();
				handle.mode.revoke(Mode.write);
			}
		} catch (IOException e) {
			print(e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean defined(Object object) {
		return object != null;
	}

	public static boolean defined(Object array, int i) {
		try {
			if (array.getClass().isArray()) {
				Object[] genericArr = (Object[]) array;
				return genericArr[i] != null;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			print(e);
		}
		return false;
	}

	public static boolean defined(String value) {
		try {
			return defined(new Val<String>(value));
		} catch (Exception e) {
			print(e);
			return false;
		}
	}

	public static boolean defined(Val<?> value) {
		return value.defined() ? true : false;
	}

	public static void delete(PerlMap map, PerlString key) {
		map.remove(key.value);
	}

	public static void die(Exception e) throws Exception {
		throw e;
	}

	public static boolean eof(PerlFile handle) {
		try {
			String line = handle.getReader().readLine();
			handle.line = $.s(line);
			return line == null;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
	}

	public static String eval(String expr) {
		String value = "";
		try {
			String execution = "perl -e \"" + expr + "\"";
			if (Openkore.DEBUG) {
				System.out.println(execution);
			}
			Process process = Runtime.getRuntime().exec(execution);
			int r = process.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			if (r >= 2) {
				System.out.println("OS Error: Unable to Find File or other OS error.");
			}

			while (reader.ready()) {
				String str = reader.readLine();
				value = str;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static boolean exists(PerlMap map, PerlString key) {
		return map.containsKey(key);
	}

	public static void exit(int status) {
		System.exit(status);
	}

	public static String join(String separator, Object... objects) {
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) {
			builder.append(separator).append(object.toString());
		}
		return builder.toString().replaceFirst(separator, "");
	}

	public static int length(Object o) {
		if (o instanceof String) {
			return ((String) o).length();
		} else if (o instanceof PerlString) {
			return ((PerlString) o).value.length();
		} else if (o instanceof Map) {
			return ((Map) o).size();
		} else if (o instanceof Collection) {
			return ((Collection) o).size();
		} else if (o instanceof StringBuilder) {
			return ((StringBuilder) o).length();
		}
		return 0;
	}

	public static boolean mkdir(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			return true;
		}
		return file.mkdir();
	}

	public static boolean open(PerlFile handle, String mode, String fileName) {
		Mode fileHandlerMode = new Mode(mode);
		if (mode.contains(">>")) {
			fileHandlerMode.append = true;
		}
		handle.mode = fileHandlerMode;

		if (fileHandlerMode.canRead()) {
			return openRead(handle, mode, fileName);
		}
		if (fileHandlerMode.canWrite()) {
			return openWrite(handle, mode, fileName);
		}
		throw new IllegalArgumentException("Invalid mode: " + mode);
	}

	private static boolean openRead(PerlFile handle, String mode, String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(r);
			handle.setReader(reader);
		} catch (IOException e) {
			print(e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean openWrite(PerlFile handle, String mode, String fileName) {
		try {
			PrintWriter appender = new PrintWriter(new FileWriter(fileName, handle.mode.append));
			handle.setWriter(appender);
		} catch (Exception e) {
			print(e.getMessage());
			return false;
		}
		return true;
	}

	public static void print(Exception e) {
		e.printStackTrace();
	}

	public static void print(Object e, Object... args) {
		print(e.toString(), args);
	}

	public static void print(PerlFile f, String... strings) {
		for (String string : strings) {
			f.print(string);
		}
	}

	public static void print(String string, Object... args) {
		System.out.printf(string + "\n", args);
	}

	public static void push(PerlList list, Object... items) {
		for (Object object : items) {
			list.add(object);
		}
	}

	public static String[] split(String delimiter, Val<?> value) {
		String[] values = value.s().split(delimiter);
		return values;
	}

	public static PerlList<String> splitList(String delimiter, Val<?> value) {
		PerlList<String> list = new PerlList<>();
		String[] values = split(delimiter, value);
		for (String string : values) {
			list.add(string);
		}
		return list;
	}

	public static PerlString sprintf(String format, Object... args) {
		return $.s(String.format(format, args));
	}

	public static PerlString substr(PerlString expr, int offset) {
		return substr(expr, offset, expr.value.length());
	}

	public static PerlString substr(PerlString expr, int offset, int length) {
		return substr(expr, offset, length, null);
	}

	public static PerlString substr(PerlString expr, int offset, int length, Object replacement) {
		if (defined(replacement)) {
			String target = new String(expr.value.substring(offset, offset + length));
			expr.value = expr.value.replaceFirst(target, replacement.toString());
		} else {
			expr.value = expr.value.substring(offset, offset + length);
		}
		return expr;
	}

	public static void substr(StringBuilder buffer2, int offset, Integer size, String replacement) {

	}

	public static String uc(Object o) {
		return o.toString().toUpperCase();
	}

	public static boolean unlessDefined(String value) {
		return !defined(value);
	}

	public static boolean unlessDefined(Val<?> value) {
		return !defined(value);
	}

	public static String unpack(String template, Byte[] bytes) {
		byte[] pbytes = new byte[bytes.length];
		int i = 0;
		for (Byte b : bytes) {
			pbytes[i] = b;
			i++;
		}
		return eval("print unpack '" + template + "', '" + new String(pbytes) + "'");
	}

	public static String unpack(String template, PerlList<Byte> bytes) {
		return unpack(template, bytes.array());
	}

	/**
	 * Unpacks a string into a string array following a given format.
	 */
	public static String[] unpack2(String format, String data) throws ParseException {

		ArrayList result = new ArrayList();
		int formatOffset = 0;

		int dataOffset = 0;
		int minDataOffset = 0;

		int maxDataOffset = data.length();

		StringTokenizer tokenizer = new StringTokenizer(format);

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			int tokenLen = token.length();

			// count determination
			int count = 0;

			if (tokenLen == 1) {
				count = 1;
			} else if (token.charAt(1) == '*') {
				count = -1;
			} else {
				try {
					count = new Integer(token.substring(1)).intValue();

				} catch (NumberFormatException ex) {
					throw new ParseException("Unknown count token", formatOffset);

				}
			}

			// action determination
			char action = token.charAt(0);

			switch (action) {
			case 'A':
				if (count == -1) {

					int start = (dataOffset < maxDataOffset) ? dataOffset : maxDataOffset;
					result.add(data.substring(start));

					dataOffset = maxDataOffset;
				} else {
					int start = (dataOffset < maxDataOffset) ? dataOffset : maxDataOffset;

					int end = (dataOffset + count < maxDataOffset) ? dataOffset + count : maxDataOffset;
					result.add(data.substring(start, end));

					dataOffset += count;
				}
				break;
			case 'x':
				if (count == -1) {
					dataOffset = maxDataOffset;
				} else {
					dataOffset += count;
				}
				break;
			case 'X':
				if (count == -1) {
					dataOffset = minDataOffset;
				} else {
					dataOffset -= count;
				}
				break;
			default:
				throw new ParseException("Unknown action token", formatOffset);
			}
			formatOffset += tokenLen + 1;
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	public static void usleep(long µseconds) {
		try {
			TimeUnit.MICROSECONDS.sleep(µseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void usleep(Val<?> val, long fallback) {
		usleep(val.defined() ? val.n() : fallback);
	}
}
