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
package com.gorillalogic.monkeyconsole.preferences;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	public static final String P_ISFIRSTLMSUBMISSION = "isFirstLMSubmission";
	public static final String P_TAKEAFTERSCREENSHOTS = "takeAfterScreenshots";
	public static final String P_TAKEAFTERMETRICS = "takeAfterMetrics";
	public static final String P_TAKEERRORSCREENSHOTS = "takeErrorScreenshots";
	public static final String P_INCLUDEIOS = "includeIOS";
	public static final String P_INCLUDEANDROID = "includeAndroid";
	public static final String P_ANDROIDHOME = "androidHome";
	public static final String P_THINKTIME = "thinkTime";
	public static final String P_DEFAULTTIMEOUT = "defaultTimeout";
	public static final String P_EVENTSTORECORD = "eventToRecord";
	public static final String P_COMPONENTTREETIMEOUT = "componentTreeTimeout";

	public static final String P_CLOUDUSR = "cloudUser";
	public static final String P_CLOUDPASS = "cloudPass";

	public static final String P_CONTROLLER_HOST = "cloudControllerHost";
	public static final String P_CONTROLLER_PORT = "cloudControllerPort";
	public static final String P_CONTROLLER_SSL_PORT = "cloudControllerSslPort";
	public static final String P_CONTROLLER_PROTOCOL = "cloudControllerProtocol";

	public static final String P_DEVICE_TOKEN = "cloudControllerDevice";

	public static final String P_EVENTSTOFILTER = "eventsToFilter";
	public static final String P_EVENTSTOCOMBINE = "eventsToCombine";

	public static final String P_DUPLICATEEVENTSSUPRESS = "eventsToSupress";

	public static final String P_LOGEVENTCONSENT = "logEventConsent";

	// Not configurable in Prefrences
	public static final String C_HOST = "host";
	public static final String C_PORT = "port";
	public static final String C_CLOUD_HOST = "cloudHost";
	public static final String C_WEBSITE = "website";
	public static final String C_CONNECTION_TYPE = "connectionType";

	// app under test name
	public static final String C_APKNAME = "apkName";
	public static final String C_APPNAME = "appName";

	// Proxy
	public static final String P_USE_PROXY = "cloudProxy";
	public static final String P_PROXY_HOST = "cloudProxyHost";
	public static final String P_PROXY_PORT = "cloudProxyPort";

	public static final String P_USE_PROXY_AUTHENTICATION = "cloudProxyAuthentication";
	public static final String P_PROXY_USERNAME = "proxyUsername";
	public static final String P_PROXY_PASSWORD = "proxyPassword";

	// most-recent settings for Record Action Filter
	// managed on toolbar
	public static final String P_RECORD_MOVE = "recordMove";
	public static final String P_RECORD_SWIPE = "recordSwipe";
	public static final String P_RECORD_DRAG = "recordDrag";
	public static final String P_RECORD_TAP_COORDINATES = "recordTapCoordinates";

	public static final String CORE_PROJECT_QUALIFIER = "";

	public static final String SELECTED_PROJECT_NAME = "selectedProjectName";
	public static final String ALWAYS_INSTRUMENT_KEY = "alwaysInstrument";
	public static final String NEVER_INSTRUMENT_KEY = "neverInstrument";
	public static final String SELECTED_PROJECT_INSTRUMENT_AS_NEEDED_KEY = "selectedProjectInstrumentAsNeeded";

	public static final String PROJECT_CLOUD_OS_KEY = "cloudOs";
	public static final String ANDRIOD = "android";
	public static final String IOS = "ios";
	public static final String PROJECT_CLOUD_APK_PATHS_KEY = "cloudApkPaths";
	public static final String SELECTED_PROJECT_APK_PATH_KEY = "selectedProjectApkPath";

	public static final String PROJECT_CLOUD_APP_PATHS_KEY = "cloudAppPaths";
	public static final String SELECTED_PROJECT_APP_PATH_KEY = "selectedProjectAppPath";

	private static final String MULTIVALUE_SEP = ",";

	public static final void toSet(String key, Set<String> set) {
		set.clear();
		IPreferenceStore store = FoneMonkeyPlugin.getDefault().getPreferenceStore();
		String items = store.getString(key);
		for (String item : items.split(MULTIVALUE_SEP)) {
			set.add(item);
		}
	}

	public static final void store(String key, Collection<Object> items) {
		String value = "";
		String sep = "";
		for (Object item : items) {
			if (item == null) {
				continue;
			}

			value += sep + item;
			sep = MULTIVALUE_SEP;
		}

		IPreferenceStore store = FoneMonkeyPlugin.getDefault().getPreferenceStore();
		store.setValue(key, value);
	}

	public static final void store(String key, Object... items) {
		store(key, Arrays.asList(items));
	}
}