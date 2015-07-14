package com.test.job.android.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Text2XML {

	private static final String COMPONENT_ID = "com.job.android:id/";

	private ArrayList<MonkeyTalkEvent> mMonkeyEvents = new ArrayList<MonkeyTalkEvent>();

	

	public interface ParserListener {
		void onParserComplete(String fileName);
	}

	public void parserMonkeyTalkScript(File inputFile ,File outFile) throws FileNotFoundException {
		
		Scanner scanner = new Scanner(new FileInputStream(inputFile));
		String line = null;
		try {
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				MonkeyTalkEvent event = new MonkeyTalkEvent(line);
				mMonkeyEvents.add(event);
			}
		} finally {
			scanner.close();
			scanner = null;
		}
		parseEvents(outFile);
	}

	private void parseEvents(File outfile) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();
		Element root = doc.createElement("jobcase");
		doc.appendChild(root);

		// String parentAction = null;
		for (MonkeyTalkEvent event : mMonkeyEvents) {
			String action = event.getAction();
			Element element = null;
			if ("tap".equalsIgnoreCase(action)) {
				if (event.getComponent().equalsIgnoreCase("input")) {
					// 输入文本不需要点击，所以直接忽略
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
					view.setAttribute("rootResId",
							COMPONENT_ID + event.getMonkeyID());
					int index = Integer.parseInt(event.getArgument()) - 1;
					view.setAttribute("indexs", String.valueOf(index));
				} else if ("grid".equalsIgnoreCase(event.getComponent())) {
					view.setAttribute("rootClass", "android.widget.GridView");
					view.setAttribute("resId",
							COMPONENT_ID + event.getMonkeyID());
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
			} else if ("select".equals(action)) {
				element = doc.createElement("click");
				root.appendChild(element);

				Element view = doc.createElement("text");
				String text = event.getArgument();
				if (text != null && text.length() > 0) {
					text = text.split(";")[0];
				}
				view.setAttribute("text", text);
				element.appendChild(view);
			} else {
				Comment comment = doc.createComment(event.toString());
				root.appendChild(comment);
			}

		}

		try {
			FileOutputStream fos = new FileOutputStream(outfile);
			OutputStreamWriter outwriter = new OutputStreamWriter(fos);
			format(doc, outwriter);
//			callWriteXmlFile(doc, outwriter, "utf-8");
			outwriter.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void format(Document document, Writer out) {
		try {
			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(4);
			format.setEncoding("utf-8");
			format.setLineSeparator("\r\n");
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void setTextAttributes(MonkeyTalkEvent event, Element view,
			Document doc, Element element) {
		if (event.isIdSuite()) {
			view.setAttribute("resId", COMPONENT_ID + event.getMonkeyID());
		} else if (event.isTextSuit()) {
			view.setAttribute("text", event.getMonkeyID());
		} else {
			view.setAttribute("unknown", event.getMonkeyID());
			Comment commont = doc
					.createComment("unknown attrribute : " + event);
			element.appendChild(commont);
		}
	}

	public static void callWriteXmlFile(Document doc, Writer w, String encoding) {
		try {

			Source source = new DOMSource(doc);
			Result result = new StreamResult(w);
//
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

		// textEditor.getDocumentProvider().getDocument(textEditor).
	private static String getJobTestText(FileEditorInput fei,File outfile) {
		IEditorInput editInput = fei;
		if (editInput instanceof FileEditorInput) {
			FileEditorInput fileInput = (FileEditorInput) editInput;
			Text2XML xmlTool = new Text2XML();
			IFile file = fileInput.getFile();
			IPath path = file.getFullPath();
			IPath location = file.getLocation();
			System.out.println("file.getName : " + file.getName());
			System.out.println("file.getFileExtension : "
					+ file.getFileExtension());
			System.out.println("file.getLocation : " + file.getLocation());
			System.out.println("file.getFullPath : " + file.getFullPath());

			System.out.println("file.getWorkspace : " + file.getWorkspace());
			System.out.println("file.getProjectRelativePath : "
					+ file.getProjectRelativePath());

			try {
				xmlTool.parserMonkeyTalkScript(location.toFile(),outfile);
				// System.out.println("getJobText : \n" + text);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	public static void generateXmlScript(FileEditorInput fei,
			IWorkbenchPartSite editorSite) {
		File f = new File((fei).getPath().toString());
		f = f.getParentFile();

		String fileName = (fei).getName().substring(0,
				(fei).getName().length() - 3)
				+ ".xml";
		File outfile = new File(f.getAbsolutePath() + "/" + fileName);
		if (outfile.exists()) {
			MessageBox dialog = new MessageBox(editorSite.getShell(),
					SWT.ICON_ERROR | SWT.CANCEL | SWT.OK);
			dialog.setText("Monkey Talk Editor");
			dialog.setMessage("The file " + fileName
					+ " already exists. Do you want to overwrite it?");
			if (dialog.open() != SWT.OK)
				return;
		}
		
		getJobTestText(fei,outfile);
		
		try {
			(fei).getFile().getProject()
					.refreshLocal(IResource.DEPTH_INFINITE, null);

			IEditorPart ieditorpart = editorSite.getPage().getActiveEditor();
			IFile fileToBeOpened = ((IFileEditorInput) ieditorpart
					.getEditorInput()).getFile().getProject().getFile(fileName);

			IEditorInput editorInput = new FileEditorInput(fileToBeOpened);
			editorSite.getPage().openEditor(editorInput,
					"org.eclipse.wst.sse.ui.StructuredTextEditor");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
