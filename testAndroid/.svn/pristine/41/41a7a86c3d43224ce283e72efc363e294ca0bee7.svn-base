/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2014 Gorilla Logic, Inc.

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
package com.gorillalogic.monkeytalk.java.custom.tests;

public class Fred {
	private String monkeyId;

	public Fred() {
		this(null);
	}

	public Fred(String monkeyId) {
		this.monkeyId = monkeyId;
	}

	public String action1(String... args) {
		StringBuilder sb = new StringBuilder("FRED-");
		if (monkeyId != null) {
			sb.append(monkeyId).append("-");
		}
		for (String arg : args) {
			sb.append(arg).append("-");
		}

		return sb.substring(0, sb.length() - 1);
	}
}
