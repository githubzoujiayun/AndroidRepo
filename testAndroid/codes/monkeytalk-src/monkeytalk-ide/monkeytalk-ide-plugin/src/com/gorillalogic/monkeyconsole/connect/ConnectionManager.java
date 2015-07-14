/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2012-2014 Gorilla Logic, Inc.

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
package com.gorillalogic.monkeyconsole.connect;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gorillalogic.monkeyconsole.ADBHelper;
import com.gorillalogic.monkeyconsole.server.RecordListener;
import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.agents.AgentManager;
import com.gorillalogic.monkeytalk.agents.CloudAgent;
import com.gorillalogic.monkeytalk.agents.IAgent;
import com.gorillalogic.monkeytalk.sender.CommandSender;
import com.gorillalogic.monkeytalk.sender.Response;
import com.gorillalogic.monkeytalk.sender.Response.ResponseStatus;
import com.gorillalogic.monkeytalk.server.ServerConfig;

public class ConnectionManager {
	private static final int POLLING_FREQUENCY = 2000; // ms
	private static final int MAX_CONNECTION_RETRIES = 30;
	private static final long CONNECTION_RETRY_DELAY = 1000;
	private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

	private ConnectionInfo connectionInfo;
	private RecordListener recordListener = null;
	private ConnectionListener connectionListener = null;

	private boolean isRecording = false;
	private boolean currentlyConnected = false;
	private Timer heartbeatTimer = null;
	private IAgent agent;

	public static interface ConnectionListener {
		public void onConnect(ConnectionInfo connectionInfo, String message);

		public void onDisconnect(ConnectionInfo connectionInfo, String message);
	}

	public ConnectionManager(ConnectionInfo connectionInfo) {
		this(connectionInfo, new DefaultConnectionListener(), null);
	}

	public ConnectionManager(ConnectionInfo connectionInfo, ConnectionListener connectionListener) {
		this(connectionInfo, connectionListener, null);
	}

	public ConnectionManager(ConnectionInfo connectionInfo, ConnectionListener connectionListener,
			RecordListener recordListener) {
		this.connectionInfo = connectionInfo;
		this.connectionListener = connectionListener;
		this.recordListener = recordListener;
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfo info) {
		this.disconnect();
		this.connectionInfo = info;
	}

	public boolean isCurrentlyConnected() {
		return currentlyConnected;
	}

	public ConnectionTypesEnum getConnectionType() {
		return this.getConnectionInfo().getConnectionType();
	}

	public String getHost() {
		return this.getConnectionInfo().getHost();
	}

	public String getDeviceIdentifier() {
		return this.getConnectionInfo().getIdentifier();
	}

	public int getPort() {
		return this.getConnectionInfo().getPort();
	}

	public boolean isRecording() {
		return this.isRecording;
	}

	/**
	 * may be null if the Manager has never connected
	 */
	public IAgent getAgent() {
		return agent;
	}

	public String connect() {
		return connect(this.getConnectionInfo());
	}

	public void disconnect() {
		this.stopRecording();
		if (this.heartbeatTimer != null) {
			this.heartbeatTimer.cancel();
		}
	}

	private String connect(ConnectionInfo info) {
		return connect(info.getConnectionType(), info.getHost(), info.getPort(),
				info.getIdentifier());
	}

	public void startRecording(RecordListener recordListener) {
		this.recordListener = recordListener;
		startRecording();
	}

	public void startRecording() {
		this.isRecording = true;
	}

	public void stopRecording() {
		this.isRecording = false;
	}

