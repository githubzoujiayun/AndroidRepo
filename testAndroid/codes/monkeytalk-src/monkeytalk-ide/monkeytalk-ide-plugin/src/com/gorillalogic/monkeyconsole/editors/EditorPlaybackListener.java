package com.gorillalogic.monkeyconsole.editors;

import org.eclipse.swt.widgets.Display;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.sender.Response;
import com.gorillalogic.monkeytalk.sender.Response.ResponseStatus;

public class EditorPlaybackListener implements PlaybackListener {
	
	private FoneMonkeyTestEditor scriptEditor;
	private int playFrom = 0;
	private int playTo = -1;

	/**
	 * Play the entire script
	 * @param scriptEditor
	 */
	public EditorPlaybackListener(FoneMonkeyTestEditor scriptEditor) {
		this.scriptEditor = scriptEditor;
	}		

	/**
	 * Play from/to
	 * @param scriptEditor
	 */
	public EditorPlaybackListener(FoneMonkeyTestEditor scriptEditor, int playFrom, int playTo) {
		this.scriptEditor = scriptEditor;
		this.playFrom = playFrom;
		this.playTo = playTo;
	}		

	@Override
	public void onStart(final Scope scope) {
		if (playTo == -1) {
			playTo = scriptEditor.getCommands().size();
		}

		MonkeyTalkUtils.runOnGUI(new Runnable() {
			public void run() {
				if (playFrom != playTo) {
					int selectionNdx = playFrom + scope.getCurrentIndex() - 1;
					if (selectionNdx < scriptEditor.getCommands().size()) {
						scriptEditor.getTabularEditor().setSelection(selectionNdx);
					}
				}
			}
		}, getDisplay());
	}

	@Override
	public void onScriptStart(Scope scope) {
	}

	@Override
	public void onScriptComplete(Scope scope, PlaybackResult r) {
	}

	@Override
	public void onComplete(final Scope scope, final Response response) {
		// long elapsed = System.currentTimeMillis() -
		// replayCommandStartTime;

		if (response.getStatus() == ResponseStatus.FAILURE || response.getStatus() == ResponseStatus.ERROR) {
			MonkeyTalkUtils.runOnGUI(new Runnable() {
				public void run() {
					int selectionNdx = playFrom + scope.getCurrentIndex() - 1;
					if (selectionNdx < scriptEditor.getCommands().size()) {
						scriptEditor.getTabularEditor().setSelection(selectionNdx);
						scriptEditor.getTabularEditor().markRowAsError(selectionNdx);
					}
				}
			}, getDisplay());

		}
	}

	@Override
	public void onPrint(String message) {
	}
	
	private Display getDisplay() {
		return scriptEditor.getSite().getShell().getDisplay();
	}
		
}
