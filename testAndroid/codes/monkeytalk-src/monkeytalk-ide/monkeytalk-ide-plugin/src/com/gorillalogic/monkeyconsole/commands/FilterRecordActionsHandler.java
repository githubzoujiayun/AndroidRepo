package com.gorillalogic.monkeyconsole.commands;

public class FilterRecordActionsHandler extends DropDownHandler {
	public FilterRecordActionsHandler() {
		this.setBaseEnabled(true);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

}
