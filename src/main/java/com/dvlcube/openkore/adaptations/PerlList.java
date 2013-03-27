package com.dvlcube.openkore.adaptations;

import java.util.ArrayList;

public class PerlList<E> extends ArrayList<E> {

	private static final long serialVersionUID = -4108981251142110677L;

	/**
	 * @param strings
	 * @return quoted words
	 * @since 07/01/2013
	 * @author Ulisses Lima
	 */
	public static PerlList<String> qw(Object... strings) {
		PerlList<String> qw = new PerlList<>();
		for (Object o : strings) {
			qw.add(o.toString());
		}
		return qw;
	}

	public static PerlList<String> qws(String string) {
		return qw(new Object[] { string.split(" ") });
	}

	public PerlList() {
		super();
	}

	public PerlList(E[] values) {
		super();
		addAll(values);
	}

	public void addAll(E[] array) {
		for (E e : array) {
			add(e);
		}
	}

	public String[] arr() {
		String[] arr = new String[size()];
		int i = 0;
		for (E e : this) {
			arr[i] = e.toString();
		}
		return arr;
	}

	public E[] array() {
		return (E[]) super.toArray();
	}

	public E get(E e) {
		for (E element : this) {
			if (element.equals(e)) {
				return element;
			}
		}
		return null;
	}

	public PerlList<E> remove(int i, int size) {
		for (; i < size; i++) {
			remove(i);
		}
		return this;
	}

	public PerlList<E> sub(int offset, int size) {
		PerlList<E> sub = new PerlList<>();
		int endIndex = offset + size;
		for (; offset < endIndex; offset++) {
			sub.add(get(offset));
		}
		return sub;
	}

	public PerlList<E> values() {
		// PerlList<E> copy = new PerlList<>();
		// for (E e : this) {
		// copy.add(e);
		// }
		return (PerlList<E>) clone();
	}
}
