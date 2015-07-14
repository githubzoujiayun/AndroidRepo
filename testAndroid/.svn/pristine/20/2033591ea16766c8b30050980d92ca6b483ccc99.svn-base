package com.gorillalogic.monkeyconsole.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;
import org.json.JSONObject;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;
import com.gorillalogic.monkeyconsole.server.RecordListener;
import com.gorillalogic.monkeytalk.Command;

public class EditorRecordListener implements RecordListener {

	IPreferenceStore prefs;
	FoneMonkeyTestEditor scriptEditor;

	public EditorRecordListener(FoneMonkeyTestEditor scriptEditor) {
		this.scriptEditor = scriptEditor;
		this.prefs = FoneMonkeyPlugin.getDefault().getPreferenceStore();
	}

	// @Override
	public void onRecord(Command command, JSONObject json) {

		if (command.getAction().equalsIgnoreCase("drag")) {
			command = convertToGesture(command);
		} else if (command.getAction().equalsIgnoreCase("tap")) {
			if (!prefs.getBoolean(PreferenceConstants.P_RECORD_TAP_COORDINATES)) {
				// Strip coordinates
				System.out.println("getting rid of the coords preference set");
				if (command.getArgs().size() == 2) {
					System.out.println("getting rid of the coords");
					command.setArgsAndModifiers("");
				}
			}
		}

		Display display = scriptEditor.getSite().getShell().getDisplay();
		final Command recordCommand = command;
		MonkeyTalkUtils.runOnGUI(new Runnable() {
			public void run() {
				scriptEditor.recordCommand(recordCommand, true);
				if (scriptEditor.getSelectedPage() instanceof ITextEditor) {
					scriptEditor.convertFromMonkeyTalk();
				}
			}
		}, display);
	}

	private Command convertToGesture(Command command) {

		if (prefs.getBoolean(PreferenceConstants.P_RECORD_MOVE)) {
			command.setAction("move");
			return command;

		} else if (prefs.getBoolean(PreferenceConstants.P_RECORD_DRAG)) {
			try {
				int x1 = Integer.parseInt(command.getArgs().get(0));
				int y1 = Integer.parseInt(command.getArgs().get(1));
				int x2 = Integer.parseInt(command.getArgs().get(command.getArgs().size() - 2));
				int y2 = Integer.parseInt(command.getArgs().get(command.getArgs().size() - 1));
				command.setArgsAndModifiers(x1 + " " + y1 + " " + x2 + " " + y2);
			} catch (NumberFormatException e) {
			}
			return command;

		} else if (prefs.getBoolean(PreferenceConstants.P_RECORD_SWIPE)) {
			try {
				int x1 = Integer.parseInt(command.getArgs().get(0));
				int y1 = Integer.parseInt(command.getArgs().get(1));
				int x2 = Integer.parseInt(command.getArgs().get(command.getArgs().size() - 2));
				int y2 = Integer.parseInt(command.getArgs().get(command.getArgs().size() - 1));
				if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) { // action is on the
																// x axis
					if (x1 > x2)
						command.setArgsAndModifiers("left");
					else
						command.setArgsAndModifiers("right");
				} else {
					if (y1 > y2)
						command.setArgsAndModifiers("up");
					else
						command.setArgsAndModifiers("down");
				}
				command.setAction("swipe");
			} catch (NumberFormatException e) {
			}
			return command;
		}
		return command;
	}
}
