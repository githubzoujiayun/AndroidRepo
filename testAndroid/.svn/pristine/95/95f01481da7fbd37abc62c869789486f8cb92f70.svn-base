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

import java.io.File;
import java.util.Arrays;

import com.gorillalogic.monkeytalk.utils.exec.Exec;
import com.gorillalogic.monkeytalk.utils.exec.ExecResult;

public class AndroidEmulatorAgent extends AndroidAgent {
	public static final String ADB_PROP = "adb";
	public static final String ADB_SERIAL_PROP = "adbSerial";
	public static final String ADB_LOCAL_PORT_PROP = "adbLocalPort";
	public static final String ADB_REMOTE_PORT_PROP = "adbRemotePort";

	private AdbHelper adbHelper;

	public AndroidEmulatorAgent() {
		super();
		adbHelper = new AdbHelper();
	}

	@Override
	public String getName() {
		return "AndroidEmulator";
	}

	@Override
	public void start() {
		super.start();
		adbHelper.startAdb(getAdb());
	}

	public File getAdb() {
		String a = getProperty(ADB_PROP);
		return (a == null ? null : new File(a));
	}

	public String getAdbSerial() {
		String s = getProperty(ADB_SERIAL_PROP);
		return (s == null ? "" : s.trim());
	}

	public int getAdbLocalPort() {
		String s = getProperty(ADB_LOCAL_PORT_PROP);
		try {
			int port = Integer.parseInt(s);
			return (port > 0 ? port : getPort());
		} catch (NumberFormatException ex) {
			return super.getPort();
		}
	}

	@Override
	public int getPort() {
		return getAdbLocalPort();
	}

	public int getAdbRemotePort() {
		String s = getProperty(ADB_REMOTE_PORT_PROP);
		try {
			int port = Integer.parseInt(s);
			return (port > 0 ? port : getPort());
		} catch (NumberFormatException ex) {
			return super.getPort();
		}
	}

	@Override
	public void stop() {
		if (adbHelper.isAdbRunning()) {
			adbHelper.stopAdb(getAdb());
		}
	}

	@Override
	public String validate() {
		if (getHost() == null || getPort() == -1) {
			return getName() + " - playback host or port not set";
		} else if (getAdb() == null) {
			return getName()
					+ " - you must specify adb to run on the Android Emulator or on a tethered Android device.";
		} else if (!getAdb().exists()) {
			return getName() + " - you must specify a vaild path to adb. File not found: "
					+ getAdb().getAbsolutePath();
		} else if (!getAdb().isFile()) {
			return getName() + " - you must specify a vaild path to adb. Not a file: "
					+ getAdb().getAbsolutePath();
		}

		return null;
	}

	public String getAppPackage() {
		return "";
	}

	public String getAppActivities() {
		return "";
	}

	private class AdbHelper {
		private boolean adbRunning = false;

		public AdbHelper() {
		}

		public boolean isAdbRunning() {
			return adbRunning;
		}

		private String extractSerial(String adbOutput) {
			if (adbOutput != null) {
				if (adbOutput.contains("List of devices")) {
					String[] lines = adbOutput.split("\n", -1);
					if (lines.length > 1 && lines[1].length() > 0) {
						return lines[1].split("\t")[0];
					}
				}
			}
			return null;
		}

		private void startAdb(File adb) {
			try {
				String[] cmdAndArgs;

				String adbSerial = getAdbSerial();
				if (adbSerial.equals("")) {
					// try to get a serial if we can
					cmdAndArgs = new String[] { adb.getAbsolutePath(), "devices" };
					ExecResult adbResult = Exec.run(cmdAndArgs);
					if (adbResult.getExitValue() == 0) {
						String serial = extractSerial(adbResult.getStdout());
						if (serial != null) {
							setProperty(ADB_SERIAL_PROP, serial);
						}
					}
				}

				adbSerial = getAdbSerial();
				if (adbSerial.contains(":")) {
					connectAdb(adb);
					adbSerial = getAdbSerial();
				}

				// adb forward tcp:<local> tcp:<remote>
				String path = adb.getAbsolutePath();
				int localPort = getAdbLocalPort();
				int remotePort = getAdbRemotePort();

				if (adbSerial.length() > 0) {
					cmdAndArgs = new String[] { path, "-s", adbSerial, "forward", "tcp:" + localPort,
							"tcp:" + remotePort };
				} else {
					cmdAndArgs = new String[] { path, "forward", "tcp:" + localPort, "tcp:" + remotePort };
				}

				ExecResult adbResult = Exec.run(cmdAndArgs);

				if (adbResult.getExitValue() != 0) {
					// retrieve the output
					String output = adbResult.getStdout();
					String err = adbResult.getStderr();
					// combine stdout and stderr so we don't miss anything
					String out = output + ((output.length() > 0 && err.length() > 0) ? "\n" : "") + err;

					throw new RuntimeException(out);
				} else {
					adbRunning = true;
				}
			} catch (Exception ex) {
				throw new RuntimeException("Error starting adb:\n" + ex.getMessage());
			}
		}

		private void stopAdb(File adb) {
			try {
				String[] cmdAndArgs = new String[] { adb.getAbsolutePath(), "kill-server" };
				ExecResult adbResult = Exec.run(cmdAndArgs);

				if (adbResult.getExitValue() != 0) {
					// retrieve the output
					String output = adbResult.getStdout();
					String err = adbResult.getStderr();

					// combine stdout and stderr so we don't miss anything
					String out = output + (output.length() > 0 && output.length() > 0 ? "\n" + err : "");

					throw new RuntimeException(out);
				} else {
					adbRunning = false;
				}
			} catch (Exception ex) {
				throw new RuntimeException("Error stopping adb:\n" + ex.getMessage());
			}
		}

		private void connectAdb(File adb) {
			if (getAdbSerial().length() == 0) {
				return;
			}

			try {
				String[] cmdAndArgs = new String[] { adb.getAbsolutePath(), "connect", getAdbSerial() };
				ExecResult adbResult = Exec.run(cmdAndArgs);

				if (adbResult.getExitValue() != 0) {
					// retrieve the output
					String err = adbResult.getStderr();
					throw new RuntimeException("Error running adb: " + Arrays.toString(cmdAndArgs) + "\n"
							+ err + "  err=" + adbResult.getExitValue());
				} else {
					@SuppressWarnings("unused")
					String output = adbResult.getStdout();
					// System.out.println("connectAdb(): output=" + out);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Error connecting:\n" + ex.getMessage());
			}
		}
	}
}
