package com.gorillalogic.monkeytalk.utils.tests;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.gorillalogic.monkeytalk.utils.StringSetStore;

public class StringCollectionStoreTest {
	private static final String key = "key";
	private static final String[] paths = {
			"zero",
			"one",
			"two",
			"three"
	};

	private static class MapStringCollectionStore extends StringSetStore
	{
		private final Map<String, String> map = new HashMap<String, String>();

		@Override
		protected String get(String key) {
			return map.get(key);
		}

		@Override
		protected void set(String key, String collection) {
			map.put(key, collection);
		}

		@Override
		public String getSeparator()
		{
			return super.getSeparator();
		}
	}

	@Test
	public void testRemove()
	{
		MapStringCollectionStore store = new MapStringCollectionStore();

		for (int i = 0; i < paths.length; i++)
		{
			store.clear(key);
			String cleared = store.get(key);
			Assert.assertTrue("cleared key is empty or null", cleared == null || cleared.isEmpty());
			for (String path : paths) {
				store.add(key, path);
			}
			String whole = store.get(key);
			String remove = paths[i];
			store.remove(key, remove);
			String actual = store.get(key);
			String expected = "";
			for (String path : paths)
			{
				if (path != remove)
				{
					expected += store.getSeparator() + path;
				}
			}
			expected = expected.substring(1);
			Assert.assertEquals(remove + " from " + whole, expected, actual);

			for (String path : paths)
			{
				store.remove(key, path);
				String value = store.get(key);
				Assert.assertFalse("value starts with seperator", value.startsWith(store.getSeparator()));
				Assert.assertFalse("value ends with seperator", value.endsWith(store.getSeparator()));
				Assert.assertFalse("value contains removed item " + path, value.contains(path));
			}
		}
	}

	@Test
	public void testRemoveSubString()
	{
		MapStringCollectionStore store = new MapStringCollectionStore();

		for (int i = 0; i < paths.length; i++)
		{
			store.clear(key);
			String cleared = store.get(key);
			Assert.assertTrue("cleared key is empty or null", cleared == null || cleared.isEmpty());
			String doubled = paths[i] + paths[i];
			for (String path : paths) {
				if (doubled.startsWith(path)) {
					store.add(key, doubled);
				}
				store.add(key, path);
			}

			String remove = paths[i];
			store.remove(key, remove);
			String value = store.get(key);

			Assert.assertFalse("value starts with seperator", value.startsWith(store.getSeparator()));
			Assert.assertFalse("value ends with seperator", value.endsWith(store.getSeparator()));
			Assert.assertTrue("value contains doubled string", value.contains(doubled));

			store.remove(key, doubled);
			value = store.get(key);

			Assert.assertFalse("value starts with seperator", value.startsWith(store.getSeparator()));
			Assert.assertFalse("value ends with seperator", value.endsWith(store.getSeparator()));
			Assert.assertFalse("value contains doubled string", value.contains(doubled));
			Assert.assertFalse("value contains removed string", value.contains(remove));

		}
	}
}
