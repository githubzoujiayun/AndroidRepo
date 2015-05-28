package com.test.job.android.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.TestUtils;
import com.test.job.android.node.Node.ConditionType;
import com.test.job.android.node.Node.Event;




import android.text.TextUtils;
import android.util.Xml;

public class NodeParser {
	
	private static final boolean PARSER_DEBUG = true;
	
	private ArrayList<Record> mRecords;
	private Node mRootNode;
	private ParserListener mParserListener;
	
	public NodeParser() {}
	
	public NodeParser(String fileName) throws XmlPullParserException, IOException {
		parserNodes(fileName);
	}
	
	public void parserNodes(String fileName) throws XmlPullParserException, IOException {
		parserNodes(new FileInputStream(fileName));
	}
	
	private String safeNextText(XmlPullParser parser)
	        throws XmlPullParserException, IOException {
	    String result = parser.nextText();
	    if (parser.getEventType() != XmlPullParser.END_TAG) {
	        parser.nextTag();
	    }
	    return result;
	}

	private void parserNodes(InputStream in) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(in, "UTF-8");
		int eventType = parser.getEventType();
		Node currentNode = null;
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				mRecords = new ArrayList<Record>();
				onParserStart();
				break;
			case XmlPullParser.START_TAG:{
				String tagName = parser.getName();
				if (PARSER_DEBUG) {
					System.out.println("start tag name : "+tagName);
				}
				Node node = null;
				if ("event-tree".equals(tagName)) {
					mRootNode = new Event();
					node = mRootNode;
				} else if ("click".equals(tagName)) {
					ClickNode clickNode = new ClickNode();
					node = clickNode;
				} else if ("text".equals(tagName)) {
					TextNode textNode = new TextNode();
					String text = parser.getAttributeValue(null,"text");
					String textMatches = parser.getAttributeValue(null,"text_matches");
					String textContains = parser.getAttributeValue(null,"text_contains");
					String textStartsWith = parser.getAttributeValue(null,"text_startswith");
					textNode.setText(text);
					textNode.setTextMatches(textMatches);
					textNode.setTextContains(textContains);
					textNode.setTextStartsWith(textStartsWith);
					node = textNode;
				} else if ("input".equals(tagName)) {
					InputNode inputNode = new InputNode();
					String input = parser.getAttributeValue(null,"type");
					inputNode.setTypedChars(input);
					node = inputNode;
				} else if ("record".equals(tagName)) {
					Record record = new Record();
					mRecords.add(record);
					node = record;
				} else if ("index_view".equals(tagName)) {
					IndexView indexView = new IndexView();
					indexView.setIndexs(parser.getAttributeValue(null,"indexs"));
					indexView.setRootClass(parser.getAttributeValue(null,"root_class"));
					indexView.setRootIndex(parser.getAttributeValue(null,"root_index"));
					node = indexView;
				} else if ("wait".equals(tagName)) {
					WaitNode wait = new WaitNode();
					wait.setWaitType(parser.getAttributeValue(null,"type"));
					wait.setTimeout(parser.getAttributeValue(null,"timeout"));
					node = wait;
				}
				else {
					System.out.printf("Unkown tag name %s.\n",tagName);
				}
				if (node != null) {
					String condition = parser.getAttributeValue(null,
							"condition");
					node.setType(ConditionType.toType(condition));
					String nodeId = parser.getAttributeValue(null,
							"condition_node_id");
					node.setNodeId(nodeId);
					node.setId(parser.getAttributeValue(null, "id"));
					node.setResourceId(parser.getAttributeValue(null,"res_id"));
					node.setResourceIdMatches(parser.getAttributeValue(null,"res_id_matches"));
					node.setClickable(parser.getAttributeValue(null,"clickable"));
					node.setParent(currentNode);
					currentNode = node;
					if (node instanceof TextNode) {
						TextNode text = (TextNode)node;
						String s = null;
						if (!TextUtils.isEmpty(s=safeNextText(parser))) {
							text.setText(s);
						}
						currentNode = text.getParent();
					}
				} else {
					System.out.println("empty node :"+tagName);
				}
				break;
			}
			case XmlPullParser.END_TAG:
				if (PARSER_DEBUG) {
					System.out.println("end tag name : "+parser.getName());
				}
				if (currentNode != null) {
					currentNode = currentNode.getParent();
				}
				break;
			default:
				break;
			}
			
			eventType = parser.next();
		}
		onParserDone();
		in.close();
	}
	
	private void onParserStart() {
		if (mParserListener != null) {
			mParserListener.onParserStart();
		}
	}
	
	private void onParserDone() {
		TestUtils.startHomeActivity();
		TestUtils.waitForHome();
		try {
			mRootNode.perform();
		} catch (UiObjectNotFoundException e) {
			e.printStackTrace();
		}
		
		if (mParserListener != null) {
			mParserListener.onParserDone();
		}
	}
	
	public void setParserListener(ParserListener listener) {
		mParserListener = listener;
	}


	public interface ParserListener {
		public void onParserStart();
		public void onParserDone();
	}
	
	public ArrayList<Record> getRecords() {
		return mRecords;
	}
}
