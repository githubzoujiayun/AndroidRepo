package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gorillalogic.monkeyconsole.editors.FoneMonkeyTestEditor;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;

/** Play all commands in the current editor
 */
public class PlayAllHandler extends MonkeyHandlerBase {
	
	/**
	 * The constructor.
	 */
	public PlayAllHandler() {
		super();
	}
	
	@Override
	public boolean isEnabled() {
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		if (controller!=null && controller.isCurrentlyConnected()) {
			if (!controller.isRecordingON() && !controller.isReplayON()) {
				return true;
			}
		}
		return false;
	}
	
	/*
	  	if (extention.equalsIgnoreCase("mt")) {
			playToolItem.setText("Play All");
			playToolItem.setToolTipText("Play All");
			playToolItem.setEnabled(false);
			playToolItem.setImageDescriptor(MonkeyTalkImagesEnum.PLAY.image);
			playToolItem.setId("monkeyplay");
		} else if (extention.equalsIgnoreCase("mts")) {
			playToolItem.setText("Run As Suite");
			playToolItem.setToolTipText("Run as a Test Suite");
			playToolItem.setImageDescriptor(MonkeyTalkImagesEnum.PLAY.image);
			playToolItem.setId("monkeyplaysuite");
			playToolItem.setEnabled(false);
		} else if (extention.equalsIgnoreCase("js")) {
			playToolItem.setText("Run Script");
			playToolItem.setToolTipText("Run Script");
			playToolItem.setImageDescriptor(MonkeyTalkImagesEnum.PLAY.image);
			playToolItem.setId("org.eclipse.wst.jsdt.internal.ui.javaeditor.monkeytalk.runaction");
			playToolItem.setEnabled(false);
		}
	 */

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
		
		if (targetEditor==null) {
			MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Play All");
			String message = "Cannot play all, there does not seem to be any active editor";
			dialog.setMessage(message);
			dialog.open();
			return null;
		}
		
		if (MonkeyTalkUtils.isPlayable(targetEditor)) {
			targetEditor.getEditorSite().getWorkbenchWindow().getWorkbench().saveAllEditors(true);

			MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
			if (MonkeyTalkUtils.isScript(targetEditor)) {
				
				if (targetEditor instanceof FoneMonkeyTestEditor) {
					FoneMonkeyTestEditor fmte = (FoneMonkeyTestEditor)targetEditor;
					if (fmte.getActivePage() == 1) {
						fmte.convertFromMonkeyTalk();
					}
				}
				
				controller.startReplayAll();
				
			} else if (MonkeyTalkUtils.isSuite(targetEditor)) {
				controller.startSuiteReplay();
				
			} else if (MonkeyTalkUtils.isJavascript(targetEditor)) {
				try {
					controller.startJScriptReplay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Play All");
			String message = "Cannot play all, you need to have a script or suite as the front editor";
			dialog.setMessage(message);
			dialog.open();
		}
		
		return null;
	}	
}
