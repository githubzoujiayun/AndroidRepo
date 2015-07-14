package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShowComponentTreeHandler extends MonkeyHandlerBase {
		
	/**
	 * The constructor.
	 */
	public ShowComponentTreeHandler() {}
	
	@Override
	public boolean isEnabled() {
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		if (controller!=null && controller.isCurrentlyConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		try {
			FoneMonkeyPlugin.getDefault().getController().fetchAndShowComponentTree();
		} catch (Exception e) {
			MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Show Component Tree");
			dialog.setMessage(e.getMessage());
			dialog.open();
		}
		return null;
	}

}
