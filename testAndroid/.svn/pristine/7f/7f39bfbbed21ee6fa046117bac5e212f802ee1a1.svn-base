package com.job.android.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.job.android.tools.monkeytalk.MonkeyTalkEvent;

public class Text2XML {
	
	
	private final static String SOURCE_SCRIPT = "res/monkeytalk3.script";
	private final static String DST_SCRIPT = "res/1.xml";
	private static final String COMPONENT_ID = "com.job.android:id/";

	private ArrayList<MonkeyTalkEvent> mMonkeyEvents = new ArrayList<MonkeyTalkEvent>();

	public static void main(String args[]) throws FileNotFoundException {
		new Text2XML().readMonkeyTalkScript(new File(SOURCE_SCRIPT));
	}

	private void readMonkeyTalkScript(File f) throws FileNotFoundException {
		// FileOutputStream fos = new FileOutputStream(f);
		Scanner scanner = new Scanner(f);
		String line = null;
		try {
			while (scanner.hasNext()) {
				line = scanner.nextLine();
				MonkeyTalkEvent event = new MonkeyTalkEvent(line);
				mMonkeyEvents.add(event);
			}
		} finally {
			scanner.close();
		}
		parseEvents();
	}

	private void parseEvents() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();
		Element root = doc.createElement("jobcase");
		doc.appendChild(root);
		
//		String parentAction = null;
		for (MonkeyTalkEvent event : mMonkeyEvents) {
			String action = event.getAction();
//			if ("tap".equals(action)) {
//				if (parentAction == null || !"tag".equals(parentAction)) {
//					Element click = doc.createElement("click");
//					root.appendChild(click);
//				}
//				
//				parentAction = action;
//			}
			Element element = null;
			if ("tap".equalsIgnoreCase(action)) {
				if (event.getComponent().equalsIgnoreCase("input")) {
					//输入文本不需要点击，所以直接忽略
					continue;
				}

				element = doc.createElement("click");
				root.appendChild(element);
				Element view = doc.createElement("text");
				setTextAttributes(event, view, doc, element);
				element.appendChild(view);
			} else if ("enterText".equalsIgnoreCase(action)) {
				element = doc.createElement("input");
				root.appendChild(element);
				Element view = doc.createElement("text");
				setTextAttributes(event, view, doc, element);
				view.setAttribute("typed", event.getArgument());
				element.appendChild(view);
			} else if ("selectIndex".equalsIgnoreCase(action)) {
				element = doc.createElement("click");
				root.appendChild(element);
				Element view = doc.createElement("indexView");
				if ("Table".equalsIgnoreCase(event.getComponent())) {
					view.setAttribute("rootClass", "android.widget.ListView");
					view.setAttribute("rootResId", COMPONENT_ID + event.getMonkeyID());
					int index = Integer.parseInt(event.getArgument()) - 1;
					view.setAttribute("indexs", String.valueOf(index));
				} else if ("grid".equalsIgnoreCase(event.getComponent())) {
					view.setAttribute("rootClass", "android.widget.GridView");
					view.setAttribute("resId", COMPONENT_ID+event.getMonkeyID());
					int index = Integer.parseInt(event.getArgument()) - 1;
					view.setAttribute("indexs", String.valueOf(index));
				} else {
					view.setAttribute("unknown", event.getMonkeyID());
				}
				element.appendChild(view);
			} else if ("back".equals(action)) {
				element = doc.createElement("press");
				element.setAttribute("pressKey", "back");
				root.appendChild(element);
			} else if ("swipe".equalsIgnoreCase(action)) {
				element = doc.createElement("swipe");
				root.appendChild(element);
				
				Element view = doc.createElement("text");
				view.setAttribute("swipeDirection", event.getArgument());
				setTextAttributes(event, view, doc, element);
				element.appendChild(view);
			}
			else {
				Comment comment = doc.createComment(event.toString());
				root.appendChild(comment);
			}
			
		}

		try {
			FileOutputStream fos = new FileOutputStream(DST_SCRIPT);
			OutputStreamWriter outwriter = new OutputStreamWriter(fos);
			callWriteXmlFile(doc, outwriter, "utf-8");
			outwriter.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void setTextAttributes(MonkeyTalkEvent event,Element view,Document doc,Element element) {
		if (event.isIdSuite()) {
			view.setAttribute("resId", COMPONENT_ID+event.getMonkeyID());
		} else if(event.isTextSuit()) {
			view.setAttribute("text", event.getMonkeyID());
		} else {
			view.setAttribute("unknown", event.getMonkeyID());
			Comment commont = doc.createComment("unknown attrribute : " + event);
			element.appendChild(commont);
		}
	}

	public static void callWriteXmlFile(Document doc, Writer w, String encoding) {
		try {
			Source source = new DOMSource(doc);

			Result result = new StreamResult(w);

			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			xformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
