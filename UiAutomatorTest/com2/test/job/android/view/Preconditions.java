package com.test.job.android.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import com.test.job.android.TestUtils;

import android.text.TextUtils;
import android.util.Xml;

public class Preconditions {
	
	private static final String FILE_CONDITIONS = "/data/local/tmp/preconditions.xml";
	
	private HashMap<String, Precondition> mConditionMap; 
	private static Preconditions mPreconditions;
	private ViewResource mResource;
	
	private enum PreconditionType {
		VIEW_EXIST, TEXT_EQUALS, TEXT_MATCHES;

		public static PreconditionType toType(String value) {
			// PreconditionType.
			if (TextUtils.isEmpty(TestUtils.stringVaule(value))) {
				throw new IllegalArgumentException(
						"PreconditionType must have a value in view_exist,text_equals or text_matches");
			}
			String upValue = value.toUpperCase();
			return PreconditionType.valueOf(upValue);
		}

		public String toValue() {
			return toString().toLowerCase();
		}
	}
	
	public static Preconditions getInstance() {
		if (mPreconditions == null) {
			mPreconditions = new Preconditions();
		}
		return mPreconditions;
	}
	
	private Preconditions() {
		mResource = ViewResource.getInstance();
		try {
			initPreconditionsFromXML(FILE_CONDITIONS);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Precondition get(String id) {
		return mConditionMap.get(id);
	}

	// 读取preconditions.xml
	private void initPreconditionsFromXML(String path)
			throws FileNotFoundException {
		InputStream inStream = new FileInputStream(path);
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();
			Precondition condition = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					mConditionMap = new HashMap<String, Precondition>();
					break;

				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();

					if ("precondition".equals(name)) {
						condition = new Precondition();
						condition.mId = parser.getAttributeValue(null,"id");
						condition.mText = parser.getAttributeValue(null,"text");
						String type = parser.getAttributeValue(null,"type");
						condition.mType = PreconditionType.toType(type);
						condition.mViewId = parser.getAttributeValue(null,"view_id");
						String exist = parser.getAttributeValue(null,"exist");
						condition.mExist = Boolean.parseBoolean(stringVaule(exist));
					}

				case XmlPullParser.END_TAG:// 结束元素事件
					if (parser.getName().equalsIgnoreCase("precondition")
							&& condition != null) {
						mConditionMap.put(condition.mId, condition);
					}
					break;
				}
				eventType = parser.next();
			}
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class Precondition {
		public String mId;
		public boolean mExist;
		public String mText;
		public String mViewId;
		public PreconditionType mType;

		public String getId() {
			return mId;
		}

		public void setId(String id) {
			this.mId = stringVaule(id);
		}

		public boolean isVisible() {
			return mExist;
		}

		public void setText(String text) {
			mText = stringVaule(text);
		}

		public String getText() {
			return mText;
		}

		public void setView(String viewId) {

		}

		@Override
		public String toString() {
			return "Precondition [mId=" + mId + ", mVisible=" + mExist
					+ ", mText=" + mText + "]";
		}
		
		public boolean satisfied() {
			switch (mType) {
			case VIEW_EXIST:
				View v = mResource.getViewById(mViewId);
				return mExist == v.exist();
			case TEXT_EQUALS:
				
				break;
			case TEXT_MATCHES:
				
				break;

			default:
				break;
			}
			return false;
		}
	}
	

	private String stringVaule(String text) {
		if (TextUtils.isEmpty(text))
			return null;
		if (text.startsWith("\"") && text.endsWith("\"")) {
			text = text.substring(1, text.length() - 1);
		}
		return text;
	}

	@Override
	public String toString() {
		return "Preconditions [mConditionMap=" + mConditionMap + ", mResource="
				+ mResource + "]";
	}
}