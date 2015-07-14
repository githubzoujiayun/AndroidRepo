/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2012 Gorilla Logic, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.gorillalogic.monkeyconsole.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
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

import com.gorillalogic.monkeyconsole.editors.utils.CloudServiceException;
import com.gorillalogic.monkeyconsole.editors.utils.CloudServices;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;

/**
 * <p>
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <code>FieldEditorPreferencePage</code>, we can use the field support built into JFace
 * that allows us to create a page that is small and knows how to save, restore and apply itself.
 * </p>
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 * </p>
 */
public class CloudMonkeyLabManagerPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button rbHTTP;
	private Button rbHTTPS;

	private Text cloudUsername;
	private Text cloudPassword;

	private Text controllerHost;
	private Text controllerPort;
	private Text controllerSslPort;


	
	// Creates a GridLayout with a specific number of columns for using with each Composite
	private GridLayout createDefaultGridLayout(int columns) 
	{
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;

		return layout;
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
	 * manipulate various types of preferences. Each field editor knows how to save and restore
	 * itself.
	 */
	@Override
	protected Control createContents(Composite parent) {	
		// Creates a 2 columns horizontal span GridData for that object that requires this attribute
		GridData gdTwoHorinzontalSpan = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gdTwoHorinzontalSpan.horizontalSpan = 2;
		
		//Creates a general GridData sized for the main labels
		GridData gdGeneralLabel = new GridData(210,15);
		
		// CLOUD PREFERENCES
		
		Composite cmCloudPreferences = new Composite(parent, SWT.NONE);
		cmCloudPreferences.setLayout(createDefaultGridLayout(2));

		// / CLOUD CONTROLLER NETWORK PREFS
		Label controllerHostLabel = new Label(cmCloudPreferences, SWT.NONE);
		controllerHostLabel.setText("CloudMonkey LabManager Hostname:");
		controllerHostLabel.setToolTipText("The hostname or IP address for the cloud server.");
		controllerHostLabel.setLayoutData(gdGeneralLabel);

		controllerHost = new Text(cmCloudPreferences, SWT.BORDER);
		GridData gdControllerHost = new GridData(300,15);
		controllerHost.setLayoutData(gdControllerHost);
		controllerHost.addVerifyListener(new DomainNameVerifyListener());

		Label controllerPortLabel = new Label(cmCloudPreferences, SWT.NONE);
		controllerPortLabel.setText("CloudMonkey LabManager Port:");
		controllerPortLabel.setToolTipText("The port on which the cloud server will listen for normal HTTP connections. Defaults to 8080.");
		controllerPortLabel.setLayoutData(gdGeneralLabel);
		
		controllerPort = new Text(cmCloudPreferences, SWT.BORDER);
		GridData gdControllerPort = new GridData(75, 15);
		controllerPort.setLayoutData(gdControllerPort);
		controllerPort.addVerifyListener(new NetworkPortVerifyListener());

		Label controllerSSLPortLabel = new Label(cmCloudPreferences, SWT.NONE);
		controllerSSLPortLabel.setText("CloudMonkey LabManager SSL Port:");
		controllerSSLPortLabel.setToolTipText("The port on which the cloud server will listen for secure (encrypted) HTTP connections. Defaults to 4430.");
		controllerSSLPortLabel.setLayoutData(gdGeneralLabel);
		
		controllerSslPort = new Text(cmCloudPreferences, SWT.BORDER);
		GridData gdControllerSslPort = new GridData(75, 15);
		controllerSslPort.setLayoutData(gdControllerSslPort);
		controllerSslPort.addVerifyListener(new NetworkPortVerifyListener());
		
		Composite radioButtonGroup = new Group(parent, SWT.NONE);
		radioButtonGroup.setLayout(createDefaultGridLayout(3));

		Label protocolLabel = new Label(radioButtonGroup, SWT.NONE);
		protocolLabel.setText("Protocol:");
		
		rbHTTP = new Button(radioButtonGroup, SWT.RADIO);
		rbHTTPS = new Button(radioButtonGroup, SWT.RADIO);

		rbHTTP.setText("HTTP");
		rbHTTPS.setText("HTTPS");

		// END CLOUD CONTROLLER NETWORK PREFS

		// CLOUD LOGIN INFO
		
		Label cloudAuthenticationLabel = new Label(cmCloudPreferences, SWT.NONE);
		cloudAuthenticationLabel.setText("To access the CloudMonkey LabManager, please enter your username and password.");
		cloudAuthenticationLabel.setLayoutData(gdTwoHorinzontalSpan);
		
		Label cloudUsernameLabel = new Label(cmCloudPreferences, SWT.NONE);
		cloudUsernameLabel.setText("Username:");
		cloudUsername = new Text(cmCloudPreferences, SWT.BORDER);

		GridData gdCloudUsername = new GridData(200, 15);
		cloudUsername.setLayoutData(gdCloudUsername);

		Label cloudPasswordLabel = new Label(cmCloudPreferences, SWT.NONE);
		cloudPasswordLabel.setText("Password:");
		cloudPassword = new Text(cmCloudPreferences, SWT.BORDER | SWT.PASSWORD);

		GridData gdCloudPassword = new GridData(200, 15);
		cloudPassword.setLayoutData(gdCloudPassword);

		Button verifyLogin = new Button(cmCloudPreferences, SWT.NONE);
		verifyLogin.setText("Verify Login");
		verifyLogin.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				setErrorMessage(null);
				setMessage("sending login request");
				
				String errorMessage = null;
				
				// verify HTTP login
				try {
					boolean isHttps = false;
					verifyLogin(isHttps);
				} catch (CloudServiceException ex) {
					if(ex.getMessage() != null && !ex.getMessage().isEmpty())
					{
						errorMessage = ex.getMessage();
					}
					else
					{
						errorMessage = "Connection error - " + ex.getClass().getSimpleName();
					}
				}

				// verify HTTPS login
				if(errorMessage == null)
				{
					try {
						boolean isHttps = true;
						verifyLogin(isHttps);
					} catch (CloudServiceException ex) {
						errorMessage = "Connection error - bad SSL port";
					}
				}
				
				if (errorMessage == null) {
					setMessage("Username and password verified");
					setErrorMessage(null);
				} else {
					setMessage("CloudMonkey LabManager Preferences");
					setErrorMessage(errorMessage);
				}
			}
		});

		// END CLOUD LOGIN INFO

		
		setDataToControls();
		
		return parent;
	}


	
	protected void setDataToControls() {
		controllerHost.setText(getPreferenceStore().getString(PreferenceConstants.P_CONTROLLER_HOST));
		controllerPort.setText(getPreferenceStore().getString(PreferenceConstants.P_CONTROLLER_PORT));
		controllerSslPort.setText(getPreferenceStore().getString(PreferenceConstants.P_CONTROLLER_SSL_PORT));
		if(getPreferenceStore().getString(PreferenceConstants.P_CONTROLLER_PROTOCOL)=="http") {
			rbHTTP.setSelection(true);
			rbHTTPS.setSelection(false);
		} else {
			rbHTTPS.setSelection(true);
			rbHTTP.setSelection(false);
		}
		cloudUsername.setText(getPreferenceStore().getString(PreferenceConstants.P_CLOUDUSR));
		cloudPassword.setText(getPreferenceStore().getString(PreferenceConstants.P_CLOUDPASS));
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		getPreferenceStore().setToDefault(PreferenceConstants.P_CONTROLLER_HOST);
		getPreferenceStore().setToDefault(PreferenceConstants.P_CONTROLLER_PORT);
		getPreferenceStore().setToDefault(PreferenceConstants.P_CONTROLLER_SSL_PORT);
		getPreferenceStore().setToDefault(PreferenceConstants.P_CONTROLLER_PROTOCOL);
		getPreferenceStore().setToDefault(PreferenceConstants.P_CLOUDUSR);
		getPreferenceStore().setToDefault(PreferenceConstants.P_CLOUDPASS);

		setDataToControls();
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(FoneMonkeyPlugin.getDefault().getPreferenceStore());
	}

	public void applySettings() {
		boolean isSsl = rbHTTP.getSelection() ? false : true;
		applySettings(isSsl);
	}
	
	private void applySettings(boolean isSsl)
	{
		getPreferenceStore().setValue(PreferenceConstants.P_CONTROLLER_HOST, controllerHost.getText());
		setNetworkPortFromText(controllerPort, PreferenceConstants.P_CONTROLLER_PORT);
		setNetworkPortFromText(controllerSslPort, PreferenceConstants.P_CONTROLLER_SSL_PORT);
		
		if(isSsl)
			getPreferenceStore().setValue(PreferenceConstants.P_CONTROLLER_PROTOCOL,  "https");
		else
			getPreferenceStore().setValue(PreferenceConstants.P_CONTROLLER_PROTOCOL,  "http");
		
		getPreferenceStore().setValue(PreferenceConstants.P_CLOUDUSR, cloudUsername.getText());
		getPreferenceStore().setValue(PreferenceConstants.P_CLOUDPASS, cloudPassword.getText());
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
	
	private void verifyLogin(boolean isSsl) throws CloudServiceException
	{
		// Apply the settings first, otherwise a rewrite of the networking code is
		// needed.
		applySettings(isSsl);
		
		String token = CloudServices.login(cloudUsername.getText(),
				cloudPassword.getText());
		if (token == null || token.length() == 0) {
			String protocol = isSsl ? "HTTPS" : "HTTP";
			throw new CloudServiceException(
					"Invalid " + protocol + " Connection prefs - hostname, ports, or proxy");
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
