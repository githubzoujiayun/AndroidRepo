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
package com.gorillalogic.monkeytalk.agents;

import org.json.JSONObject;

import com.gorillalogic.monkeytalk.sender.Response;
import com.gorillalogic.monkeytalk.server.ServerConfig;

public class AndroidAgent extends MTAgent {
	
	public static class AndroidAgentInfo {
		public final String launchPackage;
		public final String launchActivity;
		public AndroidAgentInfo(String launchPackage, String launchActivity) {
			this.launchPackage=launchPackage;
			this.launchActivity=launchActivity;
		}
	}

	public AndroidAgent() {
		this(ServerConfig.DEFAULT_PLAYBACK_PORT_ANDROID);
	}

	public AndroidAgent(int port) {
		super(port);
	}

	@Override
	public String getName() {
		return "Android";
	}
	
	@Override
	public String validate() {
		if (getHost() == null || getPort() == -1) {
			return getName() + " - playback host or port not set";
		} else if (getProperty(AndroidEmulatorAgent.ADB_PROP) != null) {
			return getName() + " - adb not needed when running against a remote Android device. Use the 'AndroidEmulator' agent to run on the Emulator or on a tethered device.";
		} else if (getProperty(AndroidEmulatorAgent.ADB_SERIAL_PROP) != null) {
			return getName() + " - adbSerial not needed when running against a remote Android device. Use the 'AndroidEmulator' agent to run on the Emulator or on a tethered device.";
		} else if (getProperty(AndroidEmulatorAgent.ADB_LOCAL_PORT_PROP) != null) {
			return getName() + " - adbLocalPort not needed when running against a remote Android device. Use the 'AndroidEmulator' agent to run on the Emulator or on a tethered device.";
		} else if (getProperty(AndroidEmulatorAgent.ADB_REMOTE_PORT_PROP) != null) {
			return getName() + " - adbRemotePort not needed when running against a remote Android device. Use the 'AndroidEmulator' agent to run on the Emulator or on a tethered device.";
		}
		return super.validate();
	}
	
	public AndroidAgentInfo getAndroidAgentInfo() {
		String launchPackage = null;
		String launchActivity = null;
		try {
			start();
			Response resp = getCommandSender().ping(false);
			if (resp != null) {
				JSONObject body = resp.getBodyAsJSON();
				if (body != null) {
					if (body.has("message")) {
						JSONObject msg = body.getJSONObject("message");
						if (msg.has("androidInfo")) {
							JSONObject androidInfo = msg.getJSONObject("androidInfo");
							if (androidInfo.has("launchPackage")) {
								launchPackage=androidInfo.getString("launchPackage");
							}
							if (androidInfo.has("launchActivity")) {
								launchActivity=androidInfo.getString("launchActivity");
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			// ignore
		}
		return new AndroidAgentInfo(launchPackage, launchActivity);
	}

	public String getLaunchSpec() {
		AndroidAgentInfo info = this.getAndroidAgentInfo();
		if (info.launchActivity !=null && info.launchActivity.length()>0
				&& info.launchPackage !=null && info.launchPackage.length()>0) {
			return info.launchPackage + "/" + info.launchActivity;
		} else {
			return "";
		}
	}
}
