package com.test.job.android.node;

import android.text.TextUtils;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.CaseManager;
import com.test.job.android.JobCase;
import com.test.job.android.JobCase.ParserListener;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.TestUtils;
import com.test.job.android.node.Node.Event;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParserException;

public class Case extends Event {
	private CaseManager mCaseManager;
	private String mClassName;
	private File mConfigFile;
	private JobCase mJobCase;
	private NodeParser mParser;
	private ResultType mResultType;

	public Case(File xmlFile) {
		mConfigFile = xmlFile;
		mParser = new NodeParser(this, xmlFile.getPath());
		mCaseManager = CaseManager.getInstance();
	}

	public ResultType getResultType() {
		return mResultType;
	}

	public void onParserDone() {
	}

	public void onParserStarted() {
	}

	public boolean onResult(ArrayList<Record> records) {
		if (mJobCase == null || !mJobCase.onResult(records)) {

			if ((records == null) || (records.size() <= 0)) {
				throw new IllegalStateException("Miss records.");
			}

			for (Record record : records) {
				if (!record.satisfied()) {
					return false;
				}
			}

			if (getResultType() == null) {
				return true;
			}

			switch (getResultType()) {
			case VIEW_EXIST:
				break;
			case VIEW_NOT_EXIST:
				break;
			case TEXT_EQUALS:
				break;
			case TEXT_MATHES:

				break;

			default:
				break;
			}
			return true;
		}
		return true;
	}

	public void perform(Node node, PerformListener listener)
			throws UiObjectNotFoundException {
		if ((node instanceof Event))
			((Event) node).dispatchPerform(listener);
	}

	public void setClassName(String className) {
		if (TextUtils.isEmpty(className)) {
			return;
		}
		mClassName = TestUtils.stringVaule(className);

		try {
			Object o = Class.forName(mClassName).newInstance();
			if (o instanceof JobCase) {
				mJobCase = ((JobCase) o);
				mJobCase.setCase(this);
				mJobCase.onCreate();
			} else {
				throw new IllegalArgumentException("invalid className "
						+ mClassName
						+ ", className must be a JobCase instance.");
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void setParserListener(ParserListener listener) {
		mParser.setParserListener(listener);
	}

	public void setPerformListener(PerformListener listener) {
		mParser.setPerformListener(listener);
	}

	public void setResultType(String paramString) {
		mResultType = ResultType.toType(paramString);
	}

	public void start() throws XmlPullParserException, IOException {
		mParser.start();
	}

	public static enum ResultType {
		VIEW_EXIST, VIEW_NOT_EXIST, TEXT_EQUALS, TEXT_MATHES;

		public static ResultType toType(String paramString) {
			if (paramString == null)
				return null;
			if (TextUtils.isEmpty(TestUtils.stringVaule(paramString)))
				throw new IllegalArgumentException(
						"ResultType must have a value in "
								+ Arrays.toString(values()));
			return valueOf(paramString.toUpperCase());
		}

		public String toValue() {
			return toString().toLowerCase();
		}
	}
}
