package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;

/** Play all commands in the current editor
 */
public class StopHandler extends MonkeyHandlerBase {
	
	/**
	 * The constructor.
	 */
	public StopHandler() {
		super();
	}
	
	@Override
	public boolean isEnabled() {
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		if (controller!=null && controller.isCurrentlyConnected()) {
			if (controller.isRecordingON() || controller.isReplayON()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		if (controller.isRecordingON()) {
			controller.stopRecording();
			//__timerTask.cancel();
			//recordToolItem.setImageDescriptor(MonkeyTalkImagesEnum.RECORDING.image);
		}
		
		if (controller.isReplayON()) {
			controller.stopReplay();
		}
		
		return null;
	}
}
