package com.gorillalogic.monkeytalk.processor.command;

import java.util.ArrayList;
import java.util.List;

import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;

public class WaitFor extends BaseCommand {
	public static final int DEFAULT_WAITFOR_TIMEOUT = 10000;

	private ScriptProcessor processor;

	public WaitFor(Command cmd, Scope scope, PlaybackListener listener, ScriptProcessor processor) {
		super(cmd, scope, listener);
		this.processor = processor;
	}

	/** Helper to convert the given WaitFor into its Verify form. */
	public static Command convertToVerify(Command waitFor) {
		if (!waitFor.getAction().toLowerCase().startsWith("waitfor")) {
			return null;
		}

		String action = waitFor.getAction().toLowerCase().replaceFirst("waitfor", "verify");

		long timeout = DEFAULT_WAITFOR_TIMEOUT;

		if (waitFor.getArgs().size() > 0 && waitFor.getArgs().get(0) != null
				&& waitFor.getArgs().get(0).length() > 0) {
			String arg = waitFor.getArgs().get(0);

			// first arg is timeout
			int seconds = 0;
			try {
				seconds = Integer.parseInt(arg);
			} catch (NumberFormatException ex) {
				return null;
			}

			if (seconds < 1) {
				return null;
			}

			timeout = seconds * 1000;
		}

		List<String> newArgs = new ArrayList<String>(waitFor.getArgs());
		if (newArgs.size() > 0) {
			newArgs.remove(0);
		}
		Command verify = new Command(waitFor.getComponentType(), waitFor.getMonkeyId(), action,
				newArgs, waitFor.getModifiers());
		verify.setModifier(Command.TIMEOUT_MODIFIER, Long.toString(timeout));

		return verify;
	}

	/** Helper to compute bad args error message. */
	public static PlaybackResult badArgsError(Command waitFor, Scope scope) {
		return new PlaybackResult(PlaybackStatus.ERROR, "command '" + waitFor.getCommandName()
				+ "' has bad args '" + waitFor.getArgsAsString()
				+ "' -- optional first arg should be number of seconds to wait", scope);
	}

	public PlaybackResult waitFor() {
		Command verify = convertToVerify(cmd);

		if (verify == null) {
			return badArgsError(cmd, scope);
		}

		return processor.playbackVanillaCommand(verify, scope);
	}
}