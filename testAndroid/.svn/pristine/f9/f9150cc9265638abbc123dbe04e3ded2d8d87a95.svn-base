package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;
import com.gorillalogic.monkeytalk.automators.AutomatorConstants;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ToggleMoveHandler extends PreferenceToggleHandler {
	
	private static String PROP_NAME 
			= PreferenceConstants.P_RECORD_MOVE;
	private static String COMMAND_ID 
			= "com.gorillalogic.monkeyconsole.commands.toggleMoveCommand";
	
	/**
	 * The constructor.
	 */
	public ToggleMoveHandler() {
		super(PROP_NAME, COMMAND_ID);
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		boolean formerState=HandlerUtil.toggleCommandState(event.getCommand());
		boolean targetState=!formerState;
		IPreferenceStore prefs = FoneMonkeyPlugin.getDefault().getPreferenceStore();
		
		prefs.setValue(PROP_NAME, targetState);
		FoneMonkeyPlugin.getDefault().getController().getRecordFilter()
				.set(AutomatorConstants.ACTION_MOVE, targetState);
		
		if (targetState) {
			// set our friends to false
			prefs.setValue(PreferenceConstants.P_RECORD_DRAG, false);
			prefs.setValue(PreferenceConstants.P_RECORD_SWIPE, false);
			PreferenceToggleHandler.refreshAll();
		}
		return null;
	}

}
