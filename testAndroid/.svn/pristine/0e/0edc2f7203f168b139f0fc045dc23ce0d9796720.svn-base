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
package com.gorillalogic.monkeyconsole.editors.utils;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.junit.model.JUnitModel;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.json.JSONException;
import org.json.JSONObject;

import com.gorillalogic.monkeyconsole.ADBHelper;
import com.gorillalogic.monkeyconsole.componentview.ui.UIContainerView;
import com.gorillalogic.monkeyconsole.connect.ConnectionInfo;
import com.gorillalogic.monkeyconsole.connect.ConnectionManager;
import com.gorillalogic.monkeyconsole.connect.ConnectionManager.ConnectionListener;
import com.gorillalogic.monkeyconsole.connect.ConnectionTypesEnum;
import com.gorillalogic.monkeyconsole.editors.FoneMonkeyTestEditor;
import com.gorillalogic.monkeyconsole.editors.IPlayablePartial;
import com.gorillalogic.monkeyconsole.editors.IRecordTarget;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;
import com.gorillalogic.monkeytalk.BuildStamp;
import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.agents.IAgent;
import com.gorillalogic.monkeytalk.automators.ActionFilter;
import com.gorillalogic.monkeytalk.automators.AutomatorConstants;
import com.gorillalogic.monkeytalk.processor.Globals;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.Runner;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;
import com.gorillalogic.monkeytalk.processor.ScriptProcessorFactory;
import com.gorillalogic.monkeytalk.processor.SuiteListener;
import com.gorillalogic.monkeytalk.processor.report.Report;
import com.gorillalogic.monkeytalk.processor.report.detail.DetailReportWriter;
import com.gorillalogic.monkeytalk.sender.CommandSender;
import com.gorillalogic.monkeytalk.sender.Response;
import com.gorillalogic.monkeytalk.sender.Response.ResponseStatus;
import com.gorillalogic.monkeytalk.utils.FileUtils;

public class MonkeyTalkController {

	/**
	 * Map of {@link PlaybackStatus} to colors. It would be better if the colors were pulled from
	 * the user's preferences.
	 */
	static final private EnumMap<PlaybackStatus, Integer> PLAYBACK_STATUS_COLORS = new EnumMap<PlaybackStatus, Integer>(
			PlaybackStatus.class);
	static {
		PLAYBACK_STATUS_COLORS.put(PlaybackStatus.OK, SWT.COLOR_DARK_GREEN);
		PLAYBACK_STATUS_COLORS.put(PlaybackStatus.ERROR, SWT.COLOR_DARK_RED);
		PLAYBACK_STATUS_COLORS.put(PlaybackStatus.FAILURE, SWT.COLOR_RED);
	}
	
	/**
	 * Allows iterating over the {@link PlaybackStatus PlaybackStati} such that 
	 * {@link PlaybackStatus#ERROR} is the last.
	 */
	static final private PlaybackStatus[] PLAYBACK_STATI = { PlaybackStatus.OK,
			PlaybackStatus.FAILURE, PlaybackStatus.ERROR };

	public IPreferenceStore preferenceStore;
	private ActionFilter recordFilter = new ActionFilter();
	private IEditorPart frontEditor = null; // could be FoneMonkeyJavascript Editor, or
											// FoneMonkeyTestEditor
	private FoneMonkeyConsoleHelper fmch;
	private String extention;
	
	// connections
	private ConnectionManager connectionManager;
	private ConnectionListener connectionListener = new ControllerConnectionListener();

	// playback
	private Runner runner;
	private ScriptProcessor processor;
	private Thread commandProcessorThread;
	private CommandSender commandSender;
	private ConnectionTypesEnum connectionType;
	private boolean replayON;

	public MonkeyTalkController() {
		preferenceStore = FoneMonkeyPlugin.getDefault().getPreferenceStore();
		getRecordFilter().set(AutomatorConstants.TOUCH_DOWN, false);
		getRecordFilter().set(AutomatorConstants.TOUCH_UP, false);
		getRecordFilter().set(AutomatorConstants.TOUCH_MOVE, false);
		fmch = new FoneMonkeyConsoleHelper();
	}

	/***
	 * Refresh the workspace
	 */
	private void refreshWorkspace() {
		// last, we refresh the workspace to pick everything up
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			if (workspace != null && workspace.getRoot() != null) {
				workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			} else {
				System.err
						.println("cannot refresh workspace after detail report creation: unable to obtain workspace root");
			}
		} catch (CoreException e) {
			// Could not refresh workspace, no biggie
			System.out.println("error refreshing workspace after detail report creation: "
					+ e.getMessage());
		}
	}

	private File getProjectDir() {
		return ((FileEditorInput) getFrontEditor().getEditorInput()).getFile().getProject()
				.getLocation().toFile();
	}
	
	public boolean canRecord() {
		if (isCurrentlyConnected()) {
			if (isRecordingON() && isReplayON()) {
				// we will sort out the target for recording 
				// when we get the actual request to record
				return true;
			}
		}
		return false;
	}
	
	public void startRecording() {
		IEditorPart editor = getFrontEditor();
		if (editor!=null && editor instanceof IRecordTarget) {
			if (connectionManager!=null) {
				connectionManager.startRecording(((IRecordTarget)editor).getRecordListener());
			} else {
				fmch.writeToConsole("Cannot record, no connection available", true);
			}
		} else {
			fmch.writeToConsole("Cannot record, you must be editing a MonkeyTalk script", true);
		}
		fmch.bringToFront();
	}

	public void stopRecording() {
		if (connectionManager!=null) {
			connectionManager.stopRecording();
		}
	}

	public boolean isRecordingON() {
		if (connectionManager!=null) {
			return connectionManager.isRecording();
		}
		return false;
	}
	
	//////////////////////// BEGIN PLAYBACK /////////////////////////
	
	public boolean isReplayON() {
		return replayON;
	}

	private void setReplayON(boolean replayON) {
		this.replayON = replayON;
	} 	
	
	public void startReplayAll() {
		IEditorPart editor = getFrontEditor(); 
		if (editor!=null && MonkeyTalkUtils.isPlayablePartial(editor)) {
			if (editor instanceof FoneMonkeyTestEditor) {
				((FoneMonkeyTestEditor)editor).getTabularEditor().deleteBlankRows();
			}
			startReplayRange(0, ((IPlayablePartial)editor).getCommands().size());
		}
	}
	
	/**
	 * Stop the replay
	 */
	public void stopReplay() {
		try {
			if (runner!=null) {
				runner.abort();
				runner = null;
			}
			if (processor!=null) {
				processor.abort();
				processor = null;
			}
		} finally {
			this.setReplayON(false);
		}
	}

	public void startReplayRange(final int from, final int to) {
		if (!(getFrontEditor() instanceof IPlayablePartial)) {
			// shouldn't happen
			return;
		}
		
		final IPlayablePartial playbackSource = (IPlayablePartial) getFrontEditor(); 
		
		final File scriptFile = ((FileEditorInput) getFrontEditor().getEditorInput()).getPath()
				.toFile();
		
		this.reconnect();
		IAgent agent = connectionManager.getAgent();
		
		Globals.clear();
		processor = createScriptProcessor(scriptFile.getParentFile(), agent);

		runner = createRunner(agent);

		PlaybackListener editorListener = playbackSource.getPlaybackListener(from, to);
		PlaybackListener listener = new ControllerPlaybackListener(editorListener, null);
		processor.setPlaybackListener(listener);
		runner.setScriptListener(listener);

		commandProcessorThread = new Thread(new Runnable() {
			public void run() {
				if (playbackSource.getCommands().size() >= to
						&& playbackSource.getCommands().size() > 0) {
					PlaybackResult result = null;
					
					File scriptDir = scriptFile.getParentFile();
					try {
						runner.initGlobals(scriptDir, null);
					} catch (IOException e) {
						throw new RuntimeException("Exception thrown attempting to read "
								+ scriptDir + "/global.properties", e);
					}
					
					Scope scope = new Scope("ide");
					if (from == to) {
						// play just a single row
						result = processor.runScript(playbackSource.getCommands().get(from), scope);
					} else if (from == 0 && to == playbackSource.getCommands().size()) {
						// play all!
						scope = new Scope(scriptFile.getPath());
						result = processor.runScript(
								playbackSource.getCommands().subList(from, to), scope);
						// DE293 - The following line is commented and replaced by the previous one
						// in order
						// to highlight the commands as they execute.
						// result = runner.run(scriptFile, null);
					} else if (from >= 0 && to >= 0) {
						// play range of rows
						result = processor.runScript(
								playbackSource.getCommands().subList(from, to), scope);
					}
					writeDetailReport(result, scope);
				}
			}
		});
		commandProcessorThread.start();
		// refreshWorkspace();
	}
	
	public Runner createRunner(IAgent agent) {
		Runner r = new Runner(agent);
		r.setGlobalThinktime(Integer.parseInt(preferenceStore
				.getString(PreferenceConstants.P_THINKTIME)));
		r.setGlobalTimeout(Integer.parseInt(preferenceStore
				.getString(PreferenceConstants.P_DEFAULTTIMEOUT)));
		r.setGlobalScreenshotOnError(FoneMonkeyPlugin.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.P_TAKEERRORSCREENSHOTS));
		r.setTakeAfterScreenshot(preferenceStore
				.getBoolean(PreferenceConstants.P_TAKEAFTERSCREENSHOTS));
		r.setTakeAfterMetrics(preferenceStore.getBoolean(PreferenceConstants.P_TAKEAFTERMETRICS));
		return r;
	}
	
	public ScriptProcessor createScriptProcessor(File rootDir, IAgent agent) {
		ScriptProcessor p = ScriptProcessorFactory.createScriptProcessor(rootDir, agent);
		p.setGlobalThinktime(Integer.parseInt(preferenceStore
				.getString(PreferenceConstants.P_THINKTIME)));
		p.setGlobalTimeout(Integer.parseInt(preferenceStore
				.getString(PreferenceConstants.P_DEFAULTTIMEOUT)));
		p.setGlobalScreenshotOnError(FoneMonkeyPlugin.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.P_TAKEERRORSCREENSHOTS));
		p.setTakeAfterScreenshot(preferenceStore
				.getBoolean(PreferenceConstants.P_TAKEAFTERSCREENSHOTS));
		p.setTakeAfterMetrics(preferenceStore.getBoolean(PreferenceConstants.P_TAKEAFTERMETRICS));
		return p;
	}
	
	public void writeDetailReport(PlaybackResult result, Scope scope) {
		new DetailReportWriter().writeDetailReport(result, scope, getProjectDir(), getReportDir(),
				getRunnerVersion(), getAgentVersion());
		
		// refresh the workspace to pick everything up
		refreshWorkspace();
	}
	
	private File getReportDir() {
		return new File(getProjectDir(), "reports");
	}

	protected String getRunnerVersion() {
		String runnerVersion = "MonkeyTalk IDE"
				+ " v"
				+ BuildStamp.VERSION
				+ (BuildStamp.BUILD_NUMBER != null && BuildStamp.BUILD_NUMBER.length() > 0 ? "_"
						+ BuildStamp.BUILD_NUMBER : "") + " - " + BuildStamp.TIMESTAMP;
		;
		return runnerVersion;
	}
	
	protected String getAgentVersion() {
		String agentVersion = "";
		if (runner != null && runner.getAgent() != null) {
			IAgent agent = runner.getAgent();
			agentVersion = agent.getName() + " v" + agent.getAgentVersion();
		}
		return agentVersion;
	}

	public void startSuiteReplay() {

		Job job = new Job("MonkeyTalk TestSuite") {
			protected IStatus run(final IProgressMonitor monitor) {

				monitor.worked(50);
				final File f = getProjectDir();
				final File reportdir = getReportDir();
				if (!reportdir.exists()) {
					reportdir.mkdir();
				}

				// System.out.println(f.getAbsolutePath());
				reconnect();
				
				runner = createRunner(connectionManager.getAgent());
				runner.setReportdir(reportdir);
				runner.setSuiteListener(new ControllerSuiteListener(monitor));
				
				runner.setScriptListener(new ControllerPlaybackListener(null, monitor));

				File suiteFile = ((FileEditorInput) getFrontEditor().getEditorInput()).getPath()
						.toFile();
				String suiteName = suiteFile.getName();
				PlaybackResult p = null;
				try {
					p = runner.run(suiteFile, null);
				} catch (Exception e) {
					p = new PlaybackResult(PlaybackStatus.ERROR);
					p.setMessage(e.getMessage());
				}				
					
				updateJUnitView(f, p.getMessage());
				writeDetailReport(p, new Scope(suiteName));
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				} else {
					return Status.OK_STATUS;
				}
			}

		};
		job.setUser(true);
		job.schedule();

	}

	public void startJScriptReplay() {

		Job job = new Job("MonkeyTalk JSTest") {
			protected IStatus run(final IProgressMonitor monitor) {

				// monitor.worked(50);
				File jsFile = ((FileEditorInput) getFrontEditor().getEditorInput()).getPath()
						.toFile();

				reconnect();
				runner = createRunner(connectionManager.getAgent());
				ControllerPlaybackListener listener = new ControllerPlaybackListener(null,  monitor);
				runner.setScriptListener(listener);
				runner.setReportdir(getReportDir());

				PlaybackResult p = runner.run(jsFile, null);
				try {
					writeDetailReport(p, new Scope("ide"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();

	}
	
	///////////// END PLAYBACK /////////////////
	
	/**
	 * Clear all rows TBD Move to FoneMonkeyEditor
	 */
	public void clear() {
		// TODO add dialog
		if (getFrontEditor() instanceof FoneMonkeyTestEditor) {
			((FoneMonkeyTestEditor)getFrontEditor()).clearAll();
		}
	}

	public ActionFilter getRecordFilter() {
		return recordFilter;
	}

	public void toggleRecordFilter(String action) {
		ActionFilter f = getRecordFilter();
		f.set(action, !f.get(action));
	}

	public void setFrontEditor(IEditorPart frontEditor) {
		this.frontEditor = frontEditor;
		fmch = new FoneMonkeyConsoleHelper();
	}

	public void setExtention(String extention) {
		this.extention = extention;
	}

	public String getExtention() {
		return this.extention;
	}

	public ConnectionInfo getCurrentConnectionInfo() {
		if (this.connectionManager!=null) {
			return this.connectionManager.getConnectionInfo();
		}
		return null;
	}
	
	public boolean isCurrentlyConnected() {
		if (connectionManager!=null) {
			return connectionManager.isCurrentlyConnected();
		}
		return false;
	}

	public void setHost(String host) {
		preferenceStore.setValue(PreferenceConstants.C_HOST, host);
	}

	public void setCloudHost(String host) {
		preferenceStore.setValue(PreferenceConstants.C_CLOUD_HOST, host);
	}

	private boolean cloudIsConfigured() {
		return (preferenceStore.getString(PreferenceConstants.C_CLOUD_HOST) != null)
				|| !(preferenceStore.getString(PreferenceConstants.C_CLOUD_HOST).isEmpty()) ? true
				: false;
	}

	/**
	 * @return
	 */
	public ConnectionTypesEnum getConnectionType() {
		String value = preferenceStore.getString(PreferenceConstants.C_CONNECTION_TYPE);
		ConnectionTypesEnum type = ConnectionTypesEnum.find(value);
		return type!=null ? type : ConnectionTypesEnum.NO_DEVICE;
	}

	private String getHost() {
		return getHost(false);
	}

	public String getHost(boolean prompt) {
		if (preferenceStore.getString(PreferenceConstants.C_HOST) == null || prompt) {
			String host = enterText("Please enter IP or hostname of networked device",
					preferenceStore.getString(PreferenceConstants.C_HOST) == null ? ""
							: preferenceStore.getString(PreferenceConstants.C_HOST));
			if (host != null) {
				String ipRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(:\\d+)?";
				String hostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])(:\\d+)?";
				if (host.matches(hostnameRegex) || host.matches(ipRegex)) {
					if (host.contains(":")) {
						preferenceStore.setValue(PreferenceConstants.C_HOST, host.split(":")[0]);
						preferenceStore.setValue(PreferenceConstants.C_PORT, host.split(":")[1]);
					} else {
						preferenceStore.setValue(PreferenceConstants.C_HOST, host);
						preferenceStore.setValue(PreferenceConstants.C_PORT, -1);
					}
				} else {
					fmch.writeToConsole("connection error: invalid address", true);
					fmch.bringToFront();
				}
			} else {
				fmch.writeToConsole("connection error: no address entered", true);
				fmch.bringToFront();
			}
		}
		return preferenceStore.getString(PreferenceConstants.C_HOST);
	}

	public String getCloudHost() {
		return preferenceStore.getString(PreferenceConstants.P_CONTROLLER_HOST);
	}

	public int getCloudPort() {
		return preferenceStore.getInt(PreferenceConstants.P_CONTROLLER_PORT);
	}

	public String getDeviceToken() {
		return getDeviceToken(false);
	}

	public String getDeviceToken(boolean prompt) {
		if (prompt) {
			String deviceToken = enterText("Please enter the token for the device",
					preferenceStore.getString(PreferenceConstants.P_DEVICE_TOKEN) == null ? ""
							: preferenceStore.getString(PreferenceConstants.P_DEVICE_TOKEN));
			if (deviceToken != null) {
				preferenceStore.setValue(PreferenceConstants.P_DEVICE_TOKEN, deviceToken);
			} else {
				fmch.writeToConsole(" connection error: no token entered", true);
				fmch.bringToFront();
			}
		}
		return preferenceStore.getString(PreferenceConstants.P_DEVICE_TOKEN);
	}

	String siteToTest = null;

	public String getWebsiteToTest(boolean prompt) {
		if (siteToTest == null || siteToTest.trim().length() == 0 || prompt) {
			String host = enterText("Please enter the url of the website to test",
					siteToTest == null ? "" : siteToTest);
			if (host != null)
				siteToTest = host;
		}
		return siteToTest;
	}

	private int getPort() {
		if (preferenceStore.contains(PreferenceConstants.C_PORT)) {
			return preferenceStore.getInt(PreferenceConstants.C_PORT);
		} else {
			return -1;
		}
	}

	// Need to provide default via IAgent
	private String getDefaultHost(ConnectionTypesEnum type) {
		if (type == ConnectionTypesEnum.EMULATOR) {
			return "localhost";
		}

		if (type == ConnectionTypesEnum.SIMULATOR) {
			return "localhost";
		}

		if (type == ConnectionTypesEnum.NO_DEVICE) {
			return "nodevice";
		}

		return getHost();
	}

	String enterText(String msg, String value) {
		InputDialog dlg = new InputDialog(getDisplay().getActiveShell(), "", msg, value, null);

		if (dlg.open() == Window.OK)
			return dlg.getValue();

		return null;
	}

	private String getConnectionStatus() {

		String type = null;
		if (getConnectionType() != null) {
			type = getConnectionType().humanReadableFormat;
			// If we are connected to a network device, print out the IP address of the device.
			if (getConnectionType() == ConnectionTypesEnum.NETWORKED_ANDROID
					|| getConnectionType() == ConnectionTypesEnum.NETWORKED_IOS) {
				type = getHost(false);
			}
		}

		if (type != null && !type.contains("unknown")) {
			return "Connection type set to: " + type;
		}

		return "Please select a connection type";
	}

	/**
	 * tell the connectionManager to connect to te current settings 
	 */
	public void reconnect() {
		if (!isCurrentlyConnected() && connectionManager!=null) {
			connectionManager.connect();
		}
	}
	
	public void connectNew(ConnectionTypesEnum type) {
		connect(type, null);
	}
	
	public void connect(ConnectionTypesEnum type, String identifier) {
		connectionType = type;
		preferenceStore.setValue(PreferenceConstants.C_CONNECTION_TYPE, type.toString());
		
		if (type == ConnectionTypesEnum.EMULATOR) {
			String msg = ADBHelper.validate();
			if (msg != null) {
				fmch.writeToConsole(type.humanReadableFormat + " connection error: " + msg, true);
				return;
			}
		}
		
		String message = _connect(type, identifier);
		if (message != null) {
			if (message.contains("device not found")) {
				message = "Device could not be found!";
			} else if (message.length() == 0) {
				message = "unspecified";
			}
			fmch.writeToConsole(type.humanReadableFormat + " connection error: " + message, true);
		} else {
			setStatus(getConnectionStatus());
		}
	}

	private String _connect(ConnectionTypesEnum type, String identifier) {
		
		String host = null;
		int port = -1;
		
		if (connectionType == ConnectionTypesEnum.CLOUD) {
			host = getCloudHost();
			port = getCloudPort();
			identifier = getDeviceToken(); 
		} else if (connectionType == ConnectionTypesEnum.NETWORKED_ANDROID
				|| connectionType == ConnectionTypesEnum.NETWORKED_IOS) {
			if (identifier!=null && identifier.length()>0) {
				host = identifier;
			} else {
				host = getDefaultHost(connectionType);
			}
			port = getPort();
		} else {
			host = getDefaultHost(connectionType);
			port = getPort();
		}
		
		ConnectionInfo connectionInfo = new ConnectionInfo(type, host, port, identifier);
		if (connectionManager==null) {
			connectionManager = new ConnectionManager(connectionInfo, this.connectionListener);
		} else {
			connectionManager.setConnectionInfo(connectionInfo);
		}
		connectionManager.connect();
		
		IAgent agent = connectionManager.getAgent();

		runner = createRunner(agent);
		commandSender = runner.getAgent().getCommandSender();

		return null;
	}

	public void setStatus(String msg) {
		setStatus(msg, false);
	}

	void setStatus(final String msg, final boolean isError) {
		MonkeyTalkUtils.runOnGUI(new Runnable() {
			public void run() {
				fmch.writeToConsole(msg, isError);
			}
		}, getDisplay());
	}

	public void fetchAndShowComponentTree() {
		if (commandSender == null) {
			return;
		}

		Integer timeout;
		try {
			timeout = Integer.parseInt(preferenceStore.getString(PreferenceConstants.P_COMPONENTTREETIMEOUT));
		} catch (NumberFormatException e) {
			timeout = null;
		}
		
		Response r = commandSender.dumpTree(timeout);
		
		JSONObject jo = null;
		try {
			jo = new JSONObject(r.getBody());
		} catch (JSONException e) {
			// Unparsable JSON
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		// check for error message
		try {
			jo.getJSONObject("message");
		} catch (Exception e) {
			// not a JSON Object
			try {
				String s=jo.getString("message");
				throw new RuntimeException("error fetching Component Tree:" + s);
			} catch (JSONException ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
		
		try {
			IWorkbenchPage page;
			if (getFrontEditor()!=null) {
				page = getFrontEditor().getSite().getPage(); 
			} else {
				page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			}
			page.showView("com.gorillalogic.monkeyconsole.componentview.ui.UIContainerView");

			UIContainerView mbv = (UIContainerView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.findView("com.gorillalogic.monkeyconsole.componentview.ui.UIContainerView");

			mbv.setInput(jo);
		} catch (JSONException e) {
			FoneMonkeyPlugin.getDefault().log("DANGME: " + e.getMessage());
			// e.printStackTrace();
		} catch (PartInitException e1) {
			FoneMonkeyPlugin.getDefault().log("HANGME: " + e1.getMessage());
			// e1.printStackTrace();
		}
	}

	public void connectToCloudHost(String host) {
		if (!(ConnectionTypesEnum.CLOUD_ANDROID.equals(connectionType))) {
			return;
		}
		this.stopReplay();
		setCloudHost(host);
		// setHost(host);
		connectNew(ConnectionTypesEnum.CLOUD_ANDROID);
	}

	private class ControllerConnectionListener implements ConnectionManager.ConnectionListener {
		@Override
		public void onConnect(ConnectionInfo connectionInfo, String message) {
			if (message != null) {
				fmch.writeToConsole(message, false);
				fmch.bringToFront();
			} else {
				// fmch.writeToConsole("invalid response connecting to MonkeyTalk agent", true);
			}
		}

		@Override
		public void onDisconnect(ConnectionInfo connectionInfo, String message) {
			if (isReplayON()) {
				// do not stop here --> restart command
				// if it is a disconnect during playback which is not a restart, 
				// we will stop anyway with better reporting
				// stopReplay();
			}
			stopRecording();
			fmch.writeToConsole("disconnected from MonkeyTalk agent", false);
			fmch.bringToFront();
		}
	}
	
	private Display getDisplay() {
		if (getFrontEditor()!=null) {
			return getFrontEditor().getSite().getShell().getDisplay();
		}
		return PlatformUI.getWorkbench().getDisplay();
	}
	
	private IEditorPart getFrontEditor() {
		return frontEditor;
	}
	
	private class ControllerPlaybackListener implements PlaybackListener {
		final private PlaybackListener wrappedListener;
		final private IProgressMonitor progressMonitor;
		
		private boolean shouldRefresh = false;
		private final String SCREENSHOT_COMMAND = "Device * Screenshot";
		
		ControllerPlaybackListener(PlaybackListener wrappedListener,
				IProgressMonitor progressMonitor) {
			this.wrappedListener = wrappedListener;
			this.progressMonitor = progressMonitor;
		}
		
		@Override
		public void onScriptStart(Scope scope) {
			setReplayON(true);
			fmch.writeToConsole("Started Script Playback");
			fmch.bringToFront();
			
			if (wrappedListener != null) {	
				wrappedListener.onScriptStart(scope);
			}
		}

		@Override
		public void onScriptComplete(Scope scope, PlaybackResult result) {
			if (wrappedListener != null) {
				wrappedListener.onScriptComplete(scope, result);
			}
			String msg = "Completed Script Playback" + " - " + result.getStatus().toString();
			if (result.getMessage() != null) {
				msg = msg + " " + result.getMessage();
			}
			fmch.writeToConsole(msg);

			setReplayON(false);
		}

		@Override
		public void onStart(Scope scope) {
			if (shouldRefresh) {
				refreshWorkspace();
				shouldRefresh = false;
			}
			fmch.writeToConsole(scope.getCurrentCommand().toString());
			if (wrappedListener != null) {
				wrappedListener.onStart(scope);
			}
			
			if (progressMonitor != null) {
				progressMonitor.subTask(scope.getCurrentCommand().getCommand());
			}
		}

		@Override
		public void onComplete(Scope scope, Response response) {			
			if (wrappedListener != null) {
				wrappedListener.onComplete(scope, response);
			}
			if (response.getStatus() == ResponseStatus.FAILURE) {
				fmch.writeToConsole("FAILURE: " + response.getMessage(), true);
			} else if (response.getStatus() == ResponseStatus.ERROR) {
				fmch.writeToConsole("ERROR: " + response.getMessage(), true);
			} else {
				String m = response.getStatus().toString();
				String msg = response.getMessage();
				if (msg != null && msg.length() > 0) {
					m = m + " - " + msg;
					fmch.writeToConsole(m);
				}
			}
			Command currentCommand = scope.getCurrentCommand();
			if (currentCommand.toString().contains(SCREENSHOT_COMMAND)
					|| currentCommand.toString().contains("Verify")) {
				shouldRefresh = true;
			}
			
			if (progressMonitor != null && progressMonitor.isCanceled()) {
				stopReplay();
			}			
		}

		@Override
		public void onPrint(String message) {
			if (wrappedListener != null) {
			  wrappedListener.onPrint(message);
			}
			fmch.writeToConsole(message, SWT.COLOR_DARK_BLUE);
		}
	}
	
	private class ControllerSuiteListener implements SuiteListener {
		IProgressMonitor monitor;
		File reportFile;
		EnumSet<PlaybackStatus> stati = EnumSet.noneOf(PlaybackStatus.class);
		String testName = null;
		
		ControllerSuiteListener(IProgressMonitor monitor) {
			this.monitor = monitor;
			String reportFileName = ((FileEditorInput)getFrontEditor().getEditorInput()).getPath()
					.toFile().getName();
			this.reportFile =  new File(getReportDir().getAbsolutePath() + "/TEST-"
						+ reportFileName.substring(0, reportFileName.lastIndexOf(".")) + ".xml");
		}
		
		@Override
		public void onSuiteStart(int total) {
			monitor.beginTask("Running TestSuite", total);
			fmch.writeToConsole("Starting suite with " + total + " tests.\n\n", false);
		}

		@Override
		public void onSuiteComplete(PlaybackResult result, Report report) {
			monitor.worked(1);

			int color = SWT.COLOR_BLACK;
			for (PlaybackStatus status : PLAYBACK_STATI ) {
				if (stati.contains(status)) {
					color = PLAYBACK_STATUS_COLORS.get(status);
				}
			}
				
			fmch.writeToConsole("Completed Suite Playback - " + stati, color);
			try {
				FileUtils.writeFile(reportFile, report.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (result != null)
				updateJUnitView(reportFile, result.getMessage());
		}

		@Override
		public void onTestStart(String name, int num, int total) {
			monitor.setTaskName(name);
			fmch.writeToConsole("Starting test " + name + " " + num + "/" + total + ".", false);
			testName = name;
		}

		@Override
		public void onTestComplete(PlaybackResult result, Report report) {
			monitor.worked(1);
			stati.add(result.getStatus());

			fmch.writeToConsole("Test " + testName + " complete - " + result.getStatus() + "\n\n",
					PLAYBACK_STATUS_COLORS.get(result.getStatus()));
			
			try {
				FileUtils.writeFile(reportFile, report.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			updateJUnitView(reportFile, result.getMessage());
		}

		@Override
		public void onRunStart(int total) {
		}

		@Override
		public void onRunComplete(PlaybackResult result, Report report) {
			//fmch.writeToConsole("SUITE RUN COMPLETE.....");
			//MonkeyTalkController.this.stopReplay();
		}
		
	}

	private void updateJUnitView(final File reportfile, final String msg) {
		MonkeyTalkUtils.runOnGUI(new Runnable() {
			public void run() {
				try {
	
					((FileEditorInput) getFrontEditor().getEditorInput()).getFile().getProject()
							.refreshLocal(IResource.DEPTH_INFINITE, null);
	
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					org.eclipse.jdt.internal.junit.ui.TestRunnerViewPart v = (org.eclipse.jdt.internal.junit.ui.TestRunnerViewPart) page
							.showView("org.eclipse.jdt.junit.ResultView");
					JUnitModel.importTestRunSession(reportfile);
					if (msg != null && !msg.equalsIgnoreCase("null"))
						fmch.writeToConsole(msg);
				} catch (CoreException e) {
	
				}
			}
		}, getDisplay());
	}
}