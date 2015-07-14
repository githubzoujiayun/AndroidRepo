package com.test.job.android.instrumentation;

import com.test.job.android.Logging;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

public class JobInstrumentationTestRunner extends InstrumentationTestRunner {

	public void onCreate(Bundle arguments) {
		Logging.log("argument : "+arguments);
		String testClassesArg = arguments.getString("class");
		Logging.log("testClassesArg : "+testClassesArg);
		if (testClassesArg == null) {
			arguments.putString("class", "com.test.job.android.RobotiumStarter");
		}
		super.onCreate(arguments);
	}
}
