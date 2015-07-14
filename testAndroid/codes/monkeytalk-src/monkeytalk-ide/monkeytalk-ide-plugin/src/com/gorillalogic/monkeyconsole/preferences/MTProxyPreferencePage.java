package com.gorillalogic.monkeyconsole.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.editors.utils.CloudServiceException;
import com.gorillalogic.monkeyconsole.editors.utils.CloudServices;

public class MTProxyPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button cbUseProxy;
	private Button cbUseProxyAuthentication;
	
	private Text proxyHost;
	private Text proxyPort;
	private Text proxyUsername;
	private Text proxyPassword;

	// Creates a GridLayout with a specific number of columns for using with each Composite
	private GridLayout createDefaultGridLayout(int columns) 
	{
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;

		return layout;
	}

	@Override
	protected Control createContents(Composite parent) {
		// Creates a 2 columns horizontal span GridData for that object that requires this attribute
		GridData gdTwoHorinzontalSpan = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gdTwoHorinzontalSpan.horizontalSpan = 2;
		
		// Creates a listener for enabling or disabling fields according to checkboxes status.
		SelectionListener listener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkState();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {				
			}
		};
		
		// PROXY NETWORK PREFS

		Group proxyPreferencesGroup = new Group(parent, SWT.NONE);
		proxyPreferencesGroup.setLayout(createDefaultGridLayout(2));
		
		cbUseProxy = new Button(proxyPreferencesGroup, SWT.CHECK);
		cbUseProxy.setText("Use proxy");
		cbUseProxy.addSelectionListener(listener);
		cbUseProxy.setLayoutData(gdTwoHorinzontalSpan);
		
		// Proxy Host
		Label proxyHostLabel = new Label(proxyPreferencesGroup, SWT.NONE);
		proxyHostLabel.setText("Proxy Hostname:");
		proxyHostLabel.setToolTipText("The hostname or IP address for the proxy server.");

		proxyHost = new Text(proxyPreferencesGroup, SWT.BORDER);
		proxyHost.addVerifyListener(new DomainNameVerifyListener());
		
		GridData gdProxyHost = new GridData(400, 15);
		proxyHost.setLayoutData(gdProxyHost);
				
		// Proxy Port
		Label proxyPortLabel = new Label(proxyPreferencesGroup, SWT.NONE);
		proxyPortLabel.setText("Proxy Port:");
		proxyPortLabel.setToolTipText("Proxy port number");
		
		proxyPort = new Text(proxyPreferencesGroup, SWT.BORDER);
		proxyPort.addVerifyListener(new NetworkPortVerifyListener());
		
		GridData gdProxyPort = new GridData(75, 15);
		proxyPort.setLayoutData(gdProxyPort);
		proxyPort.addVerifyListener(new NetworkPortVerifyListener());
		
		cbUseProxyAuthentication = new Button(proxyPreferencesGroup, SWT.CHECK);
		cbUseProxyAuthentication.setText("Proxy server requires password");
		cbUseProxyAuthentication.addSelectionListener(listener);
		cbUseProxyAuthentication.setLayoutData(gdTwoHorinzontalSpan);
		
		// Proxy Username
		Label proxyUsernameLabel = new Label(proxyPreferencesGroup, SWT.NONE);
		proxyUsernameLabel.setText("Proxy Username:");
		proxyUsernameLabel.setToolTipText("The username for the authenticated proxy server.");
		
		proxyUsername = new Text(proxyPreferencesGroup, SWT.BORDER);
		
		GridData gdProxyUsername = new GridData(200, 15);
		proxyUsername.setLayoutData(gdProxyUsername);
		
		// Proxy Password
		Label proxyPasswordLabel = new Label(proxyPreferencesGroup, SWT.NONE);
		proxyPasswordLabel.setText("Proxy Password:");
		proxyPasswordLabel.setToolTipText("The password for the authenticated proxy server.");
		
		proxyPassword = new Text(proxyPreferencesGroup, SWT.BORDER | SWT.PASSWORD);
		
		GridData gdProxyPassword = new GridData(200, 15);
		proxyPassword.setLayoutData(gdProxyPassword);
		
		// END PROXY NETWORK PREFS		return null;
		
		
		
		setDataToControls();
		checkState();
		return parent;
	}
	
	protected void checkState() {
		setErrorMessage(null);
		setValid(true);

		proxyPort.setEnabled(cbUseProxy.getSelection());
		proxyHost.setEnabled(cbUseProxy.getSelection());

		proxyUsername.setEnabled(cbUseProxy.getSelection() && cbUseProxyAuthentication.getSelection());
		proxyPassword.setEnabled(cbUseProxy.getSelection() && cbUseProxyAuthentication.getSelection());
	}
	
	protected void setDataToControls() {
		if(getPreferenceStore().getBoolean(PreferenceConstants.P_USE_PROXY)){
			cbUseProxy.setSelection(true);
		} else {
			cbUseProxy.setSelection(false);
		}
		
		proxyHost.setText(getPreferenceStore().getString(PreferenceConstants.P_PROXY_HOST));
		proxyPort.setText(getPreferenceStore().getString(PreferenceConstants.P_PROXY_PORT));
		
		if(getPreferenceStore().getBoolean(PreferenceConstants.P_USE_PROXY_AUTHENTICATION))
			cbUseProxyAuthentication.setSelection(true);
		else
			cbUseProxyAuthentication.setSelection(false);
		
		proxyUsername.setText(getPreferenceStore().getString(PreferenceConstants.P_PROXY_USERNAME));
		proxyPassword.setText(getPreferenceStore().getString(PreferenceConstants.P_PROXY_PASSWORD));
	}
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		getPreferenceStore().setToDefault(PreferenceConstants.P_USE_PROXY);
		getPreferenceStore().setToDefault(PreferenceConstants.P_PROXY_HOST);
		getPreferenceStore().setToDefault(PreferenceConstants.P_PROXY_PORT);
		getPreferenceStore().setToDefault(PreferenceConstants.P_USE_PROXY_AUTHENTICATION);
		getPreferenceStore().setToDefault(PreferenceConstants.P_PROXY_USERNAME);
		getPreferenceStore().setToDefault(PreferenceConstants.P_PROXY_PASSWORD);
		setDataToControls();
		checkState();
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(FoneMonkeyPlugin.getDefault().getPreferenceStore());
	}

	public void applySettings() {		
		if(cbUseProxy.getSelection()){
			getPreferenceStore().setValue(PreferenceConstants.P_USE_PROXY,  true);
		} else {
			getPreferenceStore().setValue(PreferenceConstants.P_USE_PROXY,  false);
		}
		
		getPreferenceStore().setValue(PreferenceConstants.P_PROXY_HOST, proxyHost.getText());
		setNetworkPortFromText(proxyPort, PreferenceConstants.P_PROXY_PORT);
		
		if(cbUseProxyAuthentication.getSelection()){
			getPreferenceStore().setValue(PreferenceConstants.P_USE_PROXY_AUTHENTICATION,  true);
		} else {
			getPreferenceStore().setValue(PreferenceConstants.P_USE_PROXY_AUTHENTICATION,  false);
		}
		getPreferenceStore().setValue(PreferenceConstants.P_PROXY_USERNAME, proxyUsername.getText());
		getPreferenceStore().setValue(PreferenceConstants.P_PROXY_PASSWORD, proxyPassword.getText());
	}
	
	@Override
	public void performApply() {
		applySettings();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		applySettings();
		return super.performOk();
	}

	// Shortcut for setIntegerPrefFromText. Cleans up the performOk function.
	private void setNetworkPortFromText(Text control, String propName) {
		setIntegerPrefFromText(control, propName, 1, 65535);
	}
	
	// Validates an Integer from a string. Min and Max used for validation of network port.
	private void setIntegerPrefFromText(Text control, String propName, int min, int max) {
		int port = -1;
		try {
			String s = control.getText();
			if (s != null) {
				port = Integer.parseInt(s);
			}
		} catch (NumberFormatException nfe) {
			port = -1;
		}
		if (port >= min && port <= max) {
			getPreferenceStore().setValue(propName, port);
		}
	}
	
	private static class CharFilterVerifyListener implements VerifyListener {
		int minlength;
		int maxlength;
		String validchars;

		CharFilterVerifyListener(String validchars, int minlength, int maxlength) {
			this.validchars = validchars;
			this.maxlength = maxlength;
			this.minlength = minlength;
		}

		@Override
		public void verifyText(VerifyEvent evt) {
			String txt = evt.text;
			if (txt == null) {
				evt.doit = false;
				return;
			}
			if (txt.length() < minlength) {
				evt.doit = false;
				return;
			}
			if (txt.length() > maxlength) {
				evt.doit = false;
				return;
			}
			for (int i = 0; i < txt.length(); i++) {
				if (validchars.indexOf(txt.charAt(i)) == -1) {
					evt.doit = false;
					break;
				}
			}
		}
	}

	private static class DomainNameVerifyListener extends CharFilterVerifyListener {
		DomainNameVerifyListener() {
			super("01234567890-.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", 0, 63);
		}
	}

	private static class NetworkPortVerifyListener extends CharFilterVerifyListener {
		NetworkPortVerifyListener() {
			super("01234567890", 0, 5);
		}

		@Override
		public void verifyText(VerifyEvent evt) {
			super.verifyText(evt);
			if (!evt.doit) {
				return;
			}
			Text control = (Text) evt.getSource();
			String oldtxt = control.getText();
			String newtxt = oldtxt.substring(0, evt.start) + evt.text + oldtxt.substring(evt.end);
			if (newtxt.length() == 0) {
				return;
			}
			int val = -1;
			try {
				val = Integer.parseInt(newtxt);
			} catch (NumberFormatException nfe) {
				evt.doit = false;
				return;
			}
			if (val < 1 || val > 65535) {
				evt.doit = false;
				return;
			}
		}
	}
}
