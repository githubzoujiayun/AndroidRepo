package com.gorillalogic.monkeyconsole.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;

public abstract class MonkeyTalkWizardPageBase extends WizardPage {

	private String helpMessage = "Sorry, help is not available.";
	private int helpDurationMs = 4000;
	private ToolTip helpTip = null;
	
	protected MonkeyTalkWizardPageBase(String pageName) {
		super(pageName);
	}

	public void setHelpMessage(String s) {
		helpMessage = s;
	}
	
	public void setHelpDurationMs(int milliseconds) {
		helpDurationMs = milliseconds;
	}
	
	@Override
	public void performHelp() {
		super.performHelp();
		
		if (helpTip == null) {
			 helpTip = new ToolTip(getShell(), SWT.ICON_INFORMATION);
		}
		helpTip.setMessage(helpMessage);
		helpTip.setVisible(true);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(helpDurationMs);
				} catch (InterruptedException e) {
				}
				final Shell shell = getShell();
				// e.g., window could have closed
				if (shell!=null) {
					MonkeyTalkUtils.runOnGUI(new Runnable() {
						public void run() {
							helpTip.setVisible(false);				
						}
					}, true, shell.getDisplay());
				}
			}			
		}).start();
	}	
}
