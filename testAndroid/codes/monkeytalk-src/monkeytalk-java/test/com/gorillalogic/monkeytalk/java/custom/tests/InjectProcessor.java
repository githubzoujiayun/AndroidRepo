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

import java.util.Arrays;

import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;

public class InjectProcessor {
	private String monkeyId;

	public InjectProcessor() {
		this(null);
	}

	public InjectProcessor(String monkeyId) {
		this.monkeyId = monkeyId;
	}

	public String action4(String... args) {
		Command cmd = new Command("Debug", monkeyId, "Print", Arrays.asList(args), null);
		PlaybackResult result = processor.runScript(cmd, null);
		return result.getMessage();
	}

	private ScriptProcessor processor;

	public void setScriptProcessor(ScriptProcessor processor) {
		this.processor = processor;
	}
}
