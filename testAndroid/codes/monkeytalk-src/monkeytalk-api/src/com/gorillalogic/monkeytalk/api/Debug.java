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
package com.gorillalogic.monkeytalk.api;

/**
 * Logging and diagnostics.\n\n
 * 
 * MonkeyId would be used as a filepath (absolute or project-relative) instead of '*' when using
 * print function in order to create a file and append the message to it.
 * 
 * @types script
 */
public interface Debug extends MTObject {

	/**
	 * Print all global variables (and their values) that are available.
	 */
	public void globals();

	/**
	 * Print all variables (and their values) that are available in the local scope.
	 * 
	 * @ignoreJS
	 */
	public void vars();

	/**
	 * Remove the output file if exists. You should use this to delete debug file when starting a
	 * test.
	 */
	public void erase();

	/**
	 * Print the given message. Use this to add debugging messages to script output.
	 * 
	 * @param message
	 *            the message to be printed
	 */
	public void print(String message);

	/**
	 * Print the tree of all visible UI components.
	 * 
	 * @param filter
	 *            (optional) a wildcard pattern to filter by component type (ex: But* would show
	 *            only Button and ButtonSelector components in the tree). Defaults to all
	 *            components.
	 * @param format
	 *            (optional) the output format (text or json). Defaults to text.
	 */
	public void tree(String filter, String format);
}