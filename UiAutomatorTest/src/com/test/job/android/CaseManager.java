package com.test.job.android;

import com.test.job.android.node.Case;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;

public class CaseManager {
	private static final String JOBTEST_HOME = "/data/local/tmp/jobtest/";
	private static final String LOG_PATH = "/data/local/tmp/jobtest/logs/";
	private static final String RESOURCE_PATH = "/data/local/tmp/jobtest/res/";
	private static CaseManager sCaseManager;
	private HashMap<String, Case> mCaseCache = new HashMap<String, Case>();
	private File mHomeDirectory;
	private File mLogDir;
	private LinkedList<Case> mQueue = new LinkedList<Case>();
	private File mResDir;
	private Result mResult;
	private Logging mLogging;

	private CaseManager() {
		if (mHomeDirectory == null) {
			mHomeDirectory = new File(JOBTEST_HOME);
			if (!mHomeDirectory.exists())
				mHomeDirectory.mkdirs();
			mResDir = new File(RESOURCE_PATH);
			if (!mResDir.exists())
				mResDir.mkdirs();
			mLogDir = new File(LOG_PATH);
			if (!mLogDir.exists())
				mLogDir.mkdirs();
		}
		mResult = new Result();
		mLogging = new Logging();
	}

	public static CaseManager getInstance() {
		if (sCaseManager == null)
			sCaseManager = new CaseManager();
		return sCaseManager;
	}

	public static void logException(Exception paramException) {
	}

	private void start() {
		Case localCase = null;
		mLogging.createNewDir();
		while((localCase = mQueue.poll()) != null) {
			try {
				mLogging.createNewLog(localCase.getConfigFile().getName());
				localCase.start();
			} catch (Exception e) {
				Logging.logException(e);
				Result localResult = mResult;
				localResult.error++;
				String name = e.getMessage();
				mLogging.takeScreenshot("exception-" + name);
			}finally {
				mLogging.closeLog();
			}
		}
		onFinished();
	}

	private void onFinished() {
		System.out.printf("Succed %d,  Failed %d,  Error %d\n",mResult.succed,mResult.failed,mResult.error);
	}

	public void cacheNewCase(String key, Case jobcase) {
		mCaseCache.put(key, jobcase);
	}

	public void onCaseFinished(Case jobCase, boolean succed) {
		if (succed) {
			mResult.succed++;
		} else {
			mResult.failed++;
		}
	}

	public void removeCase(String paramString) {
		mCaseCache.remove(paramString);
	}

	public void startCases() {
		File[] childs = mResDir.listFiles(new FilenameFilter() {
			public boolean accept(File f, String fname) {
				return fname.endsWith(".xml");
			}
		});
		for (File f : childs) {
			Case localCase = getJobCase(f.getPath());
			mQueue.offer(localCase);
		}
		start();
	}

	private Case getJobCase(String path) {
		return new Case(new File(path));
	}

	class Result {
		int error;
		int failed;
		int succed;
	}
	
	public Logging getLogging() {
		return mLogging;
	}
}