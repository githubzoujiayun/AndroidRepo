package com.gorillalogic.monkeyconsole.commands;


import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.State;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.menus.TextState;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.UIElement;

import com.gorillalogic.monkeyconsole.commands.MonkeyHandlerBase;
import com.gorillalogic.monkeyconsole.plugin.MonkeyTalkImagesEnum;

/** Play all commands in the current editor
 */
public abstract class DynamicUIRadioHandler extends DropDownHandler implements IElementUpdater {

	public static final String DROP_DOWN_SELECTOR="DropDownSelector";
	public static final String DROP_DOWN_SELECTOR_SUBSTITUTE_DEFAULT="default";
	public static final String DROP_DOWN_SUBSTITUTE_STATE_ID
			="com.gorillalogic.monkeyconsole.commands.DynamicUIRadioHandler.dropdownSubstitute";
	
	// typically subclasses override these three methods:
	// maps RadioState values to icons
	protected abstract Map<String, ImageDescriptor> getIcons();
	// maps RadioState values to tooltips/menu titles
	protected abstract Map<String, String> getTooltips();
	// what to use until initialized
	protected String getDropDownSubstituteRadioValueDefault() {
		return DROP_DOWN_SELECTOR_SUBSTITUTE_DEFAULT;
	}
		
	// commandId from used in plugin.xml
	protected String getCommandId(ExecutionEvent executionEvent) {
		return executionEvent.getCommand().getId();
	}

	protected String getDropDownSelectorRadioValue() {
		return DROP_DOWN_SELECTOR;
	}

	protected DynamicUIRadioHandler() {
		super();
	}	

	protected void updateDropDownSelector(String targetRadioValue) {
		this.setDropDownSubstituteRadioValue(targetRadioValue);
		this.refreshUIElements();
		if (!this.getDropdownCommandId().equals(this.getCommandId())) {
			MonkeyHandlerBase.refreshUIElements(this.getDropdownCommandId());
		}
	}
	
	protected void updateDropDownSelector(ExecutionEvent executionEvent, String targetRadioValue) {
		updateDropDownSelector(targetRadioValue);
	}
	
	@Override
	public void updateElement(UIElement uielement, Map params) {
		String itemName = (String) params.get(RadioState.PARAMETER_ID);
		if (this.getDropDownSelectorRadioValue().equals(itemName)) {
			updateDropDownSelectorUIElement(uielement,params);
		} else {
			updateMenuUIElement(itemName, uielement,params);
		}
	}

	protected void updateDropDownSelectorUIElement(UIElement uielement, Map params) {
		DynamicUIRadioHandler.updateDropDownSelectorUIElement(
					uielement,
					params,
					this.getDropDownSubstituteRadioValue(),
					getIcons(),
					getTooltips());
	}
	
	protected static void updateDropDownSelectorUIElement(UIElement uielement, Map params, String substitute,
												Map<String,ImageDescriptor> iconMap, 
												Map<String,String> tooltipMap) {
		if (substitute!=null) {
			ImageDescriptor icon = iconMap.get(substitute);
			if (icon!=null) {
				uielement.setIcon(icon);
			} else {
				uielement.setIcon(MonkeyTalkImagesEnum.MONKEYTALK_TINY.image);
			}
			String tooltip = tooltipMap.get(substitute);
			if (tooltip!=null) {
				uielement.setTooltip(tooltip);
			} else {
				uielement.setTooltip("");
			}
		}
	}
	
	private void updateMenuUIElement(String itemName, UIElement uielement, Map params) {
		ImageDescriptor icon = getIcons().get(itemName);
		if (icon!=null) {
			uielement.setIcon(icon);
		}
		String tooltip = getTooltips().get(itemName);
		if (tooltip!=null) {
			uielement.setTooltip(tooltip);
			uielement.setText(tooltip);
		}
	}
	
	private void setDropDownSubstituteRadioValue(String sub) {
		Command myCommand = MonkeyHandlerBase.getCommand(this.getDropdownCommandId());
		State myState = myCommand.getState(DROP_DOWN_SUBSTITUTE_STATE_ID);
		myState.setValue(sub);
	}
	
	protected String getDropDownSubstituteRadioValue() {
		String dropDownSubstituteRadioValue=null;
		Command myCommand = MonkeyHandlerBase.getCommand(this.getDropdownCommandId());
		State myState = myCommand.getState(DROP_DOWN_SUBSTITUTE_STATE_ID);
		if (myState!=null) {  // could be null if menu invoked before plugin-activation
			String val = (String) myState.getValue();
			if (val != null && val.length()>0) {
				dropDownSubstituteRadioValue=val;
			}
		}
		if (dropDownSubstituteRadioValue==null || dropDownSubstituteRadioValue.length()==0) {
			return this.getDropDownSubstituteRadioValueDefault();
		}
		return dropDownSubstituteRadioValue; 
	}
	
	protected String getDropdownCommandId() {
		return getCommandId();
	}
	
	/**
	 * used when a menu or toolbar item is updated in a way that requires re-layout
	 * e.g. setting a long text on a toolbar
	 */
	protected void updateApplicationToolBar() {
		
		IMenuService menuService = (IMenuService)PlatformUI.getWorkbench().getService(IMenuService.class);
		
		// Activator.getDefault().refreshActions();
		
		Menu menu=Display.getCurrent().getMenuBar();
		if (menu!=null) {
			// System.out.println("I went to the getMenuBar() but all I got was this lousy " + menu);
		}
		
		IWorkbenchWindow icandidate = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (icandidate==null) {
			return;
		}
		if (!(icandidate instanceof ApplicationWindow)) {
			return;
		}
		ApplicationWindow applicationWindow=(ApplicationWindow)icandidate;
		IToolBarManager tbm = applicationWindow.getToolBarManager();
		if (tbm!=null) {
			tbm.update(true);
		}
		ICoolBarManager cbm = applicationWindow.getCoolBarManager();
		if (cbm!=null) {
			cbm.update(true);
		}
	}
}
