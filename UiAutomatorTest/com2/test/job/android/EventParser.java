package com.test.job.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.view.Preconditions;
import com.test.job.android.view.Preconditions.Precondition;

public class EventParser {
	
	private static final boolean EVENT_DEBUG = true;
	
	private List<Event> mEvents;
	private List<Record> mRecords;
	private Preconditions mPreconditions;
	
	public EventParser(String path) throws FileNotFoundException {
		mEvents = new ArrayList<Event>();
		mRecords = new ArrayList<Record>();
		mPreconditions = Preconditions.getInstance();
		InputStream in = new FileInputStream(path);
		readXML(in);
	}

	// 读取XML
	private List<Event> readXML(InputStream inStream) {

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();

			Event currentEvent = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					mEvents.clear();
					mRecords.clear();
					break;

				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();
					if (EVENT_DEBUG) {
						System.out.println("start tag name : " + name);
					}
					if (name.equalsIgnoreCase("click")) {
						currentEvent = new ClickEvent();
					} else if (name.equalsIgnoreCase("wait")) {
						currentEvent = new WaitEvent();
					} else if (name.equalsIgnoreCase("record")) {
						currentEvent = new Record();
					} else if (name.equalsIgnoreCase("res_id_matches")) {
						currentEvent.setIdMatches(parser.nextText());
					} else if (name.equalsIgnoreCase("res_id")) {
						currentEvent.setResourceId(parser.nextText());
					} else if (name.equalsIgnoreCase("text")) {
						currentEvent.setText(parser.nextText());
					} else if (name.equalsIgnoreCase("text_matches")) {
						currentEvent.setTextMatches(parser.nextText());
					} else if (name.equalsIgnoreCase("text_contains")) {
						currentEvent.setTextContains(parser.nextText());
					} else if (name.equalsIgnoreCase("text_startswith")) {
						currentEvent.setTextStartsWith(parser.nextText());
					} else if (name.equalsIgnoreCase("precondition")) {
						Precondition condition = mPreconditions.get(parser
								.nextText());
						currentEvent.setPrecondition(condition);
					} else if (name.equalsIgnoreCase("timeout")) {
						currentEvent.setTimeout(parser.nextText());
					} else if("wait_type".equalsIgnoreCase(name)) {
						currentEvent.setWaitType(parser.nextText());
					} else if ("view_id".equalsIgnoreCase(name)) {
						currentEvent.setViewId(parser.nextText());
					}
					break;

				case XmlPullParser.END_TAG:// 结束元素事件
					String endTag = parser.getName();
					if (EVENT_DEBUG) {
						System.out.println("end tag name : " + endTag);
					}
					if (currentEvent != null) {
						if (endTag.equalsIgnoreCase("click")
								|| endTag.equalsIgnoreCase("wait")) {
							mEvents.add(currentEvent);
						} else if (endTag.equalsIgnoreCase("record")) {
							mEvents.add(currentEvent);
							mRecords.add((Record)currentEvent);
						}
						currentEvent.createView();
						currentEvent = null;
						break;
					}
				}
				eventType = parser.next();
			}
			inStream.close();
			onParserComplete();
			return mEvents;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private void onParserComplete() {
		for (Event event: mEvents) {
			try {
				event.performEvent();
			} catch (UiObjectNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Event> getEvents() {
		return mEvents;
	}
	
	public List<Record> getRecords(){
		return mRecords;
	}
}
