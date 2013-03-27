package com.dvlcube.openkore.adaptations;

import java.util.HashMap;

public class PerlMap extends HashMap<String, Val<?>> {

	private static final long serialVersionUID = 7784969630621217743L;

	public Val<?> get(String key) {
		Val<?> val = super.get(key);
		return val == null ? super.get(key.split("=")[0]) : val;
	}

	public String gets(String key) {
		return get(key).s();
	}

	public boolean has(String string) {
		Val<?> val = super.get(string);
		return val != null ? true : false;
	}

	public boolean is(String key) {
		return get(key).b();
	}

	public Val<?> put(PerlString key, Object value) {
		return put(key.value, value);
	}

	public Val<?> put(String key, Object value) {
		if (value instanceof Integer) {
			return super.put(key, new Val<Integer>((Integer) value));
		} else if (value instanceof Boolean) {
			return super.put(key, new Val<Boolean>((Boolean) value));
		} else if (value instanceof String) {
			boolean isNumber = false;
			boolean isBoolean = false;

			long longValue = 0;
			try {
				longValue = Long.parseLong((String) value);
				isNumber = true;
			} catch (NumberFormatException e) {
				isNumber = false;
			}

			Boolean booleanValue = null;
			try {
				booleanValue = Boolean.valueOf((String) value);
			} catch (Exception e) {
				booleanValue = null;
			}

			if (isNumber) {
				return new Val<Long>(longValue);

			} else if (isBoolean) {
				return new Val<Boolean>(booleanValue);
			} else {
				return new Val<String>((String) value);
			}
		} else {
			throw new UnsupportedOperationException(
					"Unexpected value of type: " + value.getClass());
		}
	}

	public void putUnlessDefined(String key, boolean value) {
		putUnlessDefined(key, new Val<Boolean>(value));
	}

	public void putUnlessDefined(String key, String value) {
		putUnlessDefined(key, new Val<String>(value));
	}

	public void putUnlessDefined(String key, Val<?> value) {
		if (get(key) == null) {
			put(key, value);
		}
	}

	public PerlMap sub(String... keys) {
		PerlMap submap = new PerlMap();
		for (String key : keys) {
			submap.put(key, get(key));
		}
		return submap;
	}
}
