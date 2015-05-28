package com.test.job.android;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.test.job.android.node.NodeParser;
import com.test.job.android.node.Record;

public class Test extends UiAutomatorTestCase {
	
	public void testParser() throws XmlPullParserException, IOException, UiObjectNotFoundException {
		NodeParser parser = new NodeParser("data/local/tmp/login.xml");
		ArrayList<Record> records = parser.getRecords();
		assertTrue(records.get(0).viewExist());
	}
	
	public void testSearch()  {
		try {
			NodeParser parser = new NodeParser("data/local/tmp/search.xml");
			
			ArrayList<Record> records = parser.getRecords();
			String text1 = records.get(0).getRecordText();
			System.out.println(text1);
			String text2 = records.get(1).getRecordText();
			assertEquals(TestUtils.getInt(text1), TestUtils.getInt(text2));
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void testReflect() {
//		Record.clearCache();
//	}
}
