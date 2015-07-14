package com.test.job.android;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import com.test.job.android.node.Case;
import com.test.job.android.node.IWork;

public class CaseManager {
	
	private static CaseManager sCaseManager;
	private HashMap<String, Case> mCaseCache = new HashMap<String, Case>();
	private File mHomeDirectory;
	private File mLogDir;
	private LinkedList<Case> mQueue = new LinkedList<Case>();
	private File mResDir;
	private Result mResult;
	private Logging mLogging;
	private static IWork mWork;

	private CaseManager() {
		if (mHomeDirectory == null) {
			mHomeDirectory = new File(mWork.getHomePath());
			if (!mHomeDirectory.exists())
				mHomeDirectory.mkdirs();
			mResDir = new File(mWork.getResourcePath());
			if (!mResDir.exists())
				mResDir.mkdirs();
			mLogDir = new File(mWork.getLogPath());
			if (!mLogDir.exists())
				mLogDir.mkdirs();
		}
		mResult = new Result();
		mLogging = new Logging(mWork);
	}

	public static CaseManager getInstance(IWork work) {
		if (sCaseManager == null) {
			mWork = work;
			sCaseManager = new CaseManager();
		}
		return sCaseManager;
	}
	
	public static CaseManager getInstance() {
		return sCaseManager;
	}

	public static void logException(Exception paramException) {
	}

	private void start() {
		Case localCase = null;
		mLogging.createNewDir();
		while((localCase = mQueue.poll()) != null) {
			try {
				mLogging.createNewLog(localCase.getTag());
				localCase.start();
			} catch (Throwable e) {
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
		Logging.logInfo("Succed %d,  Failed %d,  Error %d\n",mResult.succed,mResult.failed,mResult.error);
	}

	public void cacheNewCase(String key, Case jobcase) {
		mCaseCache.put(key, jobcase);
	}

	public void onCaseFinished(Case jobCase, boolean succed) {
		if (succed) {
			mResult.succed++;
			Logging.logInfo("Case succed!");
		} else {
			mResult.failed++;
			Logging.logInfo("Case failed!");
		}
	}

	public void removeCase(String paramString) {
		mCaseCache.remove(paramString);
	}

	public void startCases() {
		String[] configs = null;

		if (configs == null) {
			configs = mWork.listConfigs();
		}
		for (String config : configs) {
			Case localCase = getJobCase(config);
			mQueue.offer(localCase);
		}
		start();
	}

	private Case getJobCase(String config) {
//		return new Case(File.);
		return new Case(mWork.getInputStream(config),config);
	}

	class Result {
		int error;
		int failed;
		int succed;
	}
	
	public Logging getLogging() {
		return mLogging;
	}

	public IWork getWork() {
		return mWork;
	}
}