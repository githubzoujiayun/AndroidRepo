package com.gorillalogic.monkeyconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

import com.gorillalogic.monkeyconsole.connect.ConnectionTypesEnum;
import com.gorillalogic.monkeyconsole.editors.utils.CloudServices;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkController;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.plugin.MonkeyTalkImagesEnum;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;

/**
 * Play all commands in the current editor
 */
public class ConnectToAgentHandler extends DynamicUIRadioHandler implements IElementUpdater {

	// used in plugin.xml
	public static final String COMMAND_ID = "com.gorillalogic.monkeyconsole.commands.connectToAgentCommand";

	// these values are used in plugin.xml to configure the connection menu
	public static final String ANDROID_DEVICE_TETHERED = "AndroidDeviceTethered";
	public static final String ANDROID_EMULATOR = "AndroidEmulator";
	public static final String ANDROID_WIFI = "AndroidDeviceWifi";
	public static final String IOS_WIFI = "iOSDeviceWifi";
	public static final String IOS_SIMULATOR = "iOSSimulator";
	public static final String CLOUDMONKEY_DEVICE = "CloudMonkeyDevice";
	public static final String NO_DEVICE = "NoDevice";
	public static final String DROP_DOWN_SELECTOR = "DropDownSelector";

	private Map<String, ImageDescriptor> connectionIcons = new HashMap<String, ImageDescriptor>();
	private Map<String, String> connectionTooltips = new HashMap<String, String>();

	private final boolean includeCloudSupport;

	/*
	 * if (new Boolean(FoneMonkeyPlugin.getDefault().getPreferenceStore()
	 * .getString(PreferenceConstants.P_INCLUDEANDROID))) {
	 * connectionItems.add(connectToAndroidEmulatorTethered); }
	 */

	/**
	 * The constructor.
	 */
	public ConnectToAgentHandler() {
		this(false);
	}

	protected ConnectToAgentHandler(boolean includeCloudSupport) {
		super();
		this.includeCloudSupport = includeCloudSupport;
		initDecoratorMaps();
	}

	/**
	 * the command has been executed, so extract extract the needed information from the application
	 * context.
	 */
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		Shell dialogShell = HandlerUtil.getActiveShell(event);
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();

		if (HandlerUtil.matchesRadioState(event)) {
			// this handler is called twice, once for
			// deselect and once for select
			// not clear how to detect
			// deselect-to-switch vs.
			return null; // we are already in the updated state - do nothing
		}

		// obtain the state from the Command (see plugin.xml)
		String requestedConnection = event.getParameter(RadioState.PARAMETER_ID);
		if (DROP_DOWN_SELECTOR.equals(requestedConnection)) {
			requestedConnection = this.getDropDownSubstituteRadioValue();
		}

		try {

			if (ANDROID_DEVICE_TETHERED.equals(requestedConnection)) {
				run(ConnectionTypesEnum.EMULATOR);

			} else if (ANDROID_EMULATOR.equals(requestedConnection)) {
				run(ConnectionTypesEnum.EMULATOR);

			} else if (ANDROID_WIFI.equals(requestedConnection)) {
				String host = controller.getHost(true);
				if (host != null) {
					run(ConnectionTypesEnum.NETWORKED_ANDROID);
					controller.setHost(host);
					connectionTooltips.put(requestedConnection,
							"Device at " + controller.preferenceStore.getString(PreferenceConstants.C_HOST));
				}

			} else if (IOS_WIFI.equals(requestedConnection)) {
				String host = controller.getHost(true);
				if (host != null) {
					run(ConnectionTypesEnum.NETWORKED_IOS);
					controller.setHost(host);
					connectionTooltips.put(requestedConnection,
							"Device at " + controller.preferenceStore.getString(PreferenceConstants.C_HOST));
				}

			} else if (IOS_SIMULATOR.equals(requestedConnection)) {
				run(ConnectionTypesEnum.SIMULATOR);

			} else if (CLOUDMONKEY_DEVICE.equals(requestedConnection)) {
				String host = controller.getCloudHost();
				int port = controller.getCloudPort();
				if (host != null) {
					controller.setHost(host);
					controller.getDeviceToken(true);
					run(ConnectionTypesEnum.CLOUD);
					connectionTooltips.put(requestedConnection, "CloudMonkey at " + host + ":" + port);
				}

			} else if (NO_DEVICE.equals(requestedConnection)) {
				run(ConnectionTypesEnum.NO_DEVICE);

			} else {
				throw new ExecutionException("unsupported connection type \"" + requestedConnection + "\"");
			}
		} catch (Exception e) {
			MessageBox dialog = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Connect");
			String message = "Cannot connect: " + e.getMessage();
			dialog.setMessage(message);
			dialog.open();
		}

