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

public class Joe {
	private String monkeyId;
	private String extra;

	public Joe() {
		this(null);
	}

	public Joe(String monkeyId) {
		this(monkeyId, "extra");
	}

	public Joe(String monkeyId, String extra) {
		this.monkeyId = monkeyId;
		this.extra = extra;
	}

	public String action2() {
		return action2("txt", -1);
	}

	public String action2(String txt) {
		return action2(txt, -1);
	}

	public String action2(String txt, int i) {
		StringBuilder sb = new StringBuilder("JOE=");
		if (monkeyId != null) {
			sb.append(monkeyId).append("=");
		}
		if (extra != null) {
			sb.append(extra).append("=");
		}

		sb.append(txt).append("=");
		sb.append(i);

		return sb.toString().toUpperCase();
	}
}
