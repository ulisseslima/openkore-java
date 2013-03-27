package com.dvlcube.openkore.adaptations;

public class PerlString {
	public String value;

	public PerlString(String str) {
		value = str;
	}

	/**
	 * @param regex
	 * @return if the value of this perl string matches the regex.
	 * @since 12/01/2013
	 * @author Ulisses Lima
	 */
	public boolean _(String regex) {
		return value.matches(regex);
	}

	public boolean eq(PerlString other) {
		return other.equals(this);
	}

	public boolean eq(String other) {
		return eq(new PerlString(other));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PerlString other = (PerlString) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	public int i() {
		Integer i = Integer.parseInt(value);
		return i;
	}

	public boolean isBlank() {
		if (value == null) {
			return true;
		}
		if (value.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @param regex
	 * @return if this.value matches the regex.
	 * @since 20/01/2013
	 * @author Ulisses Lima
	 */
	public boolean m(String regex) {
		return value.matches(regex);
	}

	public long n() {
		Long n = Long.parseLong(value);
		return n;
	}

	public boolean ne(PerlString other) {
		return !eq(other);
	}

	public boolean ne(String other) {
		return !eq(new PerlString(other));
	}

	/**
	 * @param regex
	 * @return if this.value does not match the regex.
	 * @since 20/01/2013
	 * @author Ulisses Lima
	 */
	public boolean nm(String regex) {
		return !m(regex);
	}

	/**
	 * Equivalent to =~ s/string/replacement/g<br>
	 * Replaces all characters matching <code>regex</code> with
	 * <code>replacement</code>.
	 * 
	 * @param string
	 *            String using the pattern: regex/replacement.
	 * @since 13/01/2013
	 * @author Ulisses Lima
	 */
	public void s(String string) {
		String separator = "/";
		String[] params = string.split(separator, 2);
		String regex = params[0];
		String replacement = Perl.defined(params, 1) ? params[1] : "";
		value = value.replaceAll(regex, replacement);
	}

	@Override
	public String toString() {
		return value;
	}

	public String x(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(value);
		}
		return builder.toString();
	}
}
