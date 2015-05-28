package com.test.job.android;

import java.io.FileNotFoundException;
import java.util.List;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.test.job.android.view.Preconditions;
import com.test.job.android.view.Text;

public class TestDemo extends UiAutomatorTestCase {


	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


//	public void testTest() throws UiObjectNotFoundException {
//////		String text = new UiObject(new UiSelector().textMatches("共找到.*个职位")).getText();
//////		System.out.println("test.result = "+text);
////		try{
////		Text text = new Text();
////		text.setTextMatches("共找到.*个职位");
////		Record record = new Record();
////		record.mView = text;
////		record.performEvent();
////		System.out.println(record.getRecordText());
////		System.out.println(record.getView().getText());
////		} finally{
////			TestUtils.executeShellCommand("sh system/bin/uiautomator dump");
////		}
//		Text text = new Text();
//		text.setText("this is a test !");
//		ClickEvent r = new ClickEvent();
//		r.mView = text;
//		r.performEvent();
//	}


	public void testFirst() {
		TestUtils.startHomeActivity();
		try {
			EventParser parser = new EventParser("/data/local/tmp/test.xml");
			List<Record> records = parser.getRecords();

			String text1 = records.get(0).getRecordText();
			String text2 = records.get(1).getRecordText();
			int result = TestUtils.getInt(text1);
			int result2 = TestUtils.getInt(text2);

			assertEquals(result, result2);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
//	public void testLogin() {
//		try {
//			EventParser parser = new EventParser("/data/local/tmp/test.xml");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}

}
