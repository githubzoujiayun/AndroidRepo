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
package com.gorillalogic.monkeytalk.api.js.tools.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Test;

import com.gorillalogic.monkeytalk.api.js.tools.Template;
import com.gorillalogic.monkeytalk.utils.TestHelper;

public class TemplateTest extends TestHelper {

	@AfterClass
	public static void afterClass() throws IOException {
		cleanup();
	}

	@Test
	public void testLowerCamel() throws IOException {
		assertThat(Template.lowerCamel(null), nullValue());
		assertThat(Template.lowerCamel(""), is(""));
		assertThat(Template.lowerCamel("a"), is("a"));
		assertThat(Template.lowerCamel("A"), is("a"));
		assertThat(Template.lowerCamel("aa"), is("aa"));
		assertThat(Template.lowerCamel("AA"), is("aA"));
	}

	@Test
	public void testReplace() throws IOException {
		Template template = new Template("templates/libgen/lib.template.js");
		assertThat(template.toString(), containsString("// $LIB_NAME$.js generated by MonkeyTalk"));

		template.replace("LIB_NAME", "foobar");
		assertThat(template.toString(), containsString("// foobar.js generated by MonkeyTalk"));
	}
}