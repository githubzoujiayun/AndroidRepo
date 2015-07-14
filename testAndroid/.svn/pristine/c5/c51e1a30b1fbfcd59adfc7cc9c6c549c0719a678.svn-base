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
 * Setup and initialize the named variables.
 * 
 * @ignoreJS
 * 
 * @types script
 */
public interface Vars extends Verifiable {

	/**
	 * Define local variables used in the script with an optional default value. Must be the first
	 * command in a script if you want to call the script from another script while passing in
	 * values for the local variables.
	 * 
	 * @param args
	 *          the local variables, in the form of: var=default var2=default2
	 */
	public void define(String... args);

	/**
	 * Define local variables (alias for Vars.Define).
	 * 
	 * @param args
	 *          the local variables, in the form of: var=default var2=default2
	 */
	public void set(String... args);
}