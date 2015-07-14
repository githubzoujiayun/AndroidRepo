package com.gorillalogic.monkeytalk.processor.command;

import com.gorillalogic.monkeytalk.BuildStamp;
import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.ProcessorExec;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.sender.Response;

public class Sys extends BaseCommand {
	public Sys(Command cmd, Scope scope, PlaybackListener listener) {
		super(cmd, scope, listener);
	}

	public PlaybackResult exec() {
		if (cmd.getArgs().size() == 0) {
			return new PlaybackResult(PlaybackStatus.ERROR, "command '" + cmd.getCommand()
					+ "' must have a system command to execute as its first arg", scope);
		} else {
			listener.onStart(scope);
			Response resp = ProcessorExec.run(cmd.getArgs());
			listener.onComplete(scope, resp);
			return new PlaybackResult(resp, scope);
		}
	}

	public PlaybackResult execAndReturn() {
		if (cmd.getArgs().size() == 0) {
			return new PlaybackResult(PlaybackStatus.ERROR, "command '" + cmd.getCommand()
					+ "' must have a variable as its first arg", scope);
		} else if (cmd.getArgs().size() == 1) {
			return new PlaybackResult(PlaybackStatus.ERROR, "command '" + cmd.getCommand()
					+ "' must have a system command to execute as its second arg", scope);
		} else {
			listener.onStart(scope);
			Response resp = ProcessorExec.run(cmd.getArgs().subList(1, cmd.getArgs().size()));

			String key = cmd.getArgs().get(0);
			String val = resp.getMessage();

			if (scope.getVariables().containsKey(key)) {
				// local variable already in scope, so Get val into the local var
				scope.addVariable(key, val);
			} else if (com.gorillalogic.monkeytalk.processor.Globals.hasGlobal(key)) {
				// global of same name exists, so Get val into the global
				com.gorillalogic.monkeytalk.processor.Globals.setGlobal(key, val);
			} else {
				// no local exists & no global exists, so create a new local var
				scope.addVariable(key, val);
			}

			listener.onComplete(scope, resp);
			return new PlaybackResult(resp, scope);
		}
	}

	public PlaybackResult getMonkeyTalkVersion() {
		listener.onStart(scope);
		String buildNumber = BuildStamp.BUILD_NUMBER;
		listener.onComplete(scope, new Response());
		return new PlaybackResult(PlaybackStatus.OK, buildNumber);
	}
}
