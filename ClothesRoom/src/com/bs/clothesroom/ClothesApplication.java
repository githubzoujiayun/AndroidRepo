package com.bs.clothesroom;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.Application;
import android.os.Environment;

public class ClothesApplication extends Application implements Thread.UncaughtExceptionHandler{

    Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private static final String APP_HOME = Environment.getExternalStorageDirectory().getPath() + "/ClothesRoom";
    private static final String LOG_PATH = APP_HOME +"/log";
    private static final String MAIN_LOG = LOG_PATH + "/main";
    
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(thread,ex) && mDefaultExceptionHandler != null) {
            mDefaultExceptionHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }
    
    private boolean handleException(Thread thread, Throwable ex) {
        try {
            android.util.Log.e("qinchao","thread.name = "+thread.getName());
            android.util.Log.e("qinchao","ex.toString = "+ex.toString());
            
            File log = null;
            PrintWriter fwriter = null;
            if (thread.getName().contains("main")) {
                log  = File.createTempFile("main", "log", new File(MAIN_LOG));
                fwriter = new PrintWriter(log);
            } else {
                log = File.createTempFile("log", "log", new File(LOG_PATH));
                fwriter = new PrintWriter(log);
            }
            ex.printStackTrace(fwriter);
            fwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        File home = new File(APP_HOME);
        if (!home.exists()) {
            home.mkdir();
        }
        File logdir = new File(LOG_PATH);
        if (!logdir.exists()) {
            logdir.mkdir();
        }
        File maindir = new File(MAIN_LOG);
        if (!maindir.exists()) {
            maindir.mkdir();
        }
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    

}
