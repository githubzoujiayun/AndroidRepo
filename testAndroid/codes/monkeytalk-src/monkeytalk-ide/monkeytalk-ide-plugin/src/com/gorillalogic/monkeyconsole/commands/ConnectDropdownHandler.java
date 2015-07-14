package com.gorillalogic.monkeyconsole.commands;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/** just the drop-down icon
 */
public class ConnectDropdownHandler extends DynamicUIRadioHandler implements IElementUpdater {

	// used in plugin.xml
	public static final String COMMAND_ID = "com.gorillalogic.monkeyconsole.commands.connectDropdownCommand"; 
	
	private static Class<? extends DynamicUIRadioHandler> dynamicUIRadioHandlerClass = ConnectToAgentHandler.class; 
	private static Class<? extends DropdownUIElementUpdater> dropdownUIElementUpdaterClass=null;
	private static DynamicUIRadioHandler dynamicUIRadioHandler = null; 

	public ConnectDropdownHandler() {
		super();
	}
	
	public static void setDropdownUIElementUpdaterClass(Class<? extends DropdownUIElementUpdater> klass) {
		ConnectDropdownHandler.dropdownUIElementUpdaterClass=klass;
	}
	public static interface DropdownUIElementUpdater {
		public void updateDropDownSelectorUIElement(UIElement uielement, Map params, String substituteRadioValue);
	}

	public static void setDynamicUIRadioHandlerClass(Class<? extends DynamicUIRadioHandler> klass) {
		dynamicUIRadioHandlerClass=klass;
		dynamicUIRadioHandler=null;
	}

	private DynamicUIRadioHandler getDynamicUIRadioHandler() {
		if (dynamicUIRadioHandler==null) {
			try {
				dynamicUIRadioHandler=dynamicUIRadioHandlerClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return dynamicUIRadioHandler;
	}
	
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
			return getDynamicUIRadioHandler().doExecute(event);
	}
	
	@Override
	protected Map<String, ImageDescriptor> getIcons() {
		return getDynamicUIRadioHandler().getIcons();
	}

	@Override
	protected Map<String, String> getTooltips() {
		return getDynamicUIRadioHandler().getTooltips();
	}
	
	// what to use until initialized
	@Override
	protected String getDropDownSubstituteRadioValueDefault() {
		return getDynamicUIRadioHandler().getDropDownSubstituteRadioValueDefault();
	}

	@Override
	protected void updateDropDownSelectorUIElement(UIElement uielement, Map params) {
		super.updateDropDownSelectorUIElement(uielement, params);
		if (ConnectDropdownHandler.dropdownUIElementUpdaterClass!=null) {
			String substituteRadioValue = this.getDropDownSubstituteRadioValue();
			DropdownUIElementUpdater updater; 
			try {
				updater = dropdownUIElementUpdaterClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("error instantiating class " + dropdownUIElementUpdaterClass.getName(),e);
			}
			updater.updateDropDownSelectorUIElement(uielement, params, substituteRadioValue);
		}
	}
}
