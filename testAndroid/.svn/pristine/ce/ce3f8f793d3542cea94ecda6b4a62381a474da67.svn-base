package com.gorillalogic.monkeyconsole.editors.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gorillalogic.monkeyconsole.wizard.MonkeyTalkWizardPageBase;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;

public class TimeSetupWizardPage extends MonkeyTalkWizardPageBase {

	Text thinktimeText;
	Text timeoutText;

	protected TimeSetupWizardPage(String pageName) {
		super(pageName);
		setTitle("Thinktime and Timeout Setup");
		setDescription("Please set your preferred thinktime and timeout values.");
		setHelpMessage("Thinktime is how long to wait before executing a command.\n" 
				     + "Timeout is how long to keep trying the command before giving up.\n"
				     + "Values are in milliseconds.\n"
				);
		setHelpDurationMs(6000);
	}

	@Override
	public void createControl(Composite parent) {
		Label msLabel = null;
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		setControl(composite);

		int thinktimeValue = FoneMonkeyPlugin.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_THINKTIME);
		int timeoutValue = FoneMonkeyPlugin.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_DEFAULTTIMEOUT);

		new Label(composite, SWT.NONE).setText("Thinktime");
		thinktimeText = new Text(composite, SWT.BORDER);
		thinktimeText.setText("" + thinktimeValue);
		thinktimeText.setLayoutData(new GridData(50, 15));
		msLabel = new Label(composite, SWT.NONE);
		msLabel.setText("ms");

		new Label(composite, SWT.NONE).setText("Timeout");
		timeoutText = new Text(composite, SWT.BORDER);
		timeoutText.setText("" + timeoutValue);
		timeoutText.setLayoutData(new GridData(50, 15));
		msLabel = new Label(composite, SWT.NONE);
		msLabel.setText("ms");

		System.out.println("think : " + thinktimeValue + ", out : " + timeoutValue);

		VerifyListener verifyListener = new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				String currentText = ((Text) e.widget).getText();
				String number = currentText.substring(0, e.start) + e.text
						+ currentText.substring(e.end);
				try {
					int value = Integer.valueOf(number);
				} catch (NumberFormatException ex) {
					if (!number.equals(""))
						e.doit = false;
				}

			}
		};
		thinktimeText.addVerifyListener(verifyListener);
		timeoutText.addVerifyListener(verifyListener);
	}
	
}
