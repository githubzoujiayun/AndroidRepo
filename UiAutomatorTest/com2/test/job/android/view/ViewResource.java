package com.test.job.android.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.test.job.android.ClickEvent;

import android.util.Xml;

public class ViewResource {
	
	public static final String VIEW_RESOURCE_PATH = "/data/local/tmp/views.xml";
	
	private HashMap<String, View> mViewMap = new HashMap<String, View>();
	private static ViewResource mViewResource;
	
	public static ViewResource getInstance() {
		if (mViewResource == null) {
			mViewResource = new ViewResource();
		}
		return mViewResource;
	}

	private ViewResource() { 
		try {
			initViewsFromXML(VIEW_RESOURCE_PATH);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File "+VIEW_RESOURCE_PATH+" is not exist !",e);
		}
	}
	
	public View getViewById(String viewId) {
		return mViewMap.get(viewId);
	}

	// 读取views.xml
	private void initViewsFromXML(String path) throws FileNotFoundException {
		InputStream inStream = new FileInputStream(path);
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();
			View view = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					break;

				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();
					if ("index_view".equals(name)) {
						IndexView indexView= new IndexView();
						indexView.mId = parser.getAttributeValue(null, "id");
						indexView.mRootClass = parser.getAttributeValue(null,"root_class");
						indexView.mRootIndex = parser.getAttributeValue(null,"root_index");
						indexView.setIndexs(parser.getAttributeValue(null,"indexs"));
						view = indexView;
					} 

				case XmlPullParser.END_TAG:// 结束元素事件
					mViewMap.put(view.mId,view);
					break;
				}
				eventType = parser.next();
			}
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ViewResource [mViewMap=" + mViewMap + "]";
	}
	
}