	private String connect(ConnectionTypesEnum type, String host, int port, String identifier) {

		// get the agent
		agent = getAgent(type, host, port, identifier);

		// launch Job to connect
		final IAgent agt = agent;
		Job job = new Job("MonkeyTalk") {
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Starting agent", IProgressMonitor.UNKNOWN);
				try {
					agt.start();
				} catch (Exception e) {
					System.out.println("Error starting agent: " + e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		// start monitoring for connection
		doHeartbeat();

		// wait for connection to be established
		if (type != ConnectionTypesEnum.NO_DEVICE) {
			if (waitForConnection(DEFAULT_CONNECTION_TIMEOUT) == false) {
				return "connection to agent timed out";
			}
		}
		return null;
	}

	private IAgent getAgent(ConnectionTypesEnum connectionType, String host, int port,
			String identifier) {
		IAgent agent;
		if (connectionType == ConnectionTypesEnum.EMULATOR) {

			agent = AgentManager.getAgent("AndroidEmulator");
			agent.setProperty("adb", ADBHelper.getAdbPath());
			agent.setProperty("adbSerial", identifier);

		} else if (connectionType == ConnectionTypesEnum.SIMULATOR
				|| connectionType == ConnectionTypesEnum.NETWORKED_IOS) {

			agent = AgentManager.getAgent("iOS", host, port);

		} else if (connectionType == ConnectionTypesEnum.CLOUD) {

			agent = AgentManager.getAgent("Cloud", host, port);
			((CloudAgent) agent).setDeviceToken(identifier);

		} else {

			agent = AgentManager.getAgent("Android", host, port);

		}
		return agent;
	}

	private boolean waitForConnection(int timeout) {
		long t = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < t) {
			if (this.isCurrentlyConnected()) {
				return true;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return false;
			}
		}
		return true;
	}

	void doHeartbeat() {
		final int[] attempts = { 0 };
		if (heartbeatTimer != null) {
			heartbeatTimer.cancel();
		}
		heartbeatTimer = new Timer();
		heartbeatTimer.schedule(new HeartbeatTimerTask(attempts), 0, // initial delay
				POLLING_FREQUENCY); // subsequent rate
	}

	// Need to use IAgent
	public int getPlaybackPort() {
		ConnectionTypesEnum type = getConnectionType();
		if (ConnectionTypesEnum.SIMULATOR == type || ConnectionTypesEnum.NETWORKED_IOS == type) {
			return ServerConfig.DEFAULT_PLAYBACK_PORT_IOS;
		}
		if (ConnectionTypesEnum.FLEX == type) {
			return ServerConfig.DEFAULT_PLAYBACK_PORT_FLEX;
		}
		if (ConnectionTypesEnum.WEB == type) {
			return ServerConfig.DEFAULT_PLAYBACK_PORT_WEB;
		}
		if (ConnectionTypesEnum.CHROME == type) {
			return ServerConfig.DEFAULT_PLAYBACK_PORT_WEB;
		}
		if (ConnectionTypesEnum.SAFARI == type) {
			return ServerConfig.DEFAULT_PLAYBACK_PORT_HTML5;
		}
		if (ConnectionTypesEnum.IE == type) {
			return ServerConfig.DEFAULT_PLAYBACK_PORT_WEB;
		}

		return ServerConfig.DEFAULT_PLAYBACK_PORT_ANDROID;
	}

	/**
	 * manage connected state and record polling
	 */
	private final class HeartbeatTimerTask extends TimerTask {
		private final int[] attempts;

		private HeartbeatTimerTask(int[] attempts) {
			this.attempts = attempts;
		}

		public void run() {
			boolean wasConnected = currentlyConnected;
			CommandSender cs = agent.getCommandSender();
			Response r = cs.ping(ConnectionManager.this.isRecording());
			if (r.getStatus() == ResponseStatus.OK) {
				currentlyConnected = true;
				handleRecording(r);
			} else {
				currentlyConnected = false;
				if (attempts[0]++ == MAX_CONNECTION_RETRIES) {
					heartbeatTimer.cancel();
				}
				try {
					Thread.sleep(CONNECTION_RETRY_DELAY);
				} catch (InterruptedException e) {
					// OK
				}
			}

			// check for change in connection state
			if (wasConnected != currentlyConnected && connectionListener != null) {
				if (currentlyConnected) {
					String msg = getConnectMessage(r);
					connectionListener.onConnect(connectionInfo, msg);
				} else {
					connectionListener.onDisconnect(connectionInfo,
							"disconnected from MonkeyTalk agent");
				}
			}
		}

		private void handleRecording(Response r) {
			try {
				JSONObject body = r.getBodyAsJSON();
				if (body != null && body.has("message")) {
					Object msg = body.get("message");
					if (!(msg instanceof JSONObject)) {
						if (ConnectionManager.this.isRecording()) {
							JSONArray list = body.getJSONArray("message");
							for (int i = 0; i < list.length(); i++) {
								JSONObject ob = list.getJSONObject(i);
								if (recordListener != null) {
									recordListener.onRecord(new Command(ob), ob);
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		private String getConnectMessage(Response r) {
			String msg = null;
			try {
				JSONObject jsonBody = r.getBodyAsJSON();
				if (jsonBody != null) {
					if (jsonBody.has("message")) {
						JSONObject o = jsonBody.getJSONObject("message");
						String os = null;
						String mtversion = null;
						if (o.has("os")) {
							os = o.getString("os");
						}
						if (o.has("mtversion")) {
							mtversion = o.getString("mtversion");
						}
						if (os != null) {
							msg = os + " agent" + (mtversion != null ? "(" + mtversion + ")" : "");
						}
					}
				}
			} catch (JSONException e) {
				System.err.println("error formatting connect message:" + r.getMessage());
				e.printStackTrace();
			}
			return msg;
		}
	}

	private static class DefaultConnectionListener implements ConnectionListener {
		@Override
		public void onConnect(ConnectionInfo connectionInfo, String message) {
		}

		@Override
		public void onDisconnect(ConnectionInfo connectionInfo, String Message) {
		}
	}

	public String toString() {
		return this.getClass().getSimpleName() + "(" + " connectionInfo=" + connectionInfo
				+ " currentlyConnected=" + currentlyConnected + " isRecording=" + isRecording
				+ " recordListener=" + recordListener + " connectionListener=" + connectionListener
				+ " heartbeatTimer=" + heartbeatTimer + " agent=" + agent + ")";
	}

}