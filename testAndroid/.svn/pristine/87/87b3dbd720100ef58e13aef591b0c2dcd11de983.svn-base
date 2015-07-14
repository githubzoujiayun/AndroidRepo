/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2012 Gorilla Logic, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.gorillalogic.monkeytalk.sender;

import java.util.HashMap;
import java.util.Map;

public class CommandSenderFactory {
	private static final String DEFAULT_KEY = "default";
	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	static {
		registerDefaultClass(CommandSender.class);
	}

	public static void registerClass(String key, Class<?> klass) {
		classMap.put(key, klass);
	}

	public static void registerDefaultClass(Class<?> klass) {
		CommandSenderFactory.registerClass(CommandSenderFactory.DEFAULT_KEY, klass);
	}

	public static CommandSender createCommandSender(String host, int port) {
		return createCommandSender(DEFAULT_KEY, host, port, null);
	}

	public static CommandSender createCommandSender(String host, int port, String path) {
		return createCommandSender(DEFAULT_KEY, host, port, path);
	}

	public static CommandSender createCommandSender(String key, String host, int port, String path) {
		Class<?> klass = classMap.get(key);
		if (klass == null) {
			throw new IllegalArgumentException("no CommandSender registered with key '" + key + "'");
		}

		CommandSender sender = null;
		try {
			sender = (CommandSender) klass.newInstance();
			sender.init(null, host, port, path);
		} catch (Exception ex) {
			// somebody registered something bad here
			throw new RuntimeException(ex.getMessage());
		}
		return sender;
	}
}