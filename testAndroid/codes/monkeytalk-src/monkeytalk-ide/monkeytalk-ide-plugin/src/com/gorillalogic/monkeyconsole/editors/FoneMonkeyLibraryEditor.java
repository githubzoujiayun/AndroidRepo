package com.gorillalogic.monkeyconsole.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.gorillalogic.monkeyconsole.plugin.MonkeyTalkImagesEnum;
import com.gorillalogic.monkeytalk.utils.FileUtils;

/**
 * The MonkeyTalk editor for libraries
 */
public class FoneMonkeyLibraryEditor extends EditorPart {

	private Composite parent;

	private Image image;

	/**
	 * Creates a library editor example.
	 */
	public FoneMonkeyLibraryEditor() {
		super();
		image = MonkeyTalkImagesEnum.MONKEYTALK_LARGE.image.createImage();
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		setPartName(editorInput.getName());

		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		setSite(site);
		setInput(editorInput);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		// Set Layout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		// Add MT icon
		Label libImage = new Label(parent, SWT.CENTER);
		libImage.setImage(image);
		libImage.setAlignment(SWT.LEFT);
		libImage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		// Add label warning message
		Label libMessage = new Label(parent, SWT.CENTER);
		libMessage.setText("Library files cannot be edited. ");
		libMessage.setAlignment(SWT.LEFT);

		// Add a link to MT documentation
		Link link = new Link(parent, SWT.CENTER);
		link.setText("<a href=\"https://www.cloudmonkeymobile.com/monkeytalk-documentation/monkeytalk-user-guide/libraries/\">More info about MonkeyTalk Libraries</a>");
		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// Open default external browser
					getSite().getWorkbenchWindow().getWorkbench().getBrowserSupport()
							.getExternalBrowser().openURL(new URL(e.text));
				} catch (PartInitException ex) {
					ex.printStackTrace();
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
				}
			}
		});

		Label listMessage = new Label(parent, SWT.CENTER);
		listMessage.setText("This Library contains these scripts:");
		listMessage.setAlignment(SWT.LEFT);

		List fileList = new List(parent, SWT.CENTER);
		File unzipped = null;
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) input).getFile();
			file.getFullPath();
			try {
				unzipped = FileUtils.tempDir();
				FileUtils.unzipFile(file.getLocation().toFile(), unzipped);
				for (File f : unzipped.listFiles()) {
					fileList.add(f.getName());
				}
			} catch (ZipException e1) {
			} catch (IOException e1) {
			}
		}
		Button unzip = new Button(parent, SWT.CENTER);
		setupUnzip(unzip, unzipped);
		Text scriptPreview = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		setupPreview(fileList, unzipped, scriptPreview);
	}

	private void setupUnzip(Button unzipButton, final File unzipped) {
		unzipButton.setText("Unzip library");
		unzipButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				if (getEditorInput() instanceof IFileEditorInput) {
					IFile mtlFile = ((IFileEditorInput) getEditorInput()).getFile();
					IProject project = mtlFile.getProject();
					IFolder newFolder = project.getFolder(mtlFile.getName().replace(".mtl", ""));
					try {
						newFolder.create(false, true, null);
						for (File containedScript : unzipped.listFiles()) {
							IFile newScript = newFolder.getFile(containedScript.getName());
							newScript.create(new FileInputStream(containedScript), true, null);
						}
					} catch (CoreException e) {
					} catch (FileNotFoundException e) {
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});
	}

	private void setupPreview(final List fileList, final File unzipped, final Text text) {
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		fileList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					if (fileList.getSelection().length > 0) {
						String selection = fileList.getSelection()[0];
						if (unzipped != null) {
							for (File file : unzipped.listFiles()) {
								if (file.getName().equals(selection)) {
									BufferedReader bf = new BufferedReader(new FileReader(file));
									String fileContents = "";
									String tmp;
									while ((tmp = bf.readLine()) != null) {
										fileContents += tmp + Text.DELIMITER;
									}
									text.setText(fileContents);
									bf.close();
								}
							}
						}
					} else {
						// if nothing was selected
						text.setText("");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected void createPages() {
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setFocus() {
		// this is to resolve 'DE-828: Cannot open other files when mtl editor is
		// open'
		parent.setFocus();
	}

}
