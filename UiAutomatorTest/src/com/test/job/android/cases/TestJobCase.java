package com.test.job.android.cases;

import com.test.job.android.JobCase;
import com.test.job.android.JobCase.ParserListener;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.node.Node;
import com.test.job.android.node.Record;
import java.util.ArrayList;

public class TestJobCase extends JobCase implements ParserListener,
		PerformListener {
	
	public void onCreate() {
		setParserListener(this);
		setPerformListener(this);
	}

	public void onParserDone() {
		System.out.println("onParserDone");
	}

	public void onParserStarted() {
		System.out.println("onParserStarted");
	}

	public boolean onPerform(Node paramNode) {
		System.out.println("onPerform : " + paramNode);
		return false;
	}

	public void onPerformDone(Node paramNode) {
		System.out.println("onPerformDone : " + paramNode);
	}

	public void onPerformStarted(Node paramNode) {
		System.out.println("onPerformStarted : " + paramNode);
	}

	public boolean onResult(ArrayList<Record> records) {
		boolean succed = super.onResult(records);
		System.out.println("onResult : " + succed);
		return succed;
	}
}