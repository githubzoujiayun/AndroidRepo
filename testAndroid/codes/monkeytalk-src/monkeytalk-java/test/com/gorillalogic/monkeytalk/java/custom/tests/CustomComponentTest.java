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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.java.MonkeyTalkDriver;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.sender.Response;
import com.gorillalogic.monkeytalk.utils.TestHelper;

public class CustomComponentTest extends TestHelper {
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
			output += "PRINT=" + message;
		}
	};

	@Before
	public void before() throws IOException {
		output = "";
	}

	@After
	public void after() {
	}

	@Test
	public void testCustomComponent() throws IOException {
		MonkeyTalkDriver mt = new MonkeyTalkDriver(tempDir(), "iOS", HOST, PORT);
		mt.setScriptListener(LISTENER_WITH_OUTPUT);
		mt.registerCustomApplication(MyCustomApplication.class);

		CommandServer server = new CommandServer(PORT);
		MyCustomApplication app = (MyCustomApplication) mt.app();
		app.button("FOO1").tap();

		String f1 = app.fred("aaa").action1();
		String f2 = app.fred("bbb").action1("argA", "argB");

		String j1 = app.joe("ccc").action2();
		String j2 = app.joe("ddd", "EEXXTTRRAA").action2("arg1", 123);

		String i1 = app.injectPlayer("*").action3();
		String i2 = app.injectPlayer("*").action3("arg1", "arg2");

		String i3 = app.injectProcessor("*").action4("arg3");
		String i4 = app.injectProcessor("*").action4("arg4", "arg5");

		app.button("FOO2").tap();
		server.stop();

		List<Command> cmds = server.getCommands();
		assertThat(cmds.size(), is(2));
		assertThat(cmds.get(0).getCommand(), is("Button FOO1 tap"));
		assertThat(cmds.get(1).getCommand(), is("Button FOO2 tap"));

		assertThat(output, containsString("Button FOO1 tap -> OK\n"));
		assertThat(output, containsString("Debug * Print -> OK\n"));
		assertThat(output, containsString("\nPRINT=\n"));
		assertThat(output, containsString("Debug * Print arg1 arg2 -> OK\n"));
		assertThat(output, containsString("\nPRINT=arg1 arg2\n"));
		assertThat(output, containsString("Debug * Print arg3 -> OK\n"));
		assertThat(output, containsString("\nPRINT=arg3\n"));
		assertThat(output, containsString("Debug * Print arg4 arg5 -> OK\n"));
		assertThat(output, containsString("\nPRINT=arg4 arg5\n"));
		assertThat(output, containsString("Button FOO2 tap -> OK\n"));

		assertThat(f1, is("FRED-aaa"));
		assertThat(f2, is("FRED-bbb-argA-argB"));
		assertThat(j1, is("JOE=CCC=EXTRA=TXT=-1"));
		assertThat(j2, is("JOE=DDD=EEXXTTRRAA=ARG1=123"));
		assertThat(i1, nullValue());
		assertThat(i2, nullValue());
		assertThat(i3, nullValue());
		assertThat(i4, nullValue());
	}
}
