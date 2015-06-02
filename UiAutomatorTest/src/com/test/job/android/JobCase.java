package com.test.job.android;

import com.test.job.android.node.Case;
import com.test.job.android.node.Node;
import com.test.job.android.node.Record;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class JobCase {
	private Case mCase;

	public abstract void onCreate();

	/**
	 * 用于统计Case的结果
	 * @param records
	 * @return 完成结果判断返回true,否则返回false
	 */
	public boolean onResult(ArrayList<Record> records) {
		return false;
	}

	public void setCase(Case paramCase) {
		this.mCase = paramCase;
	}

	public void setParserListener(ParserListener paramParserListener) {
		this.mCase.setParserListener(paramParserListener);
	}

	public void setPerformListener(PerformListener paramPerformListener) {
		this.mCase.setPerformListener(paramPerformListener);
	}

	public static abstract interface ParserListener {
		public abstract void onParserDone();

		public abstract void onParserStarted();
	}

	public static abstract interface PerformListener {
		public abstract boolean onPerform(Node node);

		public abstract void onPerformDone(Node node);

		public abstract void onPerformStarted(Node node);
	}
}