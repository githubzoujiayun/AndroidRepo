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
package com.gorillalogic.monkeytalk.java.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.gorillalogic.monkeytalk.java.Logger;
import com.gorillalogic.monkeytalk.java.MonkeyTalkDriver;
import com.gorillalogic.monkeytalk.java.api.Application;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.sender.Response;
import com.gorillalogic.monkeytalk.utils.TestHelper;

public class LoggerTest extends TestHelper {
	private static final String HOST = "localhost";
	private static final int PORT = 18317;
	private static String output;

	private static final PlaybackListener LISTENER_WITH_OUTPUT = new PlaybackListener() {

		@Override
		public void onStart(Scope scope) {
			output += scope.getCurrentCommand();
		}

		@Override
		public void onScriptStart(Scope scope) {
			output = "START\n";
		}

		@Override
		public void onScriptComplete(Scope scope, PlaybackResult r) {
			output += "COMPLETE : " + r;
		}

		@Override
		public void onComplete(Scope scope, Response resp) {
			output += " -> " + resp + "\n";
		}

		@Override
		public void onPrint(String message) {
			output += message;
		}
	};

	@Before
	public void before() throws IOException {
		output = "";
	}

	@Test
	public void testCustomLogger() throws IOException {
		FooLogger foo = new FooLogger();
		MonkeyTalkDriver mt = new MonkeyTalkDriver(tempDir(), "iOS", HOST, PORT);
		mt.setScriptListener(LISTENER_WITH_OUTPUT);
		mt.setLogger(foo);

		CommandServer server = new CommandServer(PORT);
		Application app = mt.app();
		app.input("username").enterText("fred");
		app.input("password").enterText("pass");
		app.button("LOGIN").tap();
		server.stop();

		assertThat(server.getCommands().size(), is(3));
		assertThat(server.getCommands().get(0).getCommand(), is("Input username enterText fred"));
		assertThat(server.getCommands().get(1).getCommand(), is("Input password enterText pass"));
		assertThat(server.getCommands().get(2).getCommand(), is("Button LOGIN tap"));

		assertThat(output, containsString("Input username enterText fred -> OK\n"));
		assertThat(output, containsString("Input password enterText pass -> OK\n"));
		assertThat(output, containsString("Button LOGIN tap -> OK\n"));

		assertThat(foo.toString(), containsString("FOOInput username enterText fredFOO -> OK\n"));
		assertThat(foo.toString(), containsString("FOOInput password enterText passFOO -> OK\n"));
		assertThat(foo.toString(), containsString("FOOButton LOGIN tapFOO -> OK\n"));
	}

	@Test
	public void testQuiet() throws IOException {
		FooLogger foo = new FooLogger();
		MonkeyTalkDriver mt = new MonkeyTalkDriver(tempDir(), "iOS", HOST, PORT);
		mt.setScriptListener(LISTENER_WITH_OUTPUT);
		mt.setVerbose(false);
		mt.setLogger(foo);

		CommandServer server = new CommandServer(PORT);
		Application app = mt.app();
		app.input("username").enterText("fred");
		app.input("password").enterText("pass");
		app.button("LOGIN").tap();
		server.stop();

		assertThat(server.getCommands().size(), is(3));
		assertThat(server.getCommands().get(0).getCommand(), is("Input username enterText fred"));
		assertThat(server.getCommands().get(1).getCommand(), is("Input password enterText pass"));
		assertThat(server.getCommands().get(2).getCommand(), is("Button LOGIN tap"));

		assertThat(output, containsString("Input username enterText fred -> OK\n"));
		assertThat(output, containsString("Input password enterText pass -> OK\n"));
		assertThat(output, containsString("Button LOGIN tap -> OK\n"));

		assertThat(foo.toString(), is(""));
	}

	@Test
	public void testQuietest() throws IOException {
		FooLogger foo = new FooLogger();
		MonkeyTalkDriver mt = new MonkeyTalkDriver(tempDir(), "iOS", HOST, PORT, foo, false);
		mt.setScriptListener(LISTENER_WITH_OUTPUT);

		CommandServer server = new CommandServer(PORT);
		Application app = mt.app();
		app.input("username").enterText("fred");
		app.input("password").enterText("pass");
		app.button("LOGIN").tap();
		server.stop();

		assertThat(server.getCommands().size(), is(3));
		assertThat(server.getCommands().get(0).getCommand(), is("Input username enterText fred"));
		assertThat(server.getCommands().get(1).getCommand(), is("Input password enterText pass"));
		assertThat(server.getCommands().get(2).getCommand(), is("Button LOGIN tap"));

		assertThat(output, containsString("Input username enterText fred -> OK\n"));
		assertThat(output, containsString("Input password enterText pass -> OK\n"));
		assertThat(output, containsString("Button LOGIN tap -> OK\n"));

		assertThat(foo.toString(), is(""));
	}

	public class FooLogger extends Logger {
		private StringBuilder sb = new StringBuilder();

		@Override
		public void print(Object msg) {
			sb.append("FOO" + msg);
		}

		@Override
		public void println(Object msg) {
			sb.append("FOO" + msg + "\n");
		}

		@Override
		public String toString() {
			return sb.toString();
		}
	}
}
