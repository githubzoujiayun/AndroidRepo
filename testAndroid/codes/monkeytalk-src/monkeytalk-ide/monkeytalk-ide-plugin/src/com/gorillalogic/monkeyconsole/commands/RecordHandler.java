package com.gorillalogic.monkeyconsole.commands;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.plugin.MonkeyTalkImagesEnum;

/**
 * Play all commands in the current editor
 */
public class RecordHandler extends MonkeyHandlerBase implements IElementUpdater {

	/**
	 * The constructor.
	 */
	public RecordHandler() {
		super();
	}

	public void updateRecordingState() {
		this.refreshUIElements();
	}

	@Override
	public boolean isEnabled() {
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		if (controller != null && controller.isCurrentlyConnected()) {
			if (!controller.isRecordingON() && !controller.isReplayON()) {
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

		IEditorPart targetEditor;
		try {
			targetEditor = HandlerUtil.getActiveEditorChecked(event);
		} catch (ExecutionException e) {
			targetEditor = null;
		}

		if (targetEditor == null) {
			MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Record");
			String message = "Cannot record, there does not seem to be any active editor";
			dialog.setMessage(message);
			dialog.open();
			return null;
		}

		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		if (MonkeyTalkUtils.isRecordable(targetEditor)) {

			if (controller.isRecordingON()) {
				return null;
			}
			controller.startRecording();

			startAnimation();

		} else {
			MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Record");
			String message = "Cannot record, you need to have a script as the front editor";
			dialog.setMessage(message);
			dialog.open();
		}

		return null;
	}

	protected int currentAnimationIndex = 0;

	@Override
	public void updateElement(UIElement uielement, Map params) {
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		if (controller != null && controller.isRecordingON()) {
			if (++currentAnimationIndex > 18) {
				currentAnimationIndex = 1;
			}
			String iconFilePath = "icons/coolbaricons/recordingimages2/s" + currentAnimationIndex
					+ ".gif";

			ImageDescriptor icon = FoneMonkeyPlugin.getImageDescriptor(iconFilePath);
			uielement.setIcon(icon);
		} else {
			stopAnimation();
			uielement.setIcon(MonkeyTalkImagesEnum.RECORDING.image);
		}
	}

	// animation of the record button
	protected Timer animationTimer;
	protected TimerTask animationTimerTask;

	protected void startAnimation() {
		animationTimerTask = new TimerTask() {

			public void run() {
				RecordHandler.this.refreshUIElements();
			}
		};
		if (animationTimer != null) {
			animationTimer.cancel();
		}
		animationTimer = new Timer();
		animationTimer.schedule(animationTimerTask, 0, // initial delay
				200); // subsequent rate
	}

	protected void stopAnimation() {
		if (animationTimer != null) {
			animationTimer.cancel();
			animationTimer = null;
		}
	}
}
