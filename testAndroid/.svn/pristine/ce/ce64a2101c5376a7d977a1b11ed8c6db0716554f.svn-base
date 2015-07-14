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
package com.gorillalogic.monkeytalk.processor.command.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.Runner;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;

public class DebugCommandTest extends BaseCommandHelper {

	@Test
	public void testDebugGlobals() throws IOException {
		File dir = tempDir();
		File props = tempScript("globals.properties", "foo=123\nbar=Bo Bo", dir);
		File foo = tempScript("foo.mt", "Globals * Set foo=456\nDebug * Globals\nButton FOO Tap",
				dir);

		Runner runner = new Runner("iOS", HOST, PORT);
		runner.setVerbose(true);

		CommandServer server = new CommandServer(PORT);
		PlaybackResult result = runner.run(foo, null);
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap");

		assertThat(out.toString(), containsString("www.cloudmonkeymobile.com"));
		assertThat(out.toString(), containsString("loading globals.properties\n"));
		assertThat(out.toString(), not(containsString("loading globals:")));
		assertThat(out.toString(), containsString("Globals * Set foo=456 -> OK\n"));
		assertThat(out.toString(), containsString("\nfoo=456\n"));
		assertThat(out.toString(), containsString("\nbar=Bo Bo\n"));
		assertThat(out.toString(), containsString("Button FOO Tap -> OK\n"));
		assertThat(out.toString(), containsString("result: OK\n"));

		boolean success = props.delete();
		assertThat(success, is(true));
	}

	@Test
	public void testDebugVars() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Vars * Define foo=123 bar=\"Bo Bo\"\n" + "Debug * Vars\n"
				+ "Button FOO Tap", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new CommandServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Vars * Define foo=123 bar=\"Bo Bo\" -> OK"));
		assertThat(output, containsString("Debug * Vars -> OK"));
		assertThat(output, containsString("foo=123\n"));
		assertThat(output, containsString("bar=Bo Bo\n"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testDebugPrint() throws IOException {
		File dir = tempDir();
		tempScript(
				"foo.mt",
				"Button FOO Tap\nDebug * Print foo bar baz\nButton BAR Tap\nDebug debug Print Some text to file",
				dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new CommandServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap", "Button BAR Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(output, containsString("Debug * Print foo bar baz -> OK"));
		assertThat(output, containsString("Debug debug Print Some text to file -> OK"));
		assertThat(output, containsString("DEBUG : foo bar baz\n"));
		assertThat(output, containsString("Button BAR Tap -> OK"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testDebugTree() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(output, containsString("Debug * Tree -> OK\n"));
		assertThat(
				output,
				containsString("\nDEBUG : UIWindow(#1)\n  Button(LOGIN)\n  Input(username)\n  Button(LOGOUT)\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testDebugTreeWithFilter() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree B*", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(output, containsString("Debug * Tree B* -> OK\n"));
		assertThat(output, containsString("\nDEBUG : Button(LOGIN)\nButton(LOGOUT)\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testDebugTreeWithEmptyFilter() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree Fred*", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(output, containsString("Debug * Tree Fred* -> OK\n"));
		assertThat(output, containsString("\nDEBUG : COMPLETE : OK"));
	}

	@Test
	public void testDebugTreeWithInvalidJson() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree Joe", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT, "<<{{{**");
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is("bad tree"));
		assertThat(result.getDebug(), is("bad tree"));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Debug * Tree Joe -> ERROR : bad tree\n"));
		assertThat(output, containsString("COMPLETE : ERROR : bad tree"));
	}

	@Test
	public void testDebugTreeWithMissingTree() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree Joe", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT, "");
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is("bad tree"));
		assertThat(result.getDebug(), is("bad tree"));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Debug * Tree Joe -> ERROR : bad tree\n"));
		assertThat(output, containsString("COMPLETE : ERROR : bad tree"));
	}

	@Test
	public void testDebugTreeWithParseError() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree Joe", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT, "\"message\":\"not-a-tree\"");
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), containsString("error parsing tree"));
		assertThat(result.getDebug(), containsString("error parsing tree"));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Debug * Tree Joe -> ERROR : error parsing tree"));
		assertThat(output, containsString("COMPLETE : ERROR : error parsing tree"));
	}

	@Test
	public void testDebugTreeWithJsonFormat() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree * json", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(output, containsString("Debug * Tree * json -> OK\n"));
		assertThat(output, containsString("\nDEBUG : {"));
		assertThat(output, containsString("\"monkeyId\":\"#1\""));
		assertThat(output, containsString("\"monkeyId\":\"LOGIN\""));
		assertThat(output, containsString("\"monkeyId\":\"username\""));
		assertThat(output, containsString("\"monkeyId\":\"LOGOUT\""));
		assertThat(output, containsString("}\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testDebugTreeWithJsonFormatAndFilter() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree B* json", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(output, containsString("Debug * Tree B* json -> OK\n"));
		assertThat(output, containsString("\nDEBUG : ["));
		assertThat(output, containsString("\"monkeyId\":\"LOGIN\""));
		assertThat(output, containsString("\"monkeyId\":\"LOGOUT\""));
		assertThat(output, containsString("]\n"));
		assertThat(output, not(containsString("\"monkeyId\":\"#1\"")));
		assertThat(output, not(containsString("\"monkeyId\":\"username\"")));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testDebugTreeWithUnknownFormat() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nDebug * Tree * unknown", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new TreeServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO Tap -> OK"));
		assertThat(
				output,
				containsString("Debug * Tree * unknown -> ERROR : unknown tree format, allowed values are: [text, json]\n"));
		assertThat(output, not(containsString("DEBUG")));
		assertThat(
				output,
				containsString("COMPLETE : ERROR : unknown tree format, allowed values are: [text, json]"));
	}

	public class TreeServer extends CommandServer {
		public static final String TREE = "{\"monkeyId\":\"#1\",\"visible\":\"true\",\"identifiers\":[],\"value\":\"\",\"ordinal\":1,\"children\":["
				+ "{\"monkeyId\":\"LOGIN\",\"visible\":\"true\",\"identifiers\":[\"LOGIN\"],\"value\":\"LOGIN\",\"ordinal\":1,\"children\":[],\"className\":\"UIRoundedRectButton\",\"ComponentType\":\"Button\"},"
				+ "{\"monkeyId\":\"username\",\"visible\":\"true\",\"identifiers\":[],\"value\":\"fred\",\"ordinal\":1,\"children\":[],\"className\":\"UITextField\",\"ComponentType\":\"Input\"},"
				+ "{\"monkeyId\":\"LOGOUT\",\"visible\":\"true\",\"identifiers\":[\"LOGOUT\"],\"value\":\"LOGOUT\",\"ordinal\":1,\"children\":[],\"className\":\"UIRoundedRectButton\",\"ComponentType\":\"Button\"}"
				+ "],\"className\":\"UIWindow\",\"ComponentType\":\"UIWindow\"}";
		private String msg;

		public TreeServer(int port) throws IOException {
			this(port, "\"message\":" + TREE);
		}

		public TreeServer(int port, String msg) throws IOException {
			super(port);
			this.msg = msg;
		}

		@Override
		public Response serve(String uri, String method, Map<String, String> headers,
				JSONObject json) {
			Response r = super.serve(uri, method, headers, json);
			if ("dumptree".equalsIgnoreCase(json.optString("mtcommand"))) {
				return new Response(HttpStatus.OK, "{\"result\":\"OK\""
						+ (msg != null ? "," + msg : "") + "}");
			}
			return r;
		}
	}
}