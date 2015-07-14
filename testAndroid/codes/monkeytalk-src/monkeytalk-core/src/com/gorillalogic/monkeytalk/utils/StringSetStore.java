package com.gorillalogic.monkeytalk.utils;

import java.io.File;

/**
 * Encapsulation of sets of {@link String}s stored as a concatenation of the items in the set
 * delimited by {@link #getSeparator()}.<br>
 * <br>
 * Concrete classes must provide {@link #get(String)} and {@link #set(String, String)} to manage the
 * concatenated value. <br>
 * <br>
 * Implementation works by wrapping the concatenated set and item parameters in
 * {@link #getSeparator()} and then doing {@link CharSequence} operations on those {@code String}s.
 * 
 * @author j0nm00re
 * 
 */
abstract public class StringSetStore implements IStringSetStore {

	/**
	 * @param key
	 *          value used to identify the set.
	 * @return the set of items concatenated into a single {@link String} delimited by
	 *         {@link #getSeparator()}. {@code null} or an empty {@code String} may be returned.
	 */
	abstract protected String get(String key);

	/**
	 * Stores the set.
	 * 
	 * @param key
	 *          value used to identify the set.
	 * @param set
	 *          items concatenated into a single {@link String} delimited by {@link #getSeparator()}.
	 */
	abstract protected void set(String key, String set);

	/**
	 * @return by default {@link File#pathSeparator}.
	 */
	protected String getSeparator() {
		return File.pathSeparator;
	}

	/**
	 * @return the set or an empty {@code String}. Never null.
	 */
	protected String getSafe(String key) {
		String value = get(key);
		if (value == null) {
			return "";
		}
		return value.trim();
	}

	protected boolean isEmpty(Object value) {
		return value == null || value.toString().trim().isEmpty();
	}

	@Override
	public void prepend(String key, Object item) {
		insert(key, item, true);
	}

	@Override
	public void add(String key, Object item) {
		insert(key, item, false);
	}

	/**
	 * @param beforeFirst
	 *          indicates where new item should be in the resulting set.
	 */
	private void insert(String key, Object item, boolean beforeFirst) {
		if (isEmpty(item)) {
			return;
		}
		remove(key, item);
		String value = getSafe(key);
		String itemValue = item.toString();
		String pre = beforeFirst ? itemValue : value;
		String post = beforeFirst ? value : itemValue;
		value = value.isEmpty() ? itemValue : (pre + getSeparator() + post);
		set(key, value);
	}

	@Override
	public void remove(String key, Object item) {
		if (isEmpty(item)) {
			return;
		}

		// Surround the concatenated set and the item to be remove with separators
		String sep = getSeparator();
		StringBuilder value = new StringBuilder(sep + getSafe(key) + sep);
		String itemValue = sep + item.toString() + sep;

		// Find the separator delimited item
		int start = value.indexOf(itemValue);
		if (start < 0) {
			return;
		}

		// Replace it and clean up the extra separators.
		value.replace(start, start + itemValue.length(), sep);
		value.deleteCharAt(value.length() - 1);
		if (value.length() > 0) {
			value.deleteCharAt(0);
		}

		set(key, value.toString());
	}

	@Override
	public void clear(String key) {
		set(key, "");
	}

	@Override
	public boolean has(String key, Object item) {
		if (isEmpty(item)) {
			return false;
		}
		String value = getSeparator() + getSafe(key) + getSeparator();
		String itemValue = getSeparator() + item + getSeparator();
		boolean has = value.contains(itemValue);
		return has;
	}
}
