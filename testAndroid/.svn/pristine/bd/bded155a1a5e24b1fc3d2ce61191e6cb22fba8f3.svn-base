package com.gorillalogic.monkeyconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;

public class PreferenceToggleHandler extends MonkeyHandlerBase {
	private static List<PreferenceToggleHandler> allToggles 
			= new ArrayList<PreferenceToggleHandler>();
	
	protected String propName = null;
	protected String commandId = null;
	
	protected PreferenceToggleHandler(String propName, String commandId) {
		this.propName = propName;  
		this.commandId = commandId;  
		allToggles.add(this);
	}
	
	public void refreshFromPreference() {
		refreshFromPreference(commandId, propName);
	}
	
	public static void refreshFromPreference(String commandID, String propKey) {
	    ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);  
	    Command command = commandService.getCommand(commandID);  

	    State state = command.getState(RegistryToggleState.STATE_ID);
	    if (FoneMonkeyPlugin.getDefault().getPreferenceStore().getBoolean(propKey)) {
	        state.setValue(Boolean.TRUE);
	    } else {
	        state.setValue(Boolean.FALSE);
	    }
	}
	
	public static void refreshAll() {
		for (PreferenceToggleHandler handler : allToggles) {
			handler.refreshFromPreference();
		}
	}
}
