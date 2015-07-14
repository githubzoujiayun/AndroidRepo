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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.gorillalogic.monkeytalk.api.js.tools.JSHelper;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;
import com.gorillalogic.monkeytalk.utils.FileUtils;

public class ScriptRunIfCommandTest extends BaseCommandHelper {

	@Test
	public void testSimpleVerify() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Device * Verify iOS os", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Device * Verify iOS os");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Device * Verify iOS os -> OK : msg\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testSimpleWaitFor() throws IOException {
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
	public void testRunIfWithVerify() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf Device * Verify iOS os", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Device * Verify iOS os", "Button BAR Tap");

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify iOS os -> OK : running bar.mt...\n"));
		assertThat(output, containsString("Script bar.mt Run\n"));
		assertThat(output, containsString("Button BAR Tap -> OK\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testRunIfWithWaitFor() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf Button FOO WaitFor 5", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO verify %timeout=5000", "Button BAR Tap");

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Button FOO WaitFor 5 -> OK : running bar.mt...\n"));
		assertThat(output, containsString("Script bar.mt Run\n"));
		assertThat(output, containsString("Button BAR Tap -> OK\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testRunIfWithModifiers() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt",
				"Script bar.mt RunIf Device * Verify iOS os %thinktime=5000 %timeout=5000", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Device * Verify iOS os %thinktime=5000 %timeout=5000",
				"Button BAR Tap");

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify iOS os %thinktime=5000 %timeout=5000 -> OK : running bar.mt...\n"));
		assertThat(output, containsString("Script bar.mt Run %thinktime=5000 %timeout=5000\n"));
		assertThat(output, containsString("Button BAR Tap -> OK\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testRunIfWithIgnoreModifier() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Button F1 Tap\nScript bar.mt RunIf foo %ignore=true\nButton F2 Tap",
				dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button F1 Tap", "Button F2 Tap");

		assertThat(output, containsString("START"));
		assertThat(output, containsString("Button F1 Tap -> OK\n"));
		assertThat(output, containsString("Script bar.mt RunIf foo %ignore=true -> OK : ignored\n"));
		assertThat(output, containsString("Button F2 Tap -> OK\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testRunIfWithError() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf Device * Verify Joe", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));

		server.assertCommands("Device * Verify Joe");

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify Joe -> ERROR : verify error - error on Joe\n"));
		assertThat(output, not(containsString("Button BAR Tap")));
		assertThat(output, containsString("COMPLETE : ERROR"));
	}

	@Test
	public void testRunIfWithFailure() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf Device * Verify Fred", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Device * Verify Fred");

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify Fred -> OK : not running bar.mt - fail on Fred\n"));
		assertThat(output, not(containsString("Button BAR Tap")));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testRunIfWithWaitForFailure() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf Button FRED WaitFor 5", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FRED verify %timeout=5000");

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Button FRED WaitFor 5 -> OK : not running bar.mt - fail on Fred\n"));
		assertThat(output, not(containsString("Button BAR Tap")));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testRunIfWithMissingVerify() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		String err = "command 'Script bar.mt RunIf' must have a valid verify command as its arguments";
		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is(err));

		assertThat(server.getCommands().size(), is(0));

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Script bar.mt RunIf -> ERROR : " + err + "\n"));
		assertThat(output, containsString("COMPLETE : ERROR : " + err));
	}

	@Test
	public void testRunIfWithInvalidVerifyUnknownComponent() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nScript bar.mt RunIf FOOBAR", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		String err = "command 'Script bar.mt RunIf FOOBAR' has invalid verify command 'FOOBAR' with unknown component type 'FOOBAR'";
		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is(err));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Script bar.mt RunIf FOOBAR -> ERROR : " + err + "\n"));
		assertThat(output, containsString("COMPLETE : ERROR : " + err));
	}

	@Test
	public void testRunIfWithInvalidVerifyMissingAction() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nScript bar.mt RunIf Button *", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		String err = "command 'Script bar.mt RunIf Button *' has invalid verify command 'Button *' with missing action";
		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is(err));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Script bar.mt RunIf Button * -> ERROR : " + err + "\n"));
		assertThat(output, containsString("COMPLETE : ERROR : " + err));
	}

	@Test
	public void testRunIfWithInvalidVerifyUnknownAction() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nScript bar.mt RunIf Button * Fred", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		String err = "command 'Script bar.mt RunIf Button * Fred' has invalid verify command 'Button * Fred' with unknown action 'Fred' on component 'Button'";
		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is(err));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Script bar.mt RunIf Button * Fred -> ERROR : " + err
				+ "\n"));
		assertThat(output, containsString("COMPLETE : ERROR : " + err));
	}

	@Test
	public void testRunIfWithInvalidVerifyUnverifiableComponent() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nScript bar.mt RunIf Script * Run", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		String err = "command 'Script bar.mt RunIf Script * Run' has invalid verify command 'Script * Run' with component 'Script' that is not verifiable";
		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is(err));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Script bar.mt RunIf Script * Run -> ERROR : " + err
				+ "\n"));
		assertThat(output, containsString("COMPLETE : ERROR : " + err));
	}

	@Test
	public void testRunIfWithInvalidVerifyWithBadVerifyAction() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Button FOO Tap\nScript bar.mt RunIf Button * Tap", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		String err = "command 'Script bar.mt RunIf Button * Tap' has invalid verify command 'Button * Tap' with bad verify action 'Tap'";
		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is(err));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString("Script bar.mt RunIf Button * Tap -> ERROR : " + err
				+ "\n"));
		assertThat(output, containsString("COMPLETE : ERROR : " + err));
	}

	@Test
	public void testRunIfWithInvalidShouldFailModifier() throws IOException {
		String cmd = "Script bar.mt RunIf Button FOO Verify %shouldfail=true";
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Button FOO Tap\n" + cmd, dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		String err = "command 'script.runif' has illegal shouldFail modifier";
		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is(err));

		server.assertCommands("Button FOO Tap");

		assertThat(output, containsString("START\n"));
		assertThat(output, containsString("Button FOO Tap -> OK\n"));
		assertThat(output, containsString(cmd + " -> ERROR : " + err + "\n"));
		assertThat(output, containsString("COMPLETE : ERROR : " + err));
	}

	@Test
	public void testRunIfWithInvalidWaitFor() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf Button FOO WaitFor bad", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));

		server.assertCommands();

		assertThat(output, containsString("START"));
		assertThat(output, containsString("ERROR : command 'button.waitfor' has bad args 'bad'"));
	}

	@Test
	public void testRunIfWithMissingScript() throws IOException {
		File dir = tempDir();
		tempScript("foo.mt", "Script missing.mt RunIf Device * Verify iOS os", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is("script 'missing.mt' not found"));

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script missing.mt RunIf Device * Verify iOS os -> OK : running missing.mt...\n"));
		assertThat(output, containsString("Script missing.mt Run -> OK"));
		assertThat(output, containsString("COMPLETE : ERROR : script 'missing.mt' not found"));
	}

	@Test
	public void testRunIfWithChildScriptVars() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Vars * Define bar\nButton ${bar} Tap", dir);
		tempScript("foo.mt", "Script bar.mt RunIf Device * Verify iOS os", dir);

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.mt");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Device * Verify iOS os", "Button <bar> Tap");

		assertThat(output, containsString("START"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify iOS os -> OK : running bar.mt...\n"));
		assertThat(output, containsString("Script bar.mt Run\n"));
		assertThat(output, containsString("Button <bar> Tap -> OK\n"));
		assertThat(output, containsString("COMPLETE : OK"));
	}

	@Test
	public void testRunIfJavascript() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		File foo = tempScript("foo.mt",
				"Button FOO1 Tap\nScript bar.mt RunIf Device * Verify iOS os\nButton FOO2 Tap", dir);
		File fooJS = new File(dir, "foo.js");

		JSHelper.genAPIAndLib(dir);
		JSHelper.genJS(foo);

		String js = FileUtils.readFile(fooJS);
		assertThat(js, containsString("load(\"libs/MyProj.js\");\n"));
		assertThat(js, containsString("MyProj.foo.prototype.run = function() {\n"));
		assertThat(js, containsString("app.button(\"FOO1\").tap();\n"));
		assertThat(
				js,
				containsString("app.bar().runIf(\"Device\", \"*\", \"Verify\", \"iOS\", \"os\");\n"));
		assertThat(js, containsString("app.button(\"FOO2\").tap();\n"));

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.js");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO1 tap", "Device * Verify iOS os", "Button BAR Tap",
				"Button FOO2 tap");

		assertThat(output, containsString("Button FOO1 tap -> OK\n"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify iOS os -> OK : running bar.mt...\n"));
		assertThat(
				output,
				containsString("Script bar.mt Run\nSTART\nButton BAR Tap -> OK\nCOMPLETE : OK -> OK\n"));
		assertThat(output, containsString("Button FOO2 tap -> OK\n"));
	}

	@Test
	public void testRunIfJavascriptAndModifiers() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		File foo = tempScript(
				"foo.mt",
				"Button FOO1 Tap\nScript bar.mt RunIf Device * Verify iOS os  %thinktime=5000 %timeout=5000\nButton FOO2 Tap",
				dir);
		File fooJS = new File(dir, "foo.js");

		JSHelper.genAPIAndLib(dir);
		JSHelper.genJS(foo);

		String js = FileUtils.readFile(fooJS);
		assertThat(js, containsString("load(\"libs/MyProj.js\");\n"));
		assertThat(js, containsString("MyProj.foo.prototype.run = function() {\n"));
		assertThat(js, containsString("app.button(\"FOO1\").tap();\n"));
		assertThat(
				js,
				containsString("app.bar().runIf(\"Device\", \"*\", \"Verify\", \"iOS\", \"os\", {thinktime:\"5000\", timeout:\"5000\"});\n"));
		assertThat(js, containsString("app.button(\"FOO2\").tap();\n"));

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.js");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO1 tap",
				"Device * Verify iOS os %thinktime=5000 %timeout=5000", "Button BAR Tap",
				"Button FOO2 tap");

		assertThat(output, containsString("Button FOO1 tap -> OK\n"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify iOS os %thinktime=5000 %timeout=5000 -> OK : running bar.mt...\n"));
		assertThat(
				output,
				containsString("Script bar.mt Run %thinktime=5000 %timeout=5000\nSTART\nButton BAR Tap -> OK\nCOMPLETE : OK -> OK\n"));
		assertThat(output, containsString("Button FOO2 tap -> OK\n"));
	}

	@Test
	public void testRunIfJavascriptWithError() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		File foo = tempScript("foo.mt",
				"Button FOO1 Tap\nScript bar.mt RunIf Device * Verify Joe\nButton FOO2 Tap", dir);
		File fooJS = new File(dir, "foo.js");

		JSHelper.genAPIAndLib(dir);
		JSHelper.genJS(foo);

		String js = FileUtils.readFile(fooJS);
		assertThat(js, containsString("load(\"libs/MyProj.js\");\n"));
		assertThat(js, containsString("MyProj.foo.prototype.run = function() {\n"));
		assertThat(js, containsString("app.button(\"FOO1\").tap();\n"));
		assertThat(js, containsString("app.bar().runIf(\"Device\", \"*\", \"Verify\", \"Joe\");\n"));
		assertThat(js, containsString("app.button(\"FOO2\").tap();\n"));

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.js");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.ERROR));
		assertThat(result.getMessage(), is("verify error - error on Joe"));

		server.assertCommands("Button FOO1 tap", "Device * Verify Joe");

		assertThat(output, containsString("Button FOO1 tap -> OK\n"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify Joe -> ERROR : verify error - error on Joe\n"));
	}

	@Test
	public void testRunIfJavascriptWithFailure() throws IOException {
		File dir = tempDir();
		tempScript("bar.mt", "Button BAR Tap", dir);
		File foo = tempScript("foo.mt",
				"Button FOO1 Tap\nScript bar.mt RunIf Device * Verify Fred\nButton FOO2 Tap", dir);
		File fooJS = new File(dir, "foo.js");

		JSHelper.genAPIAndLib(dir);
		JSHelper.genJS(foo);

		String js = FileUtils.readFile(fooJS);
		assertThat(js, containsString("load(\"libs/MyProj.js\");\n"));
		assertThat(js, containsString("MyProj.foo.prototype.run = function() {\n"));
		assertThat(js, containsString("app.button(\"FOO1\").tap();\n"));
		assertThat(js,
				containsString("app.bar().runIf(\"Device\", \"*\", \"Verify\", \"Fred\");\n"));
		assertThat(js, containsString("app.button(\"FOO2\").tap();\n"));

		ScriptProcessor processor = new ScriptProcessor(HOST, PORT, dir);

		CommandServer server = new ErrorOnJoeAndFailOnFredServer(PORT);
		processor.setPlaybackListener(LISTENER_WITH_OUTPUT);
		PlaybackResult result = processor.runScript("foo.js");
		server.stop();

		assertThat("FAIL: " + result, result.getStatus(), is(PlaybackStatus.OK));

		server.assertCommands("Button FOO1 tap", "Device * Verify Fred", "Button FOO2 tap");

		assertThat(output, containsString("Button FOO1 tap -> OK\n"));
		assertThat(
				output,
				containsString("Script bar.mt RunIf Device * Verify Fred -> OK : not running bar.mt - fail on Fred\n"));
		assertThat(output, containsString("Button FOO2 tap -> OK\n"));
	}

	private class ErrorOnJoeAndFailOnFredServer extends CommandServer {
		public ErrorOnJoeAndFailOnFredServer(int port) throws IOException {
			super(port);
		}

		@Override
		public Response serve(String uri, String method, Map<String, String> headers,
				JSONObject json) {
			Response resp = super.serve(uri, method, headers, json);

			if (json.toString().toLowerCase().contains("joe")) {
				return new Response(HttpStatus.OK, "{result:\"ERROR\",message:\"error on Joe\"}");
			} else if (json.toString().toLowerCase().contains("fred")) {
				return new Response(HttpStatus.OK, "{result:\"FAILURE\",message:\"fail on Fred\"}");
			} else if (json.optString("action", "").equalsIgnoreCase("verify")) {
				return new Response(HttpStatus.OK, "{result:\"OK\",message:\"msg\"}");
			}

			return resp;
		}
	}

	private class CommandServer extends com.gorillalogic.monkeytalk.utils.TestHelper.CommandServer {
		public CommandServer(int port) throws IOException {
			super(port);
		}

		public void assertCommands(String... cmds) {
			assertCommands(false, cmds);
		}

		public void assertCommands(boolean showDefaultTimings, String... cmds) {
			assertThat(getCommands(), notNullValue());
			assertThat(getCommands().size(), is(cmds.length));
			for (int i = 0; i < cmds.length; i++) {
				assertThat(getCommands().get(i).getCommand(showDefaultTimings), is(cmds[i]));
			}
		}
	}
}