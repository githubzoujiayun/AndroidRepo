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
package com.gorillalogic.monkeytalk.parser.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;

import org.junit.Test;

import com.gorillalogic.monkeytalk.parser.CSVParser;
import com.gorillalogic.monkeytalk.parser.MonkeyTalkParser;

public class ParserTest {

	@Test
	public void testParsingNull() {
		List<String> tokens = CSVParser.parse(null);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(0));
	}
	
	@Test
	public void testParsingEmpty() {
		List<String> tokens = CSVParser.parse("");
		
		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(0));
	}
	
	@Test
	public void testParsingSpaces() {
		List<String> tokens = CSVParser.parse("   ");
		
		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(0));
	}
	
	@Test
	public void testParsing() {
		String cmd = "Button OK Click";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(3));
		assertThat(tokens, hasItems("Button", "OK", "Click"));
	}

	@Test
	public void testParsingQuotes() {
		String cmd = "\"Button\" \"ADD CONTACT\" \"Click\"";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(3));
		assertThat(tokens,
				hasItems("\"Button\"", "\"ADD CONTACT\"", "\"Click\""));
	}

	@Test
	public void testParsingQuotedArgs() {
		String cmd = "Button OK Click arg \"some arg\" \"third arg\"";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(6));
		assertThat(
				tokens,
				hasItems("Button", "OK", "Click", "arg", "\"some arg\"",
						"\"third arg\""));
	}

	@Test
	public void testParsingModifiers() {
		String cmd = "Button OK Click %foo=123 %bar=654";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(5));
		assertThat(tokens,
				hasItems("Button", "OK", "Click", "%foo=123", "%bar=654"));
	}

	@Test
	public void testParsingArgsAndModifiers() {
		String cmd = "Button OK Click arg key=val %foo=123 %bar=654";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(7));
		assertThat(
				tokens,
				hasItems("Button", "OK", "Click", "arg", "key=val",
						"%foo=123", "%bar=654"));
	}

	@Test
	public void testParsingQuotedArgsAndModifiers() {
		String cmd = "Button OK Click arg \"some arg\" \"%foo=123\" %bar=654";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(7));
		assertThat(
				tokens,
				hasItems("Button", "OK", "Click", "arg", "\"some arg\"",
						"\"%foo=123\"", "%bar=654"));
	}

	@Test
	public void testParsingQuotedArgValues() {
		String cmd = "Vars * Define foo=\"some val\" bar=\"other val\"";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(5));
		assertThat(
				tokens,
				hasItems("Vars", "*", "Define", "foo=\"some val\"",
						"bar=\"other val\""));
	}

	@Test
	public void testParsingQuotedModifierValues() {
		String cmd = "Button OK Click %foo=val %bar=\"some val\"";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(5));
		assertThat(
				tokens,
				hasItems("Button", "OK", "Click", "%foo=val",
						"%bar=\"some val\""));
	}

	@Test
	public void testParsingTabs() {
		String cmd = "Button\tOK\tClick";
		List<String> tokens = MonkeyTalkParser.parse(cmd);

		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(3));
		assertThat(tokens, hasItems("Button", "OK", "Click"));
	}
	
	@Test
	public void testParsingQuotedArgsAndModifiersWithTabs() {
		String cmd = "Button OK Click arg foo=\"some\tval\" %foo=123 %bar=\"other\tval\"";
		List<String> tokens = MonkeyTalkParser.parse(cmd);
		
		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(7));
		assertThat(tokens, hasItems("Button", "OK", "Click", "arg", "foo=\"some\tval\"",
				"%foo=123", "%bar=\"other\tval\""));
	}
	
	@Test
	public void testParsingQuotedArgsAndModifiersWithEscapedNewlines() {
		String cmd = "Button OK Click arg foo=\"some\\nval\" %foo=123 %bar=\"other\\nval\"";
		List<String> tokens = MonkeyTalkParser.parse(cmd);
		
		assertThat(tokens, notNullValue());
		assertThat(tokens.size(), is(7));
		assertThat(tokens, hasItems("Button", "OK", "Click", "arg", "foo=\"some\\nval\"",
				"%foo=123", "%bar=\"other\\nval\""));
	}
}