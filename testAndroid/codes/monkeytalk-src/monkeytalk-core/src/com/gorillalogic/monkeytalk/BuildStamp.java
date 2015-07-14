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
package com.gorillalogic.monkeytalk;

public class BuildStamp {
	/* DO NOT EDIT THIS */
	// codegen-stamp
	public static final String VERSION = "2.0.10-SNAPSHOT";
	public static final String BUILD_NUMBER = "556";
	public static final String TIMESTAMP = "2014-11-19 09:40:00 MST";
	public static final String VERSION_INFO = "2.0.10-SNAPSHOT_556 - 2014-11-19 09:40:00 MST";
	public static final String STAMP = "MonkeyTalk v2.0.10-SNAPSHOT_556 - 2014-11-19 09:40:00 MST - Copyright 2012-2014 CloudMonkey LLC - www.cloudmonkeymobile.com";
	// codegen-end

	public static void main(String[] args) {
		System.out.println(STAMP);
	}

	public static String getStamp() {
		try {
			Class<?> klass = Class.forName("com.gorillalogic.monkeytalk.BuildStampPro");
			return (String) klass.getField("STAMP").get(null);
		} catch (Exception ex) {
			// not pro
			return STAMP;
		}
	}

	public static String getVersion(String prefix) {
		String stamp = getStamp();
		String[] parts = stamp.replaceAll("^.*\\s+v", "").split(" - ");
		if (parts.length > 1) {
			return prefix + " v" + parts[0] + " - " + parts[1];
		} else {
			return prefix + " v??? - ???";
		}
	}
}
