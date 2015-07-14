package com.test.job.android.tool;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonkeyTalkEvent {
	
	private String mComponent;
	private String mMonkeyID;
	private String mAction;
	private String mArgument;
	private long mTimeout;
	private long mThinkTime;
	private boolean mShouldFailed;
	
	private static final String ID_SUITE[] = new String[]{
		"Grid","Table","Label","Image","View","Input"
	};
	
	private static final String TEXT_SUITE[] = new String[]{
		"Button"
	};
	
	public MonkeyTalkEvent(final String line) {
		Pattern p = Pattern.compile("\\s+");
		Matcher m = p.matcher(line.trim());
		final int len = 7;
		String vars[] = new String[len];
		int i = 0;
		String model = "";
		while(m.find()) {
			StringBuffer buffer = new StringBuffer(model);
			m.appendReplacement(buffer, "");
			if (buffer.indexOf("\"") >= 0) {
				if (model.length() > 0) {
					vars[i++] =  stringVaule(buffer.toString());
					model = "";
				} else {
					model = buffer.append(m.group()).toString();
				}
			}else {
				vars[i++] = buffer.toString();
				model = "";
			}
		}
		vars[i] = m.appendTail(new StringBuffer()).toString();
//		System.out.println(Arrays.toString(vars));
		mComponent = vars[0];
		mMonkeyID = vars[1];
		mAction = vars[2];
		for (i = 3;i< len;i++) {
			if(vars[i]== null) {
				continue;
			}
			int eqIndex = vars[i].indexOf("=");
			if (vars[i].startsWith("%") && eqIndex > 0) {
				String key = vars[i].substring(1,eqIndex);
				String value = vars[i].substring(eqIndex+1);
				if ("timeout".equals(key)) {
					mTimeout = Long.parseLong(value.trim());
				} else if ("thinktime".equals(key)) {
					mThinkTime = Long.parseLong(value.trim());
				} else if ("shouldfail".equals(key)) {
					mShouldFailed = Boolean.parseBoolean(value.trim());
				}
			} else {
				mArgument = vars[i];
			}
		}
		System.out.println(this);
	}
	
//	public static void main(String[] args) {
//		new MonkeyTalkEvent("Button \"��  ¼\" tap %shouldfail=true %thinktime=1000 %timeout=1000");
//	}

	public String toString() {
		return "MonkeyTalkEvent [mComponent=" + mComponent + ", mMonkeyID="
				+ mMonkeyID + ", mAction=" + mAction + ", mArgument="
				+ mArgument + ", mTimeout=" + mTimeout + ", mThinkTime="
				+ mThinkTime + ", mShouldFailed=" + mShouldFailed + "]";
	}
	
	public static String stringVaule(String text) {
		if (isEmpty(text))
			return null;
		if (text.startsWith("\"") && text.endsWith("\"")) {
			text = text.substring(1, text.length() - 1);
		}
		return text;
	}

	private static boolean isEmpty(String text) {
		if (text == null || text.length() == 0) 
			return true;
		return false;
	}

	public String getComponent() {
		return mComponent;
	}

	public String getMonkeyID() {
		return mMonkeyID;
	}

	public String getAction() {
		return mAction;
	}

	public String getArgument() {
		return mArgument;
	}

	public long getTimeout() {
		return mTimeout;
	}
	
	public boolean isIdSuite() {
		for (String suite : ID_SUITE) {
			if (suite.equalsIgnoreCase(mComponent)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isTextSuit() {
		for (String suite : TEXT_SUITE) {
			if (suite.equalsIgnoreCase(mComponent)) {
				return true;
			}
		}
		return false;
	}
}