		// and finally update the current state
		HandlerUtil.updateRadioState(event.getCommand(), requestedConnection);
		updateDropDownSelector(event, requestedConnection);

		return null;
	}

	public void run(ConnectionTypesEnum connectionType) {
		MonkeyTalkController controller = FoneMonkeyPlugin.getDefault().getController();
		ConnectionTypesEnum prevConnectionType = controller.getConnectionType();
		boolean wasConnected = controller.isCurrentlyConnected();

		doRun(controller, connectionType);

		if (controller.isCurrentlyConnected()) {
			ConnectionTypesEnum newConnectionType = controller.getConnectionType();
			if (!wasConnected || !(newConnectionType.equals(prevConnectionType))) {
				CloudServices.logEventAsync(
						"IDE_CONNECT_TO_APP",
						"username="
								+ FoneMonkeyPlugin.getDefault().getPreferenceStore()
										.getString(PreferenceConstants.P_CLOUDUSR) + ",connectionType="
								+ newConnectionType);
			}
		}
	}

	protected void doRun(MonkeyTalkController controller, ConnectionTypesEnum connectionType) {
		controller.connectNew(connectionType);
	}

	private void initDecoratorMaps() {
		connectionIcons.put(ANDROID_DEVICE_TETHERED, MonkeyTalkImagesEnum.CONNECTANDROIDEMULATOR.image);
		connectionIcons.put(ANDROID_EMULATOR, MonkeyTalkImagesEnum.CONNECTANDROIDEMULATOR.image);
		connectionIcons.put(ANDROID_WIFI, MonkeyTalkImagesEnum.CONNECTNETWORKEDANDROID.image);
		connectionIcons.put(IOS_WIFI, MonkeyTalkImagesEnum.CONNECTNETWORKEDIOS.image);
		connectionIcons.put(IOS_SIMULATOR, MonkeyTalkImagesEnum.CONNECTIOSEMMULATOR.image);
		if (includeCloudSupport) {
			connectionIcons.put(CLOUDMONKEY_DEVICE, MonkeyTalkImagesEnum.CONNECTCLOUDMONKEY.image);
		}
		connectionIcons.put(NO_DEVICE, MonkeyTalkImagesEnum.NOCONNECTION.image);

		connectionTooltips.put(ANDROID_DEVICE_TETHERED, "Android Device (USB)");
		connectionTooltips.put(ANDROID_EMULATOR, "Android Emulator");
		connectionTooltips.put(ANDROID_WIFI, "Android Device (WiFi)");
		connectionTooltips.put(IOS_WIFI, "iOS Device (WiFi)");
		connectionTooltips.put(IOS_SIMULATOR, "iOS Simulator");
		if (includeCloudSupport) {
			connectionTooltips.put(CLOUDMONKEY_DEVICE, "CloudMonkey Device");
		}
		connectionTooltips.put(NO_DEVICE, "No Device");
	}

	@Override
	protected Map<String, ImageDescriptor> getIcons() {
		return connectionIcons;
	}

	@Override
	protected Map<String, String> getTooltips() {
		return connectionTooltips;
	}

	// what to use until initialized
	@Override
	protected String getDropDownSubstituteRadioValueDefault() {
		return ANDROID_DEVICE_TETHERED;
	}

	@Override
	protected String getDropdownCommandId() {
		return ConnectDropdownHandler.COMMAND_ID;
	}
}
