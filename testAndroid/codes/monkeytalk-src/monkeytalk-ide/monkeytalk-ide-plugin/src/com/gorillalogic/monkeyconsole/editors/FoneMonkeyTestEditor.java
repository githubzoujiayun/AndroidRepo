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
package com.gorillalogic.monkeyconsole.editors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.gorillalogic.monkeyconsole.editors.utils.FoneMonkeyConsoleHelper;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.server.RecordListener;
import com.gorillalogic.monkeyconsole.tableview.MonkeyTalkTabularEditor;
import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.CommandValidator;
import com.gorillalogic.monkeytalk.CommandValidator.CommandStatus;
import com.gorillalogic.monkeytalk.CommandWorld;
import com.gorillalogic.monkeytalk.api.js.tools.JSLibGenerator;
import com.gorillalogic.monkeytalk.api.js.tools.JSMTGenerator;
import com.gorillalogic.monkeytalk.api.meta.API;
import com.gorillalogic.monkeytalk.api.meta.ScriptType;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.test.job.android.tool.Text2XML;
import com.test.job.android.tool.Text2XML.ParserListener;

/**
 * The MonkeyTalk editor for tests
 * <ul>
 * <li>page 0 contains a MonkeyTalkTabularEditor
 * <li>page 1 contains a TextEditor
 * <li>page 2 contains a "read-only" Javascript editor
 * </ul>
 */
