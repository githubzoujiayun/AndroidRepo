package com.gorillalogic.monkeyconsole.editors;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.tableview.MonkeyTalkTabularEditor;

public class FoneMonkeyJSEditor extends
		org.eclipse.wst.jsdt.internal.ui.javaeditor.CompilationUnitEditor {

	private TextEditor textEditor;

	private MonkeyTalkTabularEditor fmc;

	public FoneMonkeyJSEditor() {
		super();
	}

	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		setPartName(editorInput.getName());

		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		
		super.init(site, editorInput);
		
	}

	@Override
	public void setFocus() {
		MonkeyTalkController controller = 
				FoneMonkeyPlugin.getDefault().getController();
		
		controller.setFrontEditor(this);
		super.setFocus();
	}

}
