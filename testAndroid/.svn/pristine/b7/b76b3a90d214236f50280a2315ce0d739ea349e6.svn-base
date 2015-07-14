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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.gorillalogic.monkeyconsole.ADBHelper;
import com.gorillalogic.monkeyconsole.editors.utils.CloudServices;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeytalk.utils.AndroidUtils;

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
public class FonemonkeyPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	DirectoryFieldEditor androidSdkEditor;

	BooleanFieldEditor includeAndroidEditor;
	BooleanFieldEditor logEventConsent;
	BooleanFieldEditor takeAfterScreenshots;
	BooleanFieldEditor takeAfterMetrics;
	BooleanFieldEditor takeOnErrorScreenshot;
	
	Text thinktimeText;
	Text timeoutText;
	Text componentTreeTimeoutText;

	boolean wasOptIn = true;

	public FonemonkeyPreferencePage() {
		super(GRID);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
	 * manipulate various types of preferences. Each field editor knows how to save and restore
	 * itself.
	 */
	public void createFieldEditors() {

		// GENERAL PREFERENCES

		// Test on iOS
		addField(new BooleanFieldEditor(PreferenceConstants.P_INCLUDEIOS, "Test on iOS",
				getFieldEditorParent()));

		// Test on Android
		// Enables or disables the directory search for the Android SDK
		includeAndroidEditor = new BooleanFieldEditor(PreferenceConstants.P_INCLUDEANDROID,
				"Test on Android", getFieldEditorParent());
		addField(includeAndroidEditor);

		// Directory field for Android SDK Location
		androidSdkEditor = new DirectoryFieldEditor(PreferenceConstants.P_ANDROIDHOME,
				"Android SDK:", this.getFieldEditorParent());
		addField(androidSdkEditor);

		// digits only verifier
		VerifyListener verifyListener = new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				String currentText = ((Text) e.widget).getText();
				String number = currentText.substring(0, e.start) + e.text
						+ currentText.substring(e.end);
				try {
					int value = Integer.valueOf(number);
				} catch (NumberFormatException ex) {
					if (!number.equals("")) {
						e.doit = false;
					}
				}
			}
		};

		// Thinktime
		Label thinktimeLabel = new Label(this.getFieldEditorParent(), SWT.NONE);
		thinktimeLabel.setText("Default Thinktime:");
		thinktimeLabel.setToolTipText("Default thinktime number");

		Composite cp = new Composite(this.getFieldEditorParent(), SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		cp.setLayout(gl);

		thinktimeText = new Text(cp, SWT.BORDER);
		thinktimeText.setText(getPreferenceStore().getString(PreferenceConstants.P_THINKTIME));
		thinktimeText.addVerifyListener(verifyListener);
		GridData gdThinkTime = new GridData(50, 15);
		thinktimeText.setLayoutData(gdThinkTime);
		
		Label msLabel = null;
		msLabel = new Label(cp, SWT.NONE);
		msLabel.setText("ms");

		Label spacer = new Label(this.getFieldEditorParent(), SWT.NONE);
		
		// Timeout
		Label timeoutLabel = new Label(this.getFieldEditorParent(), SWT.NONE);
		timeoutLabel.setText("Default Timeout:");
		timeoutLabel.setToolTipText("Default timeout in milliseconds");

		cp = new Composite(this.getFieldEditorParent(), SWT.NONE);
		gl = new GridLayout();
		gl.numColumns = 2;
		cp.setLayout(gl);

		timeoutText = new Text(cp, SWT.BORDER);
		timeoutText.setText(getPreferenceStore().getString(PreferenceConstants.P_DEFAULTTIMEOUT));
		timeoutText.addVerifyListener(verifyListener);
		GridData gdTimeout = new GridData(50, 15);
		timeoutText.setLayoutData(gdTimeout);

		Label timeoutMsLabel = null;
		timeoutMsLabel = new Label(cp, SWT.NONE);
		timeoutMsLabel.setText("ms");
		
		spacer = new Label(this.getFieldEditorParent(), SWT.NONE);
		
		// Component Tree Timeout 
		Label componentTreeTimeoutLabel = new Label(this.getFieldEditorParent(), SWT.NONE);
		componentTreeTimeoutLabel.setText("Component Tree timeout:");
		componentTreeTimeoutLabel.setToolTipText("max time for component tree");

		cp = new Composite(this.getFieldEditorParent(), SWT.NONE);
		gl = new GridLayout();
		gl.numColumns = 2;
		cp.setLayout(gl);

	    componentTreeTimeoutText = new Text(cp, SWT.BORDER);
	    componentTreeTimeoutText.setText(getPreferenceStore().getString(PreferenceConstants.P_COMPONENTTREETIMEOUT));
	    componentTreeTimeoutText.addVerifyListener(verifyListener);
		GridData gdComponentTreeTimeout = new GridData(50, 15);
		componentTreeTimeoutText.setLayoutData(gdComponentTreeTimeout);

		Label componentTreeTimeoutMsLabel = null;
		componentTreeTimeoutMsLabel = new Label(cp, SWT.NONE);
		componentTreeTimeoutMsLabel.setText("ms");
		
		spacer = new Label(this.getFieldEditorParent(), SWT.NONE);
		
		// Take screenshots after each command
		takeAfterScreenshots = new BooleanFieldEditor(PreferenceConstants.P_TAKEAFTERSCREENSHOTS,
				"Take screenshot after each command", getFieldEditorParent());
		takeAfterScreenshots.getDescriptionControl(getFieldEditorParent()).setToolTipText(
				"Take a screenshot after every command is executed");
		addField(takeAfterScreenshots);

		// Take metrics after each command
		takeAfterMetrics = new BooleanFieldEditor(PreferenceConstants.P_TAKEAFTERMETRICS,
				"Take metrics after each command", getFieldEditorParent());
		takeAfterMetrics.getDescriptionControl(getFieldEditorParent()).setToolTipText(
				"Take metrics after every command is executed");
		addField(takeAfterMetrics);

		// Take screenshots on error
		takeOnErrorScreenshot = new BooleanFieldEditor(PreferenceConstants.P_TAKEERRORSCREENSHOTS,
				"Take screenshot on error", getFieldEditorParent());
		takeOnErrorScreenshot.getDescriptionControl(getFieldEditorParent()).setToolTipText(
				"Take screenshot when an error happened");
		addField(takeOnErrorScreenshot);

		// END GENERAL PREFERENCES

		// LOGGING PREFERENCES

		if (FoneMonkeyPlugin.getDefault().getPreferenceStore()
				.contains(PreferenceConstants.P_LOGEVENTCONSENT)) {
			wasOptIn = FoneMonkeyPlugin.getDefault().getPreferenceStore()
					.getBoolean(PreferenceConstants.P_LOGEVENTCONSENT);
		}
		logEventConsent = new BooleanFieldEditor(PreferenceConstants.P_LOGEVENTCONSENT,
				"Upload Usage Information", getFieldEditorParent());
		logEventConsent
				.getDescriptionControl(getFieldEditorParent())
				.setToolTipText(
						"Can we send some information to the MonkeyTalk dev team about which features are being used and whether they are working?");
		addField(logEventConsent);

		// END LOGGING PREFERENCES

		checkState();

	}

	@Override
	protected void checkState() {
		setErrorMessage(null);
		setValid(true);

		super.checkState();

		androidSdkEditor.setEnabled(includeAndroidEditor.getBooleanValue(), getFieldEditorParent());

		if (includeAndroidEditor.getBooleanValue()) {
			String sdkPath =  androidSdkEditor.getStringValue();
			String msg;
			if (sdkPath.isEmpty()) {
				msg = "MonkeyTalk needs to use your Android SDK, please set it or uncheck \"Test on Android\"";
			} else {
				msg = ADBHelper.validateAndroidSdkPath(androidSdkEditor.getStringValue());
			}
			setErrorMessage(msg);
			setValid(msg == null);
		} else {
			setErrorMessage(null);
			setValid(true);
		}
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		checkState();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);

		if (event.getProperty().equals(FieldEditor.VALUE)) {
			checkState();
		}
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(FoneMonkeyPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = FoneMonkeyPlugin.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.P_THINKTIME, thinktimeText.getText());
		store.setValue(PreferenceConstants.P_DEFAULTTIMEOUT, timeoutText.getText());
		store.setValue(PreferenceConstants.P_COMPONENTTREETIMEOUT, componentTreeTimeoutText.getText());
		if (wasOptIn) {
			if (this.logEventConsent.getBooleanValue() == false) {
				CloudServices.optOutAsync();
			}
		} else {
			if (this.logEventConsent.getBooleanValue() == true) {
				CloudServices.optInAsync();
			}
		}

		// We set the custom sdk path to access it when required
		AndroidUtils.setSdkPath(androidSdkEditor.getStringValue());
		
		return super.performOk();
	}
}