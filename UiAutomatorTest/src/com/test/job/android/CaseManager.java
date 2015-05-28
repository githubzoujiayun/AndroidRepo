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
	}

	public static CaseManager getInstance() {
		if (sCaseManager == null)
			sCaseManager = new CaseManager();
		return sCaseManager;
	}

	public static void logException(Exception paramException) {
	}

	private void start() {
		while (true) {
			Case localCase = (Case) mQueue.poll();
			if (localCase == null)
				break;
			try {
				localCase.start();
			} catch (Exception e) {
				e.printStackTrace();
				Result localResult = mResult;
				localResult.error++;
			}
		}
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
			mQueue.add(localCase);
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
}

/*
 * Location: C:\Users\chao.qin\Desktop\apktool\dex2jar\classes-dex2jar.jar
 * Qualified Name: com.test.job.android.CaseManager JD-Core Version: 0.6.1
 */