package com.jobs.lib_v1.device;

import android.annotation.SuppressLint;
import android.os.StatFs;
import android.os.Build.VERSION;

/**
 * 获取设备信息(兼容高版本和低版本的 SDK)
 * 
 * @author solomon.wen
 * @date 2014-01-22
 */
public class StatFsEx extends StatFs {
	public StatFsEx(String path) {
		super(path);
	}

	/**
	 * 获取文件系统总大小
	 */
	public long getTotalSize(){
		return getBlockCountEx() * getBlockSizeEx();
	}

	/**
	 * 获取文件系统可用部分的大小
	 */
	public long getAvailableSize(){
		return getAvailableBlocksEx() * getBlockSizeEx();
	}

	/**
	 * 获取文件系统未使用部分的大小
	 */
	public long getFreeSize(){
		return getFreeBlocksEx() * getBlockSizeEx();
	}

    /**
     * The total number of blocks on the file system. This corresponds to the
     * Unix {@code statfs.f_blocks} field.
     */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public long getBlockCountEx() {
		if (VERSION.SDK_INT >= 18) {
			return super.getBlockCountLong();
		}

		return getBlockCount();
    }

    /**
     * The size, in bytes, of a block on the file system. This corresponds to
     * the Unix {@code statfs.f_bsize} field.
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public long getBlockSizeEx() {
		if (VERSION.SDK_INT >= 18) {
			return super.getBlockSizeLong();
		}

		return getBlockSize();
    }

    /**
     * The total number of blocks that are free on the file system, including
     * reserved blocks (that are not available to normal applications). This
     * corresponds to the Unix {@code statfs.f_bfree} field. Most applications
     * will want to use {@link #getAvailableBlocks()} instead.
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public long getFreeBlocksEx() {
		if (VERSION.SDK_INT >= 18) {
			return super.getFreeBlocksLong();
		}

		return getFreeBlocks();
    }

    /**
     * The number of blocks that are free on the file system and available to
     * applications. This corresponds to the Unix {@code statfs.f_bavail} field.
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public long getAvailableBlocksEx() {
		if (VERSION.SDK_INT >= 18) {
			return super.getAvailableBlocksLong();
		}

		return getAvailableBlocks();
    }
}
