package com.jobs.lib_v1.misc.loc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.location.LocationManager;
import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;

/**
 * App 利用系统定位的管理器
 */
public class AppLocationManager implements AppLocationProviderListener {
	private LocationManager mLocationManager = null; // WIFI定位器
	private Map<String, AppLocationProvider> providers = null;
	private AppLocation mCurrentLocation = null;
	private AppLocation mLastLocation = null;
	private static AppLocationManager mAppLocation = null;

	public synchronized static AppLocationManager getManager() {
		if (null != mAppLocation) {
			return mAppLocation;
		}

		mAppLocation = new AppLocationManager();

		return mAppLocation;
	}

	public Map<String, AppLocationProvider> getProviders() {
		return providers;
	}

	private synchronized void initManager() {
		if (null != mLocationManager) {
			return;
		}

		try {
			mLocationManager = (LocationManager) AppMain.getApp().getSystemService(Context.LOCATION_SERVICE);
		} catch (Throwable e) {
			AppUtil.print(e);
			mLocationManager = null;
		}
	}

	private synchronized void initProviders() {
		initManager();

		if (null == providers) {
			providers = new HashMap<String, AppLocationProvider>();
		}

		if (null != mLocationManager) {
			List<String> providersNames = mLocationManager.getAllProviders();

			// 判断是否为空，不为空时执行下面代码 william
			// Fix bug #6492 : http://10.100.50.126/bugfree/index.php?r=info/edit&type=bug&id=6492
			if (null != providersNames) {
				for (String providerName : providersNames) {
					if (providers.containsKey(providerName)) {
						continue;
					}

					AppLocationProvider provider = AppLocationProvider
							.getProvider(providerName, mLocationManager);
					if (null != provider) {
						provider.setListener(mAppLocation);
						providers.put(providerName, provider);
					}
				}
			}
		}
	}

	public synchronized boolean isAnyAvailable() {
		initProviders();

		for (AppLocationProvider provider : providers.values()) {
			if (provider.isAvailable()) {
				return true;
			}
		}

		return false;
	}

	public synchronized boolean isAnyEnabled() {
		initProviders();

		for (AppLocationProvider provider : providers.values()) {
			if (provider.isEnabled()) {
				return true;
			}
		}

		return false;
	}

	public synchronized boolean isAnyUpdating() {
		initProviders();

		for (AppLocationProvider provider : providers.values()) {
			if (provider.isUpdating()) {
				return true;
			}
		}

		return false;
	}

	public synchronized int getProvidersCount() {
		initProviders();
		return providers.size();
	}

	public AppLocation getCurrentLocation() {
		return mCurrentLocation;
	}

	public AppLocation getLastLocation() {
		return mLastLocation;
	}

	public synchronized void startLocation() {
		mCurrentLocation = null;

		initProviders();

		if (null == mLocationManager) {
			return;
		}

		synchronized (mLocationManager) {
			for (AppLocationProvider tmpProvider : providers.values()) {
				tmpProvider.startLocation();
			}
		}
	}

	public void stopLocation() {
		if (null == mLocationManager) {
			return;
		}

		synchronized (mLocationManager) {
			for (AppLocationProvider tmpProvider : providers.values()) {
				tmpProvider.stopLocation();
			}
		}
	}

	@Override
	public void onFinished(AppLocationProvider provider, boolean requestSuccess) {
		synchronized (mLocationManager) {
			AppLocation loc = provider.getCurrentLocation();
			if (null == loc) {
				return;
			}

			mCurrentLocation = loc;
			mLastLocation = loc;

			AppUtil.error(this, "location: {lng:" + loc.lng + ", lat:" + loc.lat + "}");

			for (AppLocationProvider tmpProvider : providers.values()) {
				if (tmpProvider == provider) {
					continue;
				}

				tmpProvider.stopLocation();
			}
		}
	}
}
