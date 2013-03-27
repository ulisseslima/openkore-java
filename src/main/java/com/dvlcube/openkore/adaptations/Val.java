package com.dvlcube.openkore.adaptations;

public class Val<T> {
	public T v;

	public Val(T value) {
		this.v = value;
	}

	public boolean b() {
		return (Boolean) v;
	}

	public boolean defined() {
		if (v == null) {
			return false;
		}

		if (v instanceof String) {
			if (s().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public Integer i() {
		return (Integer) v;
	}

	public Long n() {
		return (Long) v;
	}

	public String s() {
		return (String) v;
	}
}
