package com.gorillalogic.monkeytalk.processor.command;

import java.util.ArrayList;

import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.api.meta.API;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;
import com.gorillalogic.monkeytalk.sender.Response;

public class ScriptRunIf extends BaseCommand {
	private ScriptProcessor processor;

	public ScriptRunIf(Command cmd, Scope scope, PlaybackListener listener,
			ScriptProcessor processor) {
		super(cmd, scope, listener);
		this.processor = processor;
	}

	public PlaybackResult runIf() {
		if (cmd.getArgs().size() == 0) {
			listener.onStart(scope);
			String msg = "command '" + cmd.getCommand()
					+ "' must have a valid verify command as its arguments";
			Response resp = new Response.Builder().error().message(msg).build();
			listener.onComplete(scope, resp);
			return new PlaybackResult(resp, scope);
		} else {
			Command verify = new Command(cmd.getArgsAsString() + " " + cmd.getModifiersAsString());

			if (!API.hasComponentType(verify.getComponentType())) {
				String msg = "command '" + cmd.getCommand() + "' has invalid verify command '"
						+ verify.getCommand() + "' with unknown component type '"
						+ verify.getComponentType() + "'";
				listener.onStart(scope);
				Response resp = new Response.Builder().error().message(msg).build();
				listener.onComplete(scope, resp);
				return new PlaybackResult(resp, scope);
			} else if (verify.getAction() == null) {
				String msg = "command '" + cmd.getCommand() + "' has invalid verify command '"
						+ verify.getCommand() + "' with missing action";
				listener.onStart(scope);
				Response resp = new Response.Builder().error().message(msg).build();
				listener.onComplete(scope, resp);
				return new PlaybackResult(resp, scope);
			} else if (!API.getComponent(verify.getComponentType()).hasAction(verify.getAction())) {
				String msg = "command '" + cmd.getCommand() + "' has invalid verify command '"
						+ verify.getCommand() + "' with unknown action '" + verify.getAction()
						+ "' on component '" + verify.getComponentType() + "'";
				listener.onStart(scope);
				Response resp = new Response.Builder().error().message(msg).build();
				listener.onComplete(scope, resp);
				return new PlaybackResult(resp, scope);
			} else if (!API.getComponent(verify.getComponentType()).hasSuper("verifiable")) {
				String msg = "command '" + cmd.getCommand() + "' has invalid verify command '"
						+ verify.getCommand() + "' with component '" + verify.getComponentType()
						+ "' that is not verifiable";
				listener.onStart(scope);
				Response resp = new Response.Builder().error().message(msg).build();
				listener.onComplete(scope, resp);
				return new PlaybackResult(resp, scope);
			} else if (!API.getComponent("Verifiable").hasAction(verify.getAction())) {
				String msg = "command '" + cmd.getCommand() + "' has invalid verify command '"
						+ verify.getCommand() + "' with bad verify action '" + verify.getAction()
						+ "'";
				listener.onStart(scope);
				Response resp = new Response.Builder().error().message(msg).build();
				listener.onComplete(scope, resp);
				return new PlaybackResult(resp, scope);
			} else if (verify.shouldFail()) {
				String msg = "command '" + cmd.getCommandName()
						+ "' has illegal shouldFail modifier";
				listener.onStart(scope);
				Response resp = new Response.Builder().error().message(msg).build();
				listener.onComplete(scope, resp);
				return new PlaybackResult(resp, scope);
			} else {
				Command verify2 = verify;
				if (verify.getAction().toLowerCase().startsWith("waitfor")) {
					verify2 = WaitFor.convertToVerify(verify);

					if (verify2 == null) {
						return WaitFor.badArgsError(verify, scope);
					}
				}

				Response verifyResp = processor.runCommand(verify2);
				PlaybackResult verifyResult = new PlaybackResult(verifyResp, scope);
				String verifyMsg = (verifyResult != null && verifyResult.getMessage() != null
						&& verifyResult.getMessage().length() > 0 ? " - "
						+ verifyResult.getMessage() : "");

				if (verifyResult.getStatus().equals(PlaybackStatus.OK)) {
					String msg = "running " + cmd.getMonkeyId() + "...";
					Response resp = new Response.Builder().ok().message(msg).build();
					listener.onStart(scope);
					listener.onComplete(scope, resp);

					cmd.setArgs(new ArrayList<String>());
					cmd.setAction("Run");
					return processor.runScript(cmd, scope);
				} else if (verifyResult.getStatus().equals(PlaybackStatus.FAILURE)) {
					String msg = "not running " + cmd.getMonkeyId() + verifyMsg;
					Response resp = new Response.Builder().ok().message(msg).build();
					listener.onStart(scope);
					listener.onComplete(scope, resp);
					return new PlaybackResult(PlaybackStatus.OK, msg, scope);
				} else {
					String msg = "verify error" + verifyMsg;
					Response resp = new Response.Builder().error().message(msg).build();
					listener.onStart(scope);
					listener.onComplete(scope, resp);
					return new PlaybackResult(PlaybackStatus.ERROR, msg, scope);
				}
			}
		}
	}
}