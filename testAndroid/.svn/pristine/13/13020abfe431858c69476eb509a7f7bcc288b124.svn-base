package com.gorillalogic.monkeyconsole.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;

import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;

public class ProjectPreferences {
	public static void set(IAdaptable adaptable, String name, String value) {
		IProject project = project(adaptable);
		set(project, name, value);
	}

	public static void set(IProject project, String name, String value) {
		try {
			QualifiedName qualifiedName = qualify(name);
			if (value == null) {
				project.getPersistentProperties().remove(qualifiedName);
			} else {
				project.setPersistentProperty(qualifiedName, value);
			}
		} catch (CoreException e) {
			FoneMonkeyPlugin.getDefault().logError(e);
		}
	}

	public static String get(IAdaptable adaptable, String name) {
		IProject project = project(adaptable);
		return get(project, name);
	}

	public static String get(IProject project, String name) {
		try {
			QualifiedName qualifiedName = qualify(name);
			String value = project.getPersistentProperty(qualifiedName);
			return value;
		} catch (CoreException e) {
			FoneMonkeyPlugin.getDefault().logError(e);
		}
		return null;
	}

	private static IProject project(IAdaptable adaptable) {
		IResource resource = null;
		Object adapter = adaptable.getAdapter(IResource.class);
		resource = (IResource) adapter;
		IProject project = resource.getProject();
		return project;
	}

	private static QualifiedName qualify(String name) {
		QualifiedName qualifiedName = new QualifiedName(PreferenceConstants.CORE_PROJECT_QUALIFIER,
				name);
		return qualifiedName;
	}
}
