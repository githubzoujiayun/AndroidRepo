package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gorillalogic.monkeyconsole.editors.FoneMonkeyTestEditor;
import com.gorillalogic.monkeyconsole.tableview.MonkeyTalkTabularEditor;

abstract public class RowHandler extends MonkeyHandlerBase {

	/**
	 * The constructor.
	 */
	public RowHandler() {
		super();
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
			return null;
		}

		FoneMonkeyTestEditor foneMonkeyTestEditor = (FoneMonkeyTestEditor) targetEditor;
		MonkeyTalkTabularEditor monkeyTalkTabularEditor = foneMonkeyTestEditor.getTabularEditor();

		doRowExecute(monkeyTalkTabularEditor);

		return null;
	}

	abstract protected void doRowExecute(MonkeyTalkTabularEditor monkeyTalkTabularEditor);
}
