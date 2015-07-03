package com.test.job.android;

import java.util.ArrayList;

import com.test.job.android.node.Case;
import com.test.job.android.node.Node;
import com.test.job.android.node.Record;

public abstract class JobCase {
    
    public static final int RESULT_FAILED = 0;
    public static final int RESULT_SUCCED = 1;
    public static final int RESULT_DEFAULT = 2;
    
    private Case mCase;

	public abstract void onCreate();

	/**
	 * 用于统计Case的结果
	 * @param records
	 * @return 成功返回 {@link #RESULT_SUCCED} ,
	 *         失败返回 {@link #RESULT_FAILED},
	 *         使用默认判断返回 {@link #RESULT_DEFAULT}
	 */
	public int onResult(ArrayList<Record> records) {
		return RESULT_DEFAULT;
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