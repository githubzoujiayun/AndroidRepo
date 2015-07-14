package com.gorillalogic.monkeyconsole.commands;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

/** Play all commands in the current editor
 */
public class GoGorillaHandler extends MonkeyHandlerBase {
	
	/**
	 * The constructor.
	 */
	public GoGorillaHandler() {
		super();
	}
	
	// We are always available at Gorilla
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		
		URL webUrl;
		try {
			webUrl = new URL("http://www.cloudmonkeymobile.com/monkeytalk/support");
		} catch (MalformedURLException e) {
			return null;
		}
		try {
			IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport()
					.createBrowser("GorillaLogic");
			browser.openURL(webUrl);
		} catch (PartInitException e) {
			return null;
		}
		
		return null;
	}
}
