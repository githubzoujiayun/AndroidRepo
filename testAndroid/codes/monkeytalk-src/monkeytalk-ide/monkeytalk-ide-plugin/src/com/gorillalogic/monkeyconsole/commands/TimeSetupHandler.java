package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gorillalogic.monkeyconsole.editors.utils.TimeSetupWizard;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TimeSetupHandler extends MonkeyHandlerBase {
	
	private TimeSetupAction action=null;
	
	/**
	 * The constructor.
	 */
	public TimeSetupHandler() {}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		if (action==null) {
			action = new TimeSetupAction();
		}
		action.setShell(shell);
		action.run();
		return null;
	}

	// This action setup the thinktime and timeout
	private class TimeSetupAction extends Action {
		Shell shell;
		public void setShell(Shell shell) {this.shell=shell;}
		public void run() {
			TimeSetupWizard wizard = new TimeSetupWizard();
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.open();
		}
	};
}
