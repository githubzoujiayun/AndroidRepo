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
package com.gorillalogic.monkeyconsole.wizard;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.gorillalogic.monkeyconsole.plugin.MonkeyTalkImagesEnum;
import com.gorillalogic.monkeytalk.utils.FileUtils;

public class NewMtzWizard extends BasicNewResourceWizard {
	private WizardNewFileCreationPage mainPage;

	public NewMtzWizard() {
		super();
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public void addPages() {
		super.addPages();
		mainPage = new WizardNewFileCreationPage("newFilePage1", getSelection()) {
			@Override
			protected boolean validatePage() {
				if (!super.validatePage()) {
					return false;
				}
				String projectFieldContents = getFileName();
				if (!projectFieldContents.matches("^[a-zA-Z0-9_\\.]+$")) {
					setErrorMessage("The file name can only contain alphanumeric characters and underscores");
					return false;
				} else if (!Pattern.compile("([a-zA-Z]+)")
						.matcher(projectFieldContents.substring(0, 1)).matches()) {
					setErrorMessage("The file name's first character must be a letter.");
					return false;
				}
				return true;
			}

		};
		mainPage.setTitle("Create a new MonkeyTalk Library");
		mainPage.setFileExtension("mtl");
		mainPage.setDescription("MonkeyTalk libraries compress multiple monkey scripts into a single file. "
				+ "All the files in the library can be accessed as though they were in the workspace. ");
		addPage(mainPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle("MonkeyTalk is provided by CloudMonkey Mobile");
		setNeedsProgressMonitor(true);
	}

	protected void initializeDefaultPageImageDescriptor() {
		ImageDescriptor desc = MonkeyTalkImagesEnum.GLICON.image;
		setDefaultPageImageDescriptor(desc);
	}
	
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		if (file == null) {
			return false;
		}
		try {
			File temp = FileUtils.tempDir();
			@SuppressWarnings("rawtypes")
			List selectedFiles = getSelection().toList();
			for (Object sf : selectedFiles) {
				if (sf instanceof IResource) {
					IResource f = (IResource) sf;
					if (f.getType() == IResource.FOLDER || f.getType() == IResource.PROJECT) {
						@SuppressWarnings("unchecked")
						Collection<File> containedFiles = org.apache.commons.io.FileUtils.listFiles(new File(f.getLocationURI()), new String[]{"mt", "mts"}, true);
						for (File cf : containedFiles) {
							org.apache.commons.io.FileUtils.copyFileToDirectory(cf, temp);
						}
					} else {
						org.apache.commons.io.FileUtils.copyFileToDirectory(new File(f.getLocationURI()), temp);
					}
				}
			}
			org.apache.commons.io.FileUtils.copyFile(FileUtils.zipDirectory(temp, false, true),
					new File(file.getLocationURI()));
		} catch (Exception e) {
			return false;
		}
		selectAndReveal(file);
		return true;
	}

}
