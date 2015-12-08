package com.jobs.lib_v1.misc.loc;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.ObjectSessionStore;
import com.jobs.lib_v1.misc.handler.MessageHandler;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

/**
 * App 定位控件
 */
public class AppLocationProvider implements LocationListener {
	private final static int LocationHandlerMsgTypeRequestUpdate = 1; // 开始获取结果
	private final static int LocationHandlerMsgTypeRemoveUpdate = 2;  // 停止获取结果

	private boolean mAvailable = false; // 某类定位服务是否可用
	private boolean mEnable = false; // 某类定位服务是否被启用
	private boolean mUpdating = false; // 是否正在等待某类定位服务返回结果

	private String mProviderName = null; // 某类定位服务的名称
	private LocationManager mLocationManager = null; // 定位服务管理器
	private AppLocation mLastLocation = null; // 上次定位的结果
	private AppLocation mCurrentLocation = null; // 发起定位请求后，获得的位置
	
	private AppLocationProviderListener mListener = null; // 定位状态监听器
	private final MessageHandler mHandler = new MessageHandler(){
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			AppLocationProvider provider = (AppLocationProvider) ObjectSessionStore.popObject(data.getString("provider"));

			if (provider instanceof AppLocationProvider) {
				switch (msg.what) {
				case LocationHandlerMsgTypeRequestUpdate:
					provider.RequestUpdate();
					break;
				case LocationHandlerMsgTypeRemoveUpdate:
					provider.RemoveUpdate();
					break;
				}
			}
		}
	};

	private AppLocationProvider(String name, LocationManager manager) throws Exception {
		if(TextUtils.isEmpty(name)){
			throw new Exception("Location provider name can not be empty!");
		}
	
		if(null == manager){
			throw new Exception("Location manager can not be null!");
		}

		mProviderName = name;
		mLocationManager = manager;
		mEnable = mLocationManager.isProviderEnabled(mProviderName);

		if(mEnable){
			mAvailable = true;
			startLocation();
		}
	}

	public static AppLocationProvider getProvider(String name, LocationManager manager){
		try {
			return new AppLocationProvider(name, manager);
		} catch (Throwable e){
			return null;
		}
	}
	
	public void setListener(AppLocationProviderListener l){
		mListener = l;
	}
	
	public AppLocation getCurrentLocation() {
		return mCurrentLocation;
	}

	public AppLocation getLastLocation() {
		return mLastLocation;
	}

	public void startLocation(){
		mHandler.sendEmptyMessage(LocationHandlerMsgTypeRequestUpdate);
	}

	public void stopLocation(){
		mHandler.sendEmptyMessage(LocationHandlerMsgTypeRemoveUpdate);
	}

	public synchronized boolean isEnabled(){
		return mEnable;
	}

	public synchronized boolean isAvailable(){
		return mAvailable;
	}

	public synchronized boolean isUpdating(){
		return mUpdating;
	}

	private void RequestUpdate() {
		synchronized (mLocationManager) {
			if (mUpdating) {
				return;
			}

			try {
				mCurrentLocation = null;
				mLocationManager.requestLocationUpdates(mProviderName, 1000, 0, this);
				mUpdating = true;
			} catch (Throwable e) {
				AppUtil.print(e);

				mUpdating = false;
			}

			if (!mUpdating && null != mListener) {
				try {
					mListener.onFinished(this, false);
				} catch (Throwable e) {
					AppUtil.print(e);
				}
			}
		}
	}

	private void RemoveUpdate() {
		synchronized (mLocationManager) {
			if (!mUpdating) {
				return;
			}

			try {
				mLocationManager.removeUpdates(this);
				mUpdating = false;
			} catch (Throwable e) {
				AppUtil.print(e);
			}

			if (null != mListener) {
				try {
					mListener.onFinished(this, true);
				} catch (Throwable e) {
					AppUtil.print(e);
				}
			}	
		}
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		if (null == location){
			return;
		}

		if(location.getLatitude() == 0 || location.getLongitude() == 0) {
			return;
		}
		
		AppLocation tmpLoc = new AppLocation();
		tmpLoc.lat = location.getLatitude();
		tmpLoc.lng = location.getLongitude();

		mLastLocation = tmpLoc;
		mCurrentLocation = tmpLoc;

		RemoveUpdate();
	}

	@Override
	public synchronized void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.AVAILABLE) {
			mAvailable = true;
		} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			RemoveUpdate();
			mAvailable = false;
		} else if (status == LocationProvider.OUT_OF_SERVICE) {
			mAvailable = false;
			RemoveUpdate();
		}
	}

	@Override
	public synchronized void onProviderEnabled(String provider) {
		mEnable = true;
		mAvailable = true;
	}

	@Override
	public synchronized void onProviderDisabled(String provider) {
		mEnable = false;
		mAvailable = false;
		RemoveUpdate();
	}
}
