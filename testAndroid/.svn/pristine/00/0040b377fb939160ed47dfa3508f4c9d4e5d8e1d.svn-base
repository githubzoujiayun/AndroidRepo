package com.gorillalogic.monkeyconsole.wizard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.gorillalogic.monkeyconsole.builder.MonkeyTalkNature;

public class MonkeyTalkProjectSupport {
	/**
	     * For this marvelous project we need to:
	     * - create the default Eclipse project
	     * - add the custom project nature
	     * - create the folder structure
	     *
	     * @param projectName
	     * @param location
	     * @param natureId
	     * @return
	     */
	public static IProject createProject(String projectName, URI location) {
		//Assert.isNotNull(projectName);
		//Assert.isTrue(projectName.trim().length() > 0);

		IProject project = createBaseProject(projectName, location);
		try {
			addNature(project);
			String[] paths = { "libs" }; //$NON-NLS-1$ //$NON-NLS-2$
			addToProjectStructure(project, paths);
			addLibsContent(project);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (CoreException e) {
			e.printStackTrace();
			project = null;
		}

		return project;
	}

	private static void addLibsContent(IProject project) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
				project.getFolder("libs").getLocation().toString()+ "/MonkeyTalkAPI.js")));
			out.write(fileToString("templates/MonkeyTalkAPI.js"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Just do the basics: create a basic project.
	 *
	 * @param location
	 * @param projectName
	 */
	private static IProject createBaseProject(String projectName, URI location) {
		// it is acceptable to use the ResourcesPlugin class
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if (!newProject.exists()) {
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
			if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
				projectLocation = null;
			}

			desc.setLocationURI(projectLocation);
			try {
				newProject.create(desc, null);
				if (!newProject.isOpen()) {
					newProject.open(null);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return newProject;
	}

	private static void createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}
		if (!folder.exists()){
			folder.create(false, true, null);
		}
	}

	/**
	 * Create a folder structure with a parent root, overlay, and a few child
	  * folders.
	  *
	  * @param newProject
	  * @param paths
	  * @throws CoreException
	  */
	private static void addToProjectStructure(IProject newProject, String[] paths) throws CoreException {
		for (String path : paths) {
			IFolder etcFolders = newProject.getFolder(path);
			createFolder(etcFolders);
		}
	}
	
	public static String fileToString(String file) throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(file);
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	protected static void addNature(IProject project) throws CoreException {
		if (!project.hasNature(MonkeyTalkNature.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 2];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = "org.eclipse.wst.jsdt.core.jsNature";
			newNatures[natures.length + 1] = MonkeyTalkNature.NATURE_ID;
			description.setNatureIds(newNatures);
			IProgressMonitor monitor = null;
			project.setDescription(description, monitor);
		}
	}
}
