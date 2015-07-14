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
package com.gorillalogic.monkeyconsole.plugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.service.runnable.StartupMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.gorillalogic.monkeyconsole.editors.utils.CloudServices;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.preferences.ISetEnabledPreferenceStore;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;
import com.gorillalogic.monkeyconsole.preferences.SetEnabledPreferenceStore;
import com.gorillalogic.monkeytalk.utils.AndroidUtils;
import com.gorillalogic.monkeytalk.utils.IStringSetStore;

/**
 * The activator class controls the plug-in life cycle
 */
public class FoneMonkeyPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.gorillalogic.monkeytalk.ide"; //$NON-NLS-1$

	// The shared instance
	private static FoneMonkeyPlugin plugin;

	// Tabular Editor Extension Point Id
	public static final String TABULAR_EDITOR_EXTENSION_POINT_ID = PLUGIN_ID + ".tabularEditor";

	private MonkeyTalkController monkeyTalkControls;
	private ServiceRegistration startupMonitorRegistration;

	/**
	 * @see #getPreferenceStore()
	 */
	private ISetEnabledPreferenceStore preferenceStore;

	/**
	 * The constructor
	 */
	public FoneMonkeyPlugin() {

	}

	/**
	 * Decorate the {@link IPreferenceStore} with {@link IStringSetStore} functionality.
	 */
	@Override
	public synchronized ISetEnabledPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new SetEnabledPreferenceStore(super.getPreferenceStore());
		}
		return preferenceStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		MonkeyTalkStartupMonitor mtsm = new MonkeyTalkStartupMonitor(context);
		Dictionary<String, String> monitorProps = new Hashtable<String, String>();
		monitorProps.put("name", "startup handler");
		startupMonitorRegistration = context.registerService(StartupMonitor.class.getName(), mtsm,
				monitorProps);
	}

	public MonkeyTalkController getController() {
		if (monkeyTalkControls == null)
			monkeyTalkControls = new MonkeyTalkController();
		return monkeyTalkControls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (monkeyTalkControls != null) {
			monkeyTalkControls.stopRecording();
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static FoneMonkeyPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *          the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	class MonkeyTalkStartupMonitor implements StartupMonitor {

		MonkeyTalkStartupMonitor(BundleContext context) {

		}

		@Override
		public void applicationRunning() {

			log("MonkeyTalkStartupMonitor: applicationRunning() at " + (new Date()));

			// checkLogEventConsent();

			checkCloudAvailability();

			AndroidUtils.setSdkResolver(new AndroidUtils.SdkResolver() {
				public String getSdkPath() {
					return FoneMonkeyPlugin.getDefault().getPreferenceStore()
							.getString(PreferenceConstants.P_ANDROIDHOME);
				}
			});

			com.gorillalogic.monkeyconsole.commands.PreferenceToggleHandler.refreshAll();

			startupMonitorRegistration.unregister();
		}

		@Override
		public void update() {
		}

		private void checkCloudAvailability() {
			String username = FoneMonkeyPlugin.getDefault().getPreferenceStore()
					.getString(PreferenceConstants.P_CLOUDUSR);
			if (username == null || username.trim().length() == 0) {
				username = "NOT_AVAILABLE";
			}
			String cloudData = "username=" + username;
			CloudServices.logEventAsync("MONKEYTALK_IDE_STARTUP", cloudData);
		}

		private void checkLogEventConsent() {
			FoneMonkeyPlugin p = FoneMonkeyPlugin.getDefault();
			boolean askedAndAnswered = p.getPreferenceStore().contains(
					PreferenceConstants.P_LOGEVENTCONSENT);
			log("MonkeyTalkStartupMonitor:checkLogEventConsent() askedAndAnswered=" + askedAndAnswered);
			if (!askedAndAnswered) {
				MessageBox dialog = new MessageBox(p.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.ICON_ERROR | SWT.YES | SWT.NO);
				dialog.setText("Please Help Us Improve MonkeyTalk");
				dialog
						.setMessage("Can we send some information to the MonkeyTalk dev team about which features are being used and whether they are working?");
				boolean conseted = dialog.open() == SWT.YES;
				p.getPreferenceStore().setValue(PreferenceConstants.P_LOGEVENTCONSENT, conseted);
			}
			boolean logEventConsent = p.getPreferenceStore().getBoolean(
					PreferenceConstants.P_LOGEVENTCONSENT);
			log("MonkeyTalkStartupMonitor:checkLogEventConsent() logEventConsent=" + logEventConsent);
			return;
		}
	}

	public void log(String s) {
		this.getLog().log(new Status(IStatus.INFO, FoneMonkeyPlugin.PLUGIN_ID, s));
	}

	public void logError(Exception e) {
		StringWriter s = new StringWriter();
		PrintWriter w = new PrintWriter(s);
		e.printStackTrace(w);
		this.getLog().log(new Status(IStatus.ERROR, FoneMonkeyPlugin.PLUGIN_ID, s.toString()));
	}

	public void logError(String s) {
		this.getLog().log(new Status(IStatus.ERROR, FoneMonkeyPlugin.PLUGIN_ID, s));
	}

	public static Image getImage(String imagePath) {

		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,
				imagePath);
		Image image = imageDescriptor.createImage();

		return image;

	}

}