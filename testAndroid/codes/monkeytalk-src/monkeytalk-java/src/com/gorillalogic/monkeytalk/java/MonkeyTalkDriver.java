/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2013 Gorilla Logic, Inc.

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
package com.gorillalogic.monkeytalk.java;

import java.io.File;
import java.lang.reflect.Proxy;

import com.gorillalogic.monkeytalk.BuildStamp;
import com.gorillalogic.monkeytalk.agents.AgentManager;
import com.gorillalogic.monkeytalk.agents.AndroidEmulatorAgent;
import com.gorillalogic.monkeytalk.agents.IAgent;
import com.gorillalogic.monkeytalk.java.api.Application;
import com.gorillalogic.monkeytalk.java.error.MonkeyTalkError;
import com.gorillalogic.monkeytalk.java.proxy.ComponentProxyHandler;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;
import com.gorillalogic.monkeytalk.processor.ScriptProcessorFactory;
import com.gorillalogic.monkeytalk.utils.AndroidUtils;

/**
 * The MonkeyTalk driver.
 */
public class MonkeyTalkDriver {
	private ScriptProcessor processor;
	private Scope scope;
	private ComponentProxyHandler componentProxy;
	private int startup = 0;

	/**
	 * Construct a new MonkeyTalk driver with the given working directory and agent name (iOS,
	 * Android, AndroidEmulator).
	 * 
	 * @param dir
	 *            the working directory
	 * @param agentName
	 *            the agent name (iOS, Android, AndroidEmulator)
	 */
	public MonkeyTalkDriver(File dir, String agentName) {
		this(dir, AgentManager.getAgent(agentName));
	}

	/**
	 * Construct a new MonkeyTalk driver with the given working directory, agent name (iOS, Android,
	 * AndroidEmulator), and target host.
	 * 
	 * @param dir
	 *            the working directory
	 * @param agentName
	 *            the agent name (iOS, Android, AndroidEmulator)
	 * @param host
	 *            the target host
	 */
	public MonkeyTalkDriver(File dir, String agentName, String host) {
		this(dir, AgentManager.getAgent(agentName, host));
	}

	/**
	 * Construct a new MonkeyTalk driver with the given working directory, agent name (iOS, Android,
	 * AndroidEmulator), target host, and target port.
	 * 
	 * @param dir
	 *            the working directory
	 * @param agentName
	 *            the agent name (iOS, Android, AndroidEmulator)
	 * @param host
	 *            the target host
	 * @param port
	 *            the target port
	 */
	public MonkeyTalkDriver(File dir, String agentName, String host, int port) {
		this(dir, AgentManager.getAgent(agentName, host, port));
	}

	/**
	 * Construct a new MonkeyTalk driver with the given working directory, agent name (iOS, Android,
	 * AndroidEmulator), target host, target port, logger, and verbose flag.
	 * 
	 * @param dir
	 *            the working directory
	 * @param agentName
	 *            the agent name (iOS, Android, AndroidEmulator)
	 * @param host
	 *            the target host
	 * @param port
	 *            the target port
	 * @param logger
	 *            the logger (defaults to {@code stdout} if {@code null})
	 * @param verbose
	 *            the verbose output flag (true for verbose output)
	 */
	public MonkeyTalkDriver(File dir, String agentName, String host, int port, Logger logger,
			boolean verbose) {
		this(dir, AgentManager.getAgent(agentName, host, port), logger, verbose);
	}

	/**
	 * Construct a new MonkeyTalk driver with working directory and agent.
	 * 
	 * @param dir
	 *            the working directory
	 * @param agent
	 *            the MonkeyTalk agent
	 */
	private MonkeyTalkDriver(File dir, IAgent agent) {
		this(ScriptProcessorFactory.createScriptProcessor(dir, agent), null, true);
	}

	/**
	 * Construct a new MonkeyTalk driver with working directory and agent.
	 * 
	 * @param dir
	 *            the working directory
	 * @param agent
	 *            the MonkeyTalk agent
	 * @param logger
	 *            the logger (defaults to {@code stdout} if {@code null})
	 * @param verbose
	 *            the verbose output flag (true for verbose output)
	 */
	private MonkeyTalkDriver(File dir, IAgent agent, Logger logger, boolean verbose) {
		this(ScriptProcessorFactory.createScriptProcessor(dir, agent), logger, verbose);
	}

	/**
	 * Construct a new MonkeyTalk driver with the given processor, logger, and verbose flag.
	 * 
	 * @param processor
	 *            the MonkeyTalk script processor
	 * @param logger
	 *            the logger (defaults to {@code stdout} if {@code null})
	 * @param verbose
	 *            the verbose output flag (true for verbose output)
	 */
	private MonkeyTalkDriver(ScriptProcessor processor, Logger logger, boolean verbose) {
		this.processor = processor;

		if (logger == null) {
			logger = new Logger();
		}

		scope = new Scope();
		componentProxy = new ComponentProxyHandler(processor, scope);
		componentProxy.setVerbose(verbose);
		componentProxy.setLogger(logger);

		if (processor != null && processor.getAgent() != null
				&& processor.getAgent().getName() != null
				&& processor.getAgent().getName().equalsIgnoreCase("AndroidEmulator")) {
			setAdb(AndroidUtils.getAdb());
		}

		if (verbose) {
			logger.println(BuildStamp.getStamp());
		}
	}

	/**
	 * Set the global timeout.
	 * 
	 * @param timeout
	 *            the timeout (in ms)
	 */
	public void setTimeout(int timeout) {
		if (processor != null) {
			processor.setGlobalTimeout(timeout);
		}
	}

	/**
	 * Set the global thinktime.
	 * 
	 * @param thinktime
	 *            the thinktime (in ms)
	 */
	public void setThinktime(int thinktime) {
		if (processor != null) {
			processor.setGlobalThinktime(thinktime);
		}
	}

	/**
	 * Set the global before & after screenshots.
	 * 
	 * @param screenshots
	 *            true to take before & after screenshots
	 */
	public void setScreenshots(boolean screenshots) {
		if (processor != null) {
			processor.setTakeAfterScreenshot(screenshots);
		}
	}

	/**
	 * Set the global provide metrics after every command
	 * 
	 * @param metrics
	 *            true to provide metrics after every command
	 */
	public void setMetrics(boolean metrics) {
		if (processor != null) {
			processor.setTakeAfterMetrics(metrics);
		}
	}

	/**
	 * Set the global screenshot on error.
	 * 
	 * @param screenshotOnError
	 *            true to take a screenshot on error
	 */
	public void setScreenshotOnError(boolean screenshotOnError) {
		if (processor != null) {
			processor.setGlobalScreenshotOnError(screenshotOnError);
		}
	}

	/**
	 * Set the startup time.
	 * 
	 * @param startup
	 *            the startup time (in s)
	 */
	public void setStartup(int startup) {
		this.startup = startup;
	}

	/**
	 * Set the adb path (for AndroidEmulator only).
	 * 
	 * @param adb
	 *            the adb path
	 */
	public void setAdb(File adb) {
		setAgentProperty(AndroidEmulatorAgent.ADB_PROP, adb != null ? adb.getAbsolutePath() : null);
	}

	/**
	 * Set the adb serial (for AndroidEmulator only).
	 * 
	 * @param serial
	 *            the adb serial
	 */
	public void setAdbSerial(String serial) {
		setAgentProperty(AndroidEmulatorAgent.ADB_SERIAL_PROP, serial);
	}

	/**
	 * Set the adb local port (for AndroidEmulator only).
	 * 
	 * @param port
	 *            the local port
	 */
	public void setAdbLocalPort(int port) {
		setAgentProperty(AndroidEmulatorAgent.ADB_LOCAL_PORT_PROP,
				port > 0 ? Integer.toString(port) : null);
	}

	/**
	 * Set the adb remote port (for AndroidEmulator only).
	 * 
	 * @param port
	 *            the remote port
	 */
	public void setAdbRemotePort(int port) {
		setAgentProperty(AndroidEmulatorAgent.ADB_REMOTE_PORT_PROP,
				port > 0 ? Integer.toString(port) : null);
	}

	/**
	 * Helper to set agent properties.
	 * 
	 * @param key
	 *            the agent property
	 * @param val
	 *            the agent property value
	 */
	private void setAgentProperty(String key, String val) {
		if (processor != null) {
			processor.getAgent().setProperty(key, val);
		}
	}

	/**
	 * Set a custom script playback listener. The listener will receive callbacks when each command
	 * is played and completed, and also when each stand-alone script is played and completed.
	 * 
	 * @param scriptListener
	 *            the custom listener
	 */
	public void setScriptListener(PlaybackListener scriptListener) {
		processor.setPlaybackListener(scriptListener);
	}

	/**
	 * Set verbose output flag. True for verbose output.
	 * 
	 * @param verbose
	 *            true for verbose output
	 */
	public void setVerbose(boolean verbose) {
		if (componentProxy != null) {
			componentProxy.setVerbose(verbose);
		}
	}

	/**
	 * Set the given logger to handle logging output (typically to {@code stdout}).
	 * 
	 * @param logger
	 *            the logger
	 */
	public void setLogger(Logger logger) {
		if (componentProxy != null) {
			componentProxy.setLogger(logger);
		}
	}

	/**
	 * Get the MonkeyTalk application. This is the base class for all MonkeyTalk interactions with
	 * the application under test.
	 * 
	 * @return the MonkeyTalk application
	 */
	public Application app() {
		if (processor != null && processor.getAgent() != null) {
			start();
		}

		// use custom application extension if it exists, otherwise vanilla Application
		Class<? extends Application> klass = (componentProxy.getCustomApplication() != null ? componentProxy
				.getCustomApplication() : Application.class);

		return (Application) Proxy.newProxyInstance(klass.getClassLoader(), new Class[] { klass },
				componentProxy);
	}

	/**
	 * Start the agent. The agent is automatically started when you first get the application (via
	 * {@link MonkeyTalkDriver#app()}). <b>ONLY</b> call {@code start()} if you must restart the
	 * agent after a manual stop (via {@link MonkeyTalkDriver#stop()}).
	 */
	public void start() {
		if (startup > 0) {
			boolean success = processor.getAgent().waitUntilReady(startup * 1000);

			if (!success) {
				throw new MonkeyTalkError(
						"Unable to startup MonkeyTalk connection - timeout after " + startup + "s");
			}
		}

		processor.getAgent().start();
	}

	/**
	 * Stop the agent. Typically, it is not necessary to call this.
	 */
	public void stop() {
		if (processor != null && processor.getAgent() != null) {
			processor.abort();
			processor.getAgent().stop();
			processor.getAgent().close();
		}
	}

	/**
	 * Register the custom Application extension. There can be only one registered extension
	 * interface which much extend the original {@link Application} interface. Once registered, any
	 * calls to {@code app()} may now be cast to the custom application type as needed.
	 * 
	 * <pre>
	 * <code>
	 * MonkeyTalkDriver mt = new MonkeyTalkDriver(new File(&quot;.&quot;), &quot;iOS&quot;);
	 * mt.registerCustomApplication(MyCustomApplication.class);
	 * 
	 * MyCustomApplication app = (MyCustomApplication) mt.app();
	 * app.foo().bar();
	 * </code>
	 * </pre>
	 * 
	 * @param klass
	 *            the Application extension.
	 */
	public void registerCustomApplication(Class<? extends Application> klass) {
		if (componentProxy != null) {
			componentProxy.setCustomApplication(klass);
		}
	}
}
