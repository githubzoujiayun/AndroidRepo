/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2013 Gorilla Logic, Inc.

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
package com.gorillalogic.monkeytalk.java.api;

import java.util.List;
import java.util.Map;

/**
 * The application under test.
 */
public interface App {
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments. The class is given in the monkeyId by its fully qualified class name.
	 */
	public void exec();
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments. The class is given in the monkeyId by its fully qualified class name.
	 * @param mods the MonkeyTalk modifiers
	 */
	public void exec(Map<String, String> mods);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 */
	public void exec(String method);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param mods the MonkeyTalk modifiers
	 */
	public void exec(String method, Map<String, String> mods);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param args the String args to be supplied to the method
	 */
	public void exec(String method, String... args);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param args the String args to be supplied to the method
	 */
	public void exec(String method, List<String> args);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param args the String args to be supplied to the method
	 * @param mods the MonkeyTalk modifiers
	 */
	public void exec(String method, List<String> args, Map<String, String> mods);

	/**
	 * Execute a method on a native class. The method must take zero or more String arguments and return a String result. The returned value is set into the given variable name. The class is given in the monkeyId by its fully qualified class name.
	 * @return the return value
	 */
	public String execAndReturn();
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments and return a String result. The returned value is set into the given variable name. The class is given in the monkeyId by its fully qualified class name.
	 * @param mods the MonkeyTalk modifiers
	 * @return the return value
	 */
	public String execAndReturn(Map<String, String> mods);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments and return a String result. The returned value is set into the given variable name. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @return the return value
	 */
	public String execAndReturn(String method);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments and return a String result. The returned value is set into the given variable name. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param mods the MonkeyTalk modifiers
	 * @return the return value
	 */
	public String execAndReturn(String method, Map<String, String> mods);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments and return a String result. The returned value is set into the given variable name. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param args the String args to be supplied to the method
	 * @return the return value
	 */
	public String execAndReturn(String method, String... args);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments and return a String result. The returned value is set into the given variable name. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param args the String args to be supplied to the method
	 * @return the return value
	 */
	public String execAndReturn(String method, List<String> args);
	/**
	 * Execute a method on a native class. The method must take zero or more String arguments and return a String result. The returned value is set into the given variable name. The class is given in the monkeyId by its fully qualified class name.
	 * @param method the method to call
	 * @param args the String args to be supplied to the method
	 * @param mods the MonkeyTalk modifiers
	 * @return the return value
	 */
	public String execAndReturn(String method, List<String> args, Map<String, String> mods);

	/**
	 * Restart the application under test. The currently running app will be terminated, a new instance of the app will be launched, and the connection to the MonkeyTalk Agent will be reestablished. Only available in MonkeyTalk Professional and only for Android Emulators or tethered Android devices. Timeout parameter specifies how long to wait for the app to restart and reconnect. Default timeout is 15000ms (aka 15 seconds).
	 */
	public void restart();
	/**
	 * Restart the application under test. The currently running app will be terminated, a new instance of the app will be launched, and the connection to the MonkeyTalk Agent will be reestablished. Only available in MonkeyTalk Professional and only for Android Emulators or tethered Android devices. Timeout parameter specifies how long to wait for the app to restart and reconnect. Default timeout is 15000ms (aka 15 seconds).
	 * @param mods the MonkeyTalk modifiers
	 */
	public void restart(Map<String, String> mods);
	/**
	 * Restart the application under test. The currently running app will be terminated, a new instance of the app will be launched, and the connection to the MonkeyTalk Agent will be reestablished. Only available in MonkeyTalk Professional and only for Android Emulators or tethered Android devices. Timeout parameter specifies how long to wait for the app to restart and reconnect. Default timeout is 15000ms (aka 15 seconds).
	 * @param timeout how long to wait for the app to restart (in ms)
	 */
	public void restart(int timeout);
	/**
	 * Restart the application under test. The currently running app will be terminated, a new instance of the app will be launched, and the connection to the MonkeyTalk Agent will be reestablished. Only available in MonkeyTalk Professional and only for Android Emulators or tethered Android devices. Timeout parameter specifies how long to wait for the app to restart and reconnect. Default timeout is 15000ms (aka 15 seconds).
	 * @param timeout how long to wait for the app to restart (in ms)
	 * @param mods the MonkeyTalk modifiers
	 */
	public void restart(int timeout, Map<String, String> mods);
}
