package com.gorillalogic.monkeytalk.utils;

/**
 * Methods that apply to storing a set of {@link String}s. The set is referenced by a
 * {@code String key}. <br>
 * <br>
 * No distinction is made between an empty set versus no set for a given key.
 * 
 */
public interface IStringSetStore {

	/**
	 * Inserts {@code item.toString()} as the first element of the set stored at {@code key}. <br>
	 * Previous occurrences of {@code item} are removed. <br>
	 * {@code null} and empty {@code item} values are (silently) not stored. <br>
	 */
	void prepend(String key, Object item);

	/**
	 * Inserts {@code item.toString()} as the last element of the set. <br>
	 * See #prepend(String, Object) for more details.
	 */
	void add(String key, Object item);

	/**
	 * Removes all occurrences of {@code item.toString()} from the set stored at {@code key}.
	 */
	void remove(String key, Object item);

	/**
	 * Removes the set stored at {@code key}.
	 */
	void clear(String key);

	/**
	 * @return true when {@code item.toString()} is in the set stored at {@code key}.
	 */
	boolean has(String key, Object item);
}
