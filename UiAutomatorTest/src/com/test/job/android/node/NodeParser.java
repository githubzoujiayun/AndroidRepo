package com.test.job.android.node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Xml;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.CaseManager;
import com.test.job.android.JobCase.ParserListener;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.Logging;
import com.test.job.android.TestUtils;

public class NodeParser {
    private static final boolean PARSER_DEBUG = true;
    private ParserListener mParserListener;
    private PerformListener mPerformListener;
    private ArrayList<Record> mRecords;
    private Case mRootNode;
    private String mTargetFile = null;
    private InputStream mConfigStream = null;

    public NodeParser() {
    }

    public NodeParser(Case root, String targetFile) {
        mRootNode = root;
        mTargetFile = targetFile;
    }

    public NodeParser(String targetFile) {
        mTargetFile = targetFile;
    }

    public NodeParser(Case root, InputStream inputStream) {
    	mRootNode = root;
    	mConfigStream = inputStream;
    }

	private void onParserDone() throws UiObjectNotFoundException {
        if (!mRootNode.isEnabled()) {
            return;
        }

        mRootNode.onParserDone();
//        TestUtils.startHomeActivity();
        TestUtils.waitForHome();
        if (mParserListener != null)
            mParserListener.onParserDone();
        mRootNode.dispatchPerform(mPerformListener);
        CaseManager.getInstance().onCaseFinished(mRootNode, mRootNode.onResult(mRecords));
        mRootNode = null;
        mTargetFile = null;
        mParserListener = null;
        mRecords.clear();
        return;
    }

    private void onParserStarted() {
        mRootNode.onParserStarted();
        if (mParserListener != null)
            mParserListener.onParserStarted();
    }

    private void parserNodes(InputStream paramInputStream) throws XmlPullParserException,
            IOException, UiObjectNotFoundException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(paramInputStream, "UTF-8");
        int eventType = parser.getEventType();
        Node currentNode = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                mRecords = new ArrayList<Record>();
                onParserStarted();
                break;
            case XmlPullParser.START_TAG:
                String tagName = parser.getName();
                if (PARSER_DEBUG) {
                    Logging.log("start tag name : " + tagName);
                }
                Node node = null;

                if ("jobcase".equals(tagName)) {
                    mRootNode.setResultType(parser.getAttributeValue(null, "resultType"));
                    mRootNode.setClassName(parser.getAttributeValue(null, "className"));
                    node = mRootNode;
                } else if ("click".equals(tagName)) {
                    node = new ClickEvent();
                } else if ("text".equals(tagName)) {
                    TextNode textNode = new TextNode();
                    String text = parser.getAttributeValue(null, "text");
                    String textMatches = parser.getAttributeValue(null, "textMatches");
                    String textContains = parser.getAttributeValue(null, "textContains");
                    String textStartsWith = parser.getAttributeValue(null, "textStartsWith");
                    textNode.setText(text);
                    textNode.setTextMatches(textMatches);
                    textNode.setTextContains(textContains);
                    textNode.setTextStartsWith(textStartsWith);
                    node = textNode;
                } else if ("input".equals(tagName)) {
                    InputEvent inputEvent = new InputEvent();
                    inputEvent.setTypedChars(parser.getAttributeValue(null, "typed"));
                    node = inputEvent;
                } else if ("record".equals(tagName)) {
                    Record record = new Record();
                    mRecords.add(record);
                    node = record;
                } else if ("indexView".equals(tagName)) {
                    IndexView indexView = new IndexView();
                    indexView.setIndexs(parser.getAttributeValue(null, "indexs"));
                    indexView.setRootClass(parser.getAttributeValue(null, "rootClass"));
                    indexView.setRootIndex(parser.getAttributeValue(null, "rootIndex"));
                    indexView.setRootResourceId(parser.getAttributeValue(null,"rootResId"));
                    node = indexView;
                } else if ("wait".equals(tagName)) {
                    WaitEvent waitEvent = new WaitEvent();
                    waitEvent.setWaitType(parser.getAttributeValue(null, "waitType"));
                    waitEvent.setTimeout(parser.getAttributeValue(null, "timeout"));
                    node = waitEvent;
                } else if ("view".equals(tagName)) {
                    node = new ViewImp();
                } else if ("press".equals(tagName)) {
                    PressEvent pressEvent = new PressEvent();
                    pressEvent.setPressKey(parser.getAttributeValue(null, "pressKey"));
                    pressEvent.setKeyCode(parser.getAttributeValue(null, "keyCode"));
                    pressEvent.setMetaState(parser.getAttributeValue(null, "metaState"));
                    node = pressEvent;
                } else if("swipe".equals(tagName)) {
                	SwipeEvent swipeEvent = new SwipeEvent();
                	swipeEvent.setSwipeCount(parser.getAttributeValue(null,"swipeCount"));
                	node = swipeEvent;
                } else {
                    throw new IllegalArgumentException("Unkown tag name : " + tagName);
                }

                if (node != null) {
                    node.setType(Node.ConditionType.toType(parser.getAttributeValue(null,
                            "condition")));
                    node.setNodeId(parser.getAttributeValue(null, "conditionNodeId"));
                    node.setId(parser.getAttributeValue(null, "id"));
                    node.setResourceId(parser.getAttributeValue(null, "resId"));
                    node.setResourceIdMatches(parser.getAttributeValue(null, "resIdMatches"));
                    node.setClickable(parser.getAttributeValue(null, "clickable"));
                    node.setEnabled(parser.getAttributeValue(null, "enabled"));
                    node.setDescription(parser.getAttributeValue(null, "description"));
                    node.setScrollable(parser.getAttributeValue(null, "scrollable"));
                    node.setSwipeDirection(parser.getAttributeValue(null,"swipeDirection"));
                    node.setParent(currentNode);
                    node.setTimeout(parser.getAttributeValue(null, "timeout"));
                    node.setTypedChars(parser.getAttributeValue(null,"typed"));
                    node.setComponentName(parser.getAttributeValue(null,"componentName"));
                    currentNode = node;
                    if (node instanceof TextNode) {
                        TextNode textNode = (TextNode) node;
                        String text = safeNextText(parser);
                        if (!TextUtils.isEmpty(text))
                            textNode.setText(text);
                        currentNode = textNode.getParent();
                    }
                }
                break;
            case XmlPullParser.END_TAG:
                if (PARSER_DEBUG) {
                    Logging.log("end tag name : " + parser.getName());
                }
                if (currentNode != null) {
                    currentNode = currentNode.getParent();
                }
                break;
            case XmlPullParser.END_DOCUMENT:
                break;

            default:
                break;
            }
            eventType = parser.next();
        }
        onParserDone();
        paramInputStream.close();
    }

    private String safeNextText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = parser.nextText();
        if (parser.getEventType() != 3)
            parser.nextTag();
        return text;
    }

    public ArrayList<Record> getRecords() {
        return mRecords;
    }

    public void parserNodes(String filePath) throws XmlPullParserException, IOException,
            UiObjectNotFoundException {
        parserNodes(new FileInputStream(filePath));
    }

    public void setParserListener(ParserListener listener) {
        mParserListener = listener;
    }

    public void setPerformListener(PerformListener listener) {
        mPerformListener = listener;
    }

    public void start() throws XmlPullParserException, IOException, UiObjectNotFoundException {
        if (mTargetFile == null && mConfigStream == null)
            throw new IllegalArgumentException("You must valued a jobcase xml file.");
//        parserNodes(mTargetFile);
        parserNodes(mConfigStream);
    }
}
