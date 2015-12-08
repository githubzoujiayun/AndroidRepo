package com.jobs.lib_v1.net.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Locale;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Time;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.device.Storage;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.net.NetworkManager;

/**
 * 记录HTTP错误日志信息到文件，并只保留最新的10个文件
 *
 * @author mark.wu
 * @date 2013-12-30
 */
public class HttpExceptionHandler {
    /**
     * 保存HTTP错误日志信息到文件
     */
    public static synchronized void saveExecptionToFile(final String erMsg, final String erStack, boolean isIOException) {
        final StringBuffer logBuffer = new StringBuffer();
        String errorMsg = erMsg;
        String errorStack = erStack;

        if (isIOException) {
            // 构造错误信息格式
            logBuffer.append("IO-Exception:\r\n");
        } else {
            // 构造错误信息格式
            logBuffer.append("NetWork-Exception:\r\n");
        }

        logBuffer.append("Error-Message:\r\n");
        logBuffer.append(errorMsg);
        logBuffer.append("\r\n");

        logBuffer.append("Error-StackInfo:\r\n");
        logBuffer.append(errorStack);
        logBuffer.append("\r\n");

        // 收集客户端版本信息
        logBuffer.append("Client-Info:\r\n");
        try {
            PackageManager pm = AppMain.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AppMain.getApp().getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                logBuffer.append(String.format("%s - version %s(%d) \r\n", pi.packageName, pi.versionName, pi.versionCode));
            }
        } catch (Throwable e) {
        }
        logBuffer.append("\r\n");

        // 收集系统版本信息
        logBuffer.append("Device-Info:\r\n");
        logBuffer.append(String.format("Android OS: %s\r\n", DeviceUtil.getOSMainVersion()));
        logBuffer.append(String.format("Device Model: %s\t\n", DeviceUtil.getDeviceModel()));
        logBuffer.append("Time: " + StrUtil.fromDate() + "\r\n");
        logBuffer.append("UUID: " + DeviceUtil.getUUID() + "\r\n");

        // 收集当前网络信息
        logBuffer.append("NetWork-Info:\r\n");
        if (NetworkManager.isWIFI()) {
            logBuffer.append(DeviceUtil.getConnectedWifiInfo());
            logBuffer.append("MobileNet-Info:\r\n");
            logBuffer.append(DeviceUtil.getConnectedMobileInfo());
        } else if (NetworkManager.isMobileNetwork()) {
            logBuffer.append(DeviceUtil.getConnectedMobileInfo());
        }

        writeToSdCard(logBuffer);
    }

    /**
     * 写入文件
     */
    public synchronized static void writeToSdCard(StringBuffer logBuffer) {
        try {
            String dirPathString = "";
            String filePathString = "";

            dirPathString = Storage.getHttpErrorLogDir();
            File dirFile = null;
            try {
                dirFile = new File(dirPathString);
                if (!(dirFile.exists() && !(!dirFile.isDirectory()))) {
                    dirFile.mkdirs();
                }

                File[] sorFile = getSorteFiles(dirFile);
                if (null != sorFile) {
                    int fileCount = sorFile.length;
                    if (fileCount >= 10) {
                        File file = sorFile[9];
                        file.delete();
                    }
                }

                filePathString = dirPathString + File.separator + "log" + getCurrentTime() + ".txt";
                FileOutputStream fs = new FileOutputStream(filePathString);
                OutputStreamWriter writer = new OutputStreamWriter(fs);
                writer.write(logBuffer.toString());
                writer.flush();
                writer.close();
                fs.close();
            } catch (Throwable e) {
                AppUtil.print(e);
            }
        } catch (Throwable ex) {
            AppUtil.print(ex);
        }
    }

    public static String getCurrentTime() {
        Time time = new Time("PRC");
        time.setToNow();
        return String.format(Locale.US, "%04d%02d%02d%02d%02d%02d", time.year, time.month + 1, time.monthDay, time.hour, time.minute, time.second);
    }

    /**
     * 得到排序后的文件
     */
    public static File[] getSorteFiles(File dirFile) {
        File[] files = dirFile.listFiles();
        if (files != null && files.length > 0) {
            FileWrapper[] fileWrappers = new FileWrapper[files.length];
            for (int i = 0; i < files.length; i++) {
                fileWrappers[i] = new FileWrapper(files[i]);
            }
            Arrays.sort(fileWrappers);
            File[] sortedFiles = new File[files.length];
            for (int i = 0; i < files.length; i++) {
                sortedFiles[i] = fileWrappers[i].getFile();
            }

            return sortedFiles;
        } else {
            return null;
        }
    }

    /**
     * 文件排序类
     */
    static class FileWrapper implements Comparable<Object> {
        private File file;

        public FileWrapper(File file) {
            this.file = file;
        }

        // 倒序排序
        public int compareTo(Object obj) {

            FileWrapper castObj = (FileWrapper) obj;

            if (this.file.getName().compareTo(castObj.getFile().getName()) > 0) {
                return -1;
            } else if (this.file.getName().compareTo(castObj.getFile().getName()) < 0) {
                return 1;
            } else {
                return 0;
            }
        }

        public File getFile() {
            return this.file;
        }
    }
}
