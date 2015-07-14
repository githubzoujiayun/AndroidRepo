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
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;
import com.gorillalogic.monkeytalk.processor.command.WaitFor;

public class WaitForCommandTest extends BaseCommandHelper {

	@Test
	public void testWaitFor() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO WaitFor 5", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO verify %timeout=5000");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO WaitFor 5 -> OK : msg\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testWaitForDefaultTimeout() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO WaitFor", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO verify %timeout=" + WaitFor.DEFAULT_WAITFOR_TIMEOUT);

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FOO WaitFor -> OK : msg\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testWaitForBadArgs() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO WaitFor foo", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));

		server.assertCommands();

		assertThat(output, containsString("START"));
		assertThat(output, containsString("ERROR : command 'button.waitfor' has bad args 'foo'"));
	}

	@Test
	public void testWaitForBadNegativeTimeout() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO WaitFor -123", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));

		server.assertCommands();

		assertThat(output, containsString("START"));
		assertThat(output, containsString("ERROR : command 'button.waitfor' has bad args '-123'"));
	}

	@Test
	public void testWaitForWithFailure() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FRED WaitFor 5", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.FAILURE));

		server.assertCommands("Button FRED verify %timeout=5000");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button FRED WaitFor 5 -> FAILURE : fail on Fred\n"));
		assertThat(output, containsString("COMPLETE : FAILURE"));
	}

	@Test
	public void testWaitForWithError() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button JOE WaitFor 5", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));

		server.assertCommands("Button JOE verify %timeout=5000");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button JOE WaitFor 5 -> ERROR : error on Joe\n"));
		assertThat(output, containsString("COMPLETE : ERROR"));
	}
}