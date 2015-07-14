package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ToggleScreenshotOnErrorHandler extends PreferenceToggleHandler {
	
	private static String PROP_NAME 
			= PreferenceConstants.P_TAKEERRORSCREENSHOTS;
	private static String COMMAND_ID 
			= "com.gorillalogic.monkeyconsole.commands.toggleScreenshotOnErrorCommand";

	private ToggleScreenshotOnErrorAction action=null;
	
	/**
	 * The constructor.
	 */
	public ToggleScreenshotOnErrorHandler() {
		super(PROP_NAME, COMMAND_ID);
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		boolean formerState=HandlerUtil.toggleCommandState(event.getCommand());
		boolean targetState=!formerState;
		if (action==null) {
			action = new ToggleScreenshotOnErrorAction();
		}
		action.setChecked(targetState);
		action.run();
		return null;
	}

	private class ToggleScreenshotOnErrorAction extends Action {
		public void run() {
			FoneMonkeyPlugin.getDefault().getPreferenceStore()
					.setValue(PROP_NAME, this.isChecked());
		}
	};
}