public class FoneMonkeyTestEditor extends MultiPageEditorPart implements IResourceChangeListener,
		IResourceDeltaVisitor, IRecordTarget, IPlayablePartial {

	private TextEditor textEditor;
	private MonkeyTalkTabularEditor fmc;
	private FoneMonkeyConsoleHelper fmch;
	private int lastPage = 0;
	public static Map<String, Color> colors = new HashMap<String, Color>();

	private static final int TABULAR_EDITOR_PAGE = 0;
	private static final int TEXT_EDITOR_PAGE = 1;
	private static final int JS_EDITOR_PAGE = 2;

	/**
	 * Creates a multi-page editor example.
	 */
	public FoneMonkeyTestEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		// colors.put("RED", this.getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
		fmch = new FoneMonkeyConsoleHelper();
	}

	/**
	 * Creates page 0 the tabular view
	 */
	void createPage0() {

		int index;
		try {
			index = this.addPage(fmc, getEditorInput());
			setPageText(index, "Table View");
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates page 1 the monkey talk view
	 */
	void createPage1() {
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, "MonkeyTalk");
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null,
					e.getStatus());
		}
	}

	/**
	 * Creates page 2 of the multi-page editor, which shows the java script
	 */
	StyledText t;

	void createPage2() {
		Composite mainMainComposite = new Composite(getContainer(), SWT.NONE);
		mainMainComposite.setLayout(new FormLayout());
		Button b = new Button(mainMainComposite, SWT.NONE);
		b.setText("Export");
		b.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDown(MouseEvent arg0) {

			}

			@Override
			public void mouseUp(MouseEvent arg0) {
				if (!t.getText().isEmpty()) {
					MonkeyTalkUtils.generateJScript(
							(FileEditorInput) FoneMonkeyTestEditor.this.getEditorInput(),
							fmc.getCommands(), FoneMonkeyTestEditor.this.getSite());
				} else {
					PartInitException ex = new PartInitException(
							"Is not possible to export empty javascript code to a file.");
					ErrorDialog.openError(getSite().getShell(),
							"Error exporting javascript content", null, ex.getStatus());
				}
			}

		});
		
		Button xml = new Button(mainMainComposite, SWT.NONE);
		xml.setText("Export AS XML");
		xml.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!t.getText().isEmpty()) {
					Text2XML.generateXmlScript(
							(FileEditorInput) FoneMonkeyTestEditor.this.getEditorInput(),
							FoneMonkeyTestEditor.this.getSite());
				} else {
					PartInitException ex = new PartInitException(
							"Is not possible to export empty to a file.");
					ErrorDialog.openError(getSite().getShell(),
							"Error exporting javascript content", null, ex.getStatus());
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		
		t = new StyledText(mainMainComposite, SWT.H_SCROLL | SWT.V_SCROLL);

		t.setEditable(false);
		FormData data1 = new FormData();
		data1.left = new FormAttachment(0, 5);
		data1.right = new FormAttachment(25, 0);
		b.setLayoutData(data1);
		
		FormData data2 = new FormData();
		data2.left = new FormAttachment(b,10);
		data2.right = new FormAttachment(50,25);
		xml.setLayoutData(data2);

		FormData data3 = new FormData();
		data3.top = new FormAttachment(b, 5);
		data3.left = new FormAttachment(0, 0);
		data3.right = new FormAttachment(100, 0);
		data3.bottom = new FormAttachment(100, 0);
		t.setLayoutData(data3);
		if (((FileEditorInput) getEditorInput()).getFile().getFileExtension()
				.equalsIgnoreCase("mt")) {
			int index = addPage(mainMainComposite);
			setPageText(index, "JavaScript");
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	@Override
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
		convertFromMonkeyTalk();

		FoneMonkeyPlugin.getDefault().getController().setFrontEditor(this);
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code>
	 * method disposes all nested editors. Subclasses may extend.
	 */
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		FoneMonkeyPlugin.getDefault().getController().stopRecording();
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getActivePage() == TABULAR_EDITOR_PAGE) {
			convertToMonkeyTalk();
		} else if (getActivePage() == TEXT_EDITOR_PAGE) {
			convertFromMonkeyTalk();
		}
		loadJS();
		try {
			File f = new File(((FileEditorInput) getEditorInput()).getPath().toString());
			f = f.getParentFile();

			String jsLIB = JSLibGenerator.createLib(((FileEditorInput) getEditorInput()).getFile()
					.getProject().getName(), f);
			File outfile = new File(f.getAbsolutePath() + "/libs/"
					+ ((FileEditorInput) getEditorInput()).getFile().getProject().getName() + ".js");
			writeStringToFile(outfile, jsLIB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getEditor(TEXT_EDITOR_PAGE).doSave(monitor);
		fmc.setDirty(false);
	}

	public boolean writeStringToFile(File f, String data) {
		try {

			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(data);
			out.close();

			((FileEditorInput) getEditorInput()).getFile().getProject()
					.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			return false;
		} catch (IOException e) {
			return false;

		}
		return true;
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the text for page 0's
	 * tab, and updates this multi-page editor's input to correspond to the nested editor's.
	 */
	@Override
	public void doSaveAs() {
		IEditorPart editor = getEditor(TEXT_EDITOR_PAGE);
		editor.doSaveAs();
		// setPageText(TABULAR_EDITOR_PAGE, editor.getTitle());
		setInput(editor.getEditorInput());
		setPartName(editor.getTitle());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(TABULAR_EDITOR_PAGE);
		IDE.gotoMarker(getEditor(TABULAR_EDITOR_PAGE), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method checks that the input
	 * is an instance of <code>IFileEditorInput</code>.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		setPartName(editorInput.getName());

		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}

		super.init(site, editorInput);

		try {
			fmc = createTabularEditor(editorInput);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PartInitException("Error initializing " + this.getClass().getSimpleName(), e);
		}

	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (isDirty()) {
			if (lastPage == TABULAR_EDITOR_PAGE) {
				convertToMonkeyTalk();
			} else if (lastPage == TEXT_EDITOR_PAGE) {
				convertFromMonkeyTalk();
			}
		}
		if (newPageIndex == JS_EDITOR_PAGE) {
			t.setText("");
			loadJS();
		}
		lastPage = newPageIndex;
	}

	/**
	 * Load the data from the tabular editor into the JavaScript view.
	 * 
	 * @return true if succesful, false if JavaScript was not generated
	 */
	private boolean loadJS() {
		try {
			for (Command c : fmc.getCommands()) {
				CommandValidator cv = CommandValidator.validate(c);
				if (!cv.getStatus().equals(CommandStatus.OK)) {
					throw new Exception(cv.getStatus() + " - " + cv.getMessage() + " in command \""
							+ c.getCommand() + "\"");
				}
			}
			String js = JSMTGenerator.createScript(((FileEditorInput) getEditorInput()).getFile()
					.getProject().getName(), getEditorInput().getName(), fmc.getCommands());
			t.setText(js);
			return true;
		} catch (Exception e) {
			PartInitException ex = new PartInitException("Unable to generate JavaScript: " + e);
			ErrorDialog.openError(getSite().getShell(), "Error generating javascript", null,
					ex.getStatus());
			return false;
		}
	}

	/**
	 * Convert what has been entered into the tabular editor and place it in the text editor
	 */
	private void convertToMonkeyTalk() {
		if (textEditor == null) {
			return;
		}
		textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput())
				.set(fmc.getCommandsAsString());
	}

	/**
	 * Converts the monkey talk that has been typed into the text editor into something that the
	 * tabular editor can understand
	 */
	public void convertFromMonkeyTalk() {
		// fmc.isLoading = true;
		StringTokenizer st = new StringTokenizer(textEditor.getDocumentProvider()
				.getDocument(textEditor.getEditorInput()).get(), "\n");
		List<Command> commands = new ArrayList<Command>();
		while (st.hasMoreElements()) {
			String line = st.nextToken();
			if (line.trim().length() > 0) {
				Command c = new Command(line);
				if (c.isComment()) {
					String comment = c.toString();
					c.setComponentType(comment);
				}
				commands.add(c);
			}
		}
		fmc.setCommands(commands);
	}

	/**
	 * Path to this editor's resource starting with the project name.
	 */
	private IPath getPath() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null || !(editorInput instanceof FileEditorInput)) {
			return null;
		}

		FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
		IFile file = fileEditorInput.getFile();
		if (file == null) {
			return null;
		}
		IPath path = file.getFullPath();
		return path;
	}

	/**
	 * If the resource in this editor has moved, update this editor's name.
	 */
	private void handleMove(IResourceDelta delta) {
		IPath path = getPath();
		IPath from = delta.getMovedFromPath();
		if (path == null || !path.equals(from)) {
			return;
		}
		final IResource resource = delta.getResource();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setPartName(resource.getName());
			}
		});
	}

	/**
	 * If the resource in this editor has been removed, close this editor.
	 */
	private void handleRemove(IResourceDelta delta) {
		if (delta.getKind() != IResourceDelta.REMOVED) {
			return;
		}

		IPath to = delta.getMovedToPath();
		if (to != null) {
			return;
		}

		IPath path = getPath();
		IPath deltaPath = delta.getFullPath();
		if (path == null || !path.equals(deltaPath)) {
			return;
		}
		closeEditor();
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		handleMove(delta);
		handleRemove(delta);
		return true;
	}

	private void closeEditor() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IEditorInput editorInput = textEditor.getEditorInput();
				IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
				for (IWorkbenchPage page : pages) {
					IEditorPart editorPart = page.findEditor(editorInput);
					page.closeEditor(editorPart, false);
				}
			}
		});
	}

	/**
	 * Closes this editor if its resource is deleted. Renames this editor if its resource is
	 * renamed. Closes this editor on project close or delete.
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {

		switch (event.getType()) {
		case IResourceChangeEvent.POST_CHANGE:
			try {
				event.getDelta().accept(this);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
			break;

		case IResourceChangeEvent.PRE_CLOSE:
		case IResourceChangeEvent.PRE_DELETE:
			IProject project = getProject();
			IProject eventProject = event.getResource().getProject();
			if (project.equals(eventProject)) {
				FoneMonkeyPlugin.getDefault().getController().stopRecording();
				closeEditor();
			}
		}
	}

	public FoneMonkeyConsoleHelper getFmch() {
		return fmch;
	}

	public void setFmch(FoneMonkeyConsoleHelper fmch) {
		this.fmch = fmch;
	}

	@Override
	public void setFocus() {
		FoneMonkeyPlugin.getDefault().getController().setFrontEditor(this);

		super.setFocus();
	}

	public IProject getProject() {
		return ((FileEditorInput) getEditorInput()).getFile().getProject();
		// URI uri = URI.createPlatformResourceURI(this.getEditorInput().);
		// Resource resource = rsrcSet.getResource(uri, true);

	}

	public MonkeyTalkTabularEditor getTabularEditor() {
		return fmc;
	}

	public void recordCommand(Command command, boolean b) {
		if (getSelectedPage() instanceof ITextEditor) {
			convertFromMonkeyTalk();
		}
		fmc.appendRow(command, b);
		convertToMonkeyTalk();
	}

	public void clearAll() {
		fmc.clear();
		convertToMonkeyTalk();
	}

	@Override
	public RecordListener getRecordListener() {
		return new EditorRecordListener(this);
	}

	@Override
	public PlaybackListener getPlaybackListener() {
		return new EditorPlaybackListener(this);
	}

	@Override
	public PlaybackListener getPlaybackListener(int from, int to) {
		from = from - fmc.getBlankCommandOffset(from);
		to = to - fmc.getBlankCommandOffset(to);
		fmc.deleteBlankRows();
		return new EditorPlaybackListener(this, from, to);
	}

	@Override
	public List<Command> getCommands() {
		return fmc.getCommands();
	}

	// ////////// ////////////////////////////
	private static Class<?> editorClass = null;

	public static void setTabularEditorClass(Class<?> klass) {
		FoneMonkeyTestEditor.editorClass = klass;
	}

	private static String[] suiteComponents = API.getComponentNames(ScriptType.SUITE);

	private MonkeyTalkTabularEditor createTabularEditor(IEditorInput editorInput) throws Exception {
		MonkeyTalkTabularEditor editor = createTabularEditorFromExtension();

		if (editor == null) {
			editor = createTabularEditorFromClass();
		}

		if (editor != null) {
			if (((FileEditorInput) editorInput).getFile().getFileExtension()
					.equalsIgnoreCase("mts")) {
				editor.setLimitedComponentSet(suiteComponents);
			} else {
				// we need to get the command world here somehow
				File projectDir = new File(((FileEditorInput) getEditorInput()).getPath()
						.toString());
				CommandWorld commandWorld = new CommandWorld(projectDir.getParentFile());
				Map<String, ArrayList<String>> customCommands = new HashMap<String, ArrayList<String>>();
				for (File customCommandFile : commandWorld.getCustomCommandFiles()) {
					String[] split = customCommandFile.getName().split("\\.");
					if (split.length > 2) {
						if (customCommands.containsKey(split[0])) {
							customCommands.get(split[0]).add(split[1]);
						} else {
							ArrayList<String> actions = new ArrayList<String>();
							actions.add(split[1]);
							customCommands.put(split[0], actions);
						}
					}
				}
				editor.setCustomComponents(customCommands);
			}
		}

		return editor;
	}

	private MonkeyTalkTabularEditor createTabularEditorFromClass() {
		MonkeyTalkTabularEditor editor = null;

		if (FoneMonkeyTestEditor.editorClass == null) {
			FoneMonkeyTestEditor.editorClass = MonkeyTalkTabularEditor.class;
		}

		try {
			editor = (MonkeyTalkTabularEditor) FoneMonkeyTestEditor.editorClass.newInstance();
		} catch (Exception e) {
			FoneMonkeyPlugin.getDefault().logError(e.getMessage());
			editor = null;
		}

		return editor;
	}

	private MonkeyTalkTabularEditor createTabularEditorFromExtension() {
		MonkeyTalkTabularEditor editor = null;
		IExtensionRegistry reg = Platform.getExtensionRegistry();

		IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(FoneMonkeyPlugin.TABULAR_EDITOR_EXTENSION_POINT_ID);
		for (IConfigurationElement element : extensions) {
			try {
				editor = (MonkeyTalkTabularEditor) element.createExecutableExtension("class");
			} catch (Exception e) {
				FoneMonkeyPlugin.getDefault().logError(e.getMessage());
				editor = null;
			}
		}
		return editor;
	}

}
