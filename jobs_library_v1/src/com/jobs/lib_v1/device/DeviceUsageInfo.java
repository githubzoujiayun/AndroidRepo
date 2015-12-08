package com.jobs.lib_v1.device;

import java.io.File;

import com.jobs.lib_v1.app.AppUtil;

/**
 * 设备容量情况
 *
 * @author solomon.wen
 * @date 2012-12-10
 */
public class DeviceUsageInfo {
	private long totalBlockCount = 0;
	private long availableBlockCount = 0;
	private long blockSize = 0;
	private String path = "";
	private String label = "";
	private boolean valid = false;

	public DeviceUsageInfo(String device_path){
		this.path = device_path;
	}

	public void setTotalBlockCount(long count){
		totalBlockCount = count;
	}

	public void setValid(boolean is_valid){
		valid = is_valid;
	}

	public boolean isValid(){
		return valid;
	}

	public void setPath(String device_path){
		path = device_path;
	}

	public void setLabel(String device_label){
		label = device_label;
	}
	
	public void setBlockSize(long size){
		blockSize = size;
	}

	public void setAvailableBlockCount(long count){
		availableBlockCount = count;
	}

	public long getUsedSize(){
		if(totalBlockCount <= availableBlockCount){
			return 0;
		}

		return blockSize * (totalBlockCount - availableBlockCount);
	}

	public long getAvailableSize(){
		return blockSize * availableBlockCount;
	}

	public long getTotalSize(){
		return blockSize * totalBlockCount;
	}

	public String getPath(){
		return path;
	}

	public String getLabel(){
		return label;
	}

	public static DeviceUsageInfo getDeviceUsageInfo(File file){
		if(null != file){
			return getDeviceUsageInfo(file.getPath());
		}

		return new DeviceUsageInfo(null);
	}

	public static DeviceUsageInfo getDeviceUsageInfo(String path){
		DeviceUsageInfo dev = new DeviceUsageInfo(path);

		if (null == path) {
			return dev;
		}

		try {
			StatFsEx sf = new StatFsEx(path);

			if (sf.getBlockSizeEx() < 1 || sf.getBlockCountEx() < 1) {
				dev.setValid(false);
				return dev;
			}

			dev.setValid(true);
			dev.setBlockSize(sf.getBlockSizeEx());
			dev.setTotalBlockCount(sf.getBlockCountEx());
			dev.setAvailableBlockCount(sf.getAvailableBlocksEx());
		} catch (Throwable e) {
			AppUtil.print(e);
			dev.setValid(false);
			return dev;
		}

		return dev;
	}
}
