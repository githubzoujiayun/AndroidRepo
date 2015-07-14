package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.plugin.MonkeyTalkImagesEnum;

/** Play all commands in the current editor
 */
public class ClearAllHandler extends MonkeyHandlerBase {
	
	/**
	 * The constructor.
	 */
	public ClearAllHandler() {
		super();
	}
	
	@Override
	public boolean isEnabled() {
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		String extension = controller.getExtention();
		
		return "mt".equals(extension) 
				|| "mts".equals(extension);
	}
	
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		
		IEditorPart targetEditor;
		try {
			targetEditor = HandlerUtil.getActiveEditorChecked(event);
		} catch (ExecutionException e) {
			targetEditor = null;
		}
		
		if (targetEditor==null) {
			MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Clear All");
			String message = "Cannot clear all, there does not seem to be any active editor";
			dialog.setMessage(message);
			dialog.open();
			return null;
		}
		
		if (isClearable(targetEditor)) {
			MessageDialog dlg = new MessageDialog(HandlerUtil.getActiveShell(event),
					"Delete All?", 
					MonkeyTalkImagesEnum.CLEAR.image.createImage(),
					"Are you sure you would like to delete all items?", 
					MessageDialog.WARNING,
					new String[] { "Cancel", "OK" }, 1);
			if (dlg.open() == 1) {
				FoneMonkeyPlugin.getDefault().getController().clear();
			}
		} else {
			MessageBox dialog = new MessageBox(HandlerUtil.getActiveShell(event), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Clear All");
			String message = "Cannot clear all, only used for scripts or suites";
			dialog.setMessage(message);
			dialog.open();
			return null;
		}
		
		return null;
	}
	
	protected boolean isClearable(IEditorPart targetEditor) {
		return MonkeyTalkUtils.isScript(targetEditor) || MonkeyTalkUtils.isSuite(targetEditor);
	}
	
}
