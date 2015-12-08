package com.jobs.lib_v1.app;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.text.TextUtils;

/**
 * 应用权限检查
 * 
 * @author solomon.wen
 * @date 2014-10-21
 */
public class AppPermissions {
	private static final List<String> mPermissions = loadPermissions();
	
	/**
	 * 获取当前应用的权限清单
	 */
	public static List<String> getPeermissions(){
		return mPermissions;
	}

	/**
	 * 扫描当前应用的权限清单
	 */
	private static List<String> loadPermissions() {
		List<String> permissions = new ArrayList<String>();

		try {
			PackageManager pm = AppMain.getApp().getPackageManager();
			ApplicationInfo appInfo = AppMain.getApp().getApplicationInfo();
			PackageInfo packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            PermissionInfo[] pmis = packageInfo.permissions;

			if (requestedPermissions != null) {
				for (int i = 0; i < requestedPermissions.length; i++) {
					permissions.add(requestedPermissions[i]);
				}
			}

            if (pmis != null) {
                for (PermissionInfo pmi : pmis) {
                    if (null != pmi.name) {
                        permissions.add(pmi.name);
                    }
                }
            }
        } catch (Throwable e) {
			AppUtil.print(e);
		}

		return permissions;
	}

	/**
	 * 判断应用是否有某项权限
	 * 权限名称必须是完整的字符串，类似：android.permission.READ_EXTERNAL_STORAGE
	 * 
	 * @param permission 完整权限名称
	 * @return boolean 如果应用拥有该权限则返回true
	 */
	public static boolean hasPermission(String permission) {
		if (TextUtils.isEmpty(permission)) {
			return false;
		}

		for (String p : mPermissions) {
			if (p.equalsIgnoreCase(permission)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断当期应用是否有外部存储的写入权限
	 */
	public static boolean canWriteExternalStorage() {
		if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
			return false;
		}

		if (!hasPermission("android.permission.READ_EXTERNAL_STORAGE")) {
			return false;
		}

		return true;
	}

	/**
	 * 判断当期应用是否能识别网络状态
	 */
	public static boolean canDetectNetworkState() {
		if (!hasPermission("android.permission.INTERNET")) {
			return false;
		}

		if (!hasPermission("android.permission.ACCESS_NETWORK_STATE")) {
			return false;
		}

		if (!hasPermission("android.permission.ACCESS_WIFI_STATE")) {
			return false;
		}

		return true;
	}

	/**
	 * 判断当前应用是否有权限获取的用户地址位置信息
	 */
	public static boolean canFetchUserLocation() {
		if (hasPermission("android.permission.ACCESS_COARSE_LOCATION")) {
			return true;
		}

		if (hasPermission("android.permission.ACCESS_FINE_LOCATION")) {
			return true;
		}

		if (hasPermission("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS")) {
			return true;
		}

		return false;
	}

    /**
     * 判断是否允许启动51JOB推送服务
     */
    public static boolean canStart51JobPushService() {
        if (!canDetectNetworkState()) {
            return false;
        }

        // Android5.0新规则：不同签名的app不可用同一个自定义的permission grace 20150319
        return hasPermission(AppUtil.packageName() + ".permission.JOBS_PUSH_SERVICE");
    }
}
