/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2012 Gorilla Logic, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.gorillalogic.fonemonkey.automators;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.gorillalogic.fonemonkey.Log;
import com.gorillalogic.fonemonkey.web.HtmlElement;
import com.gorillalogic.fonemonkey.web.WebAutomationManager;
import com.gorillalogic.fonemonkey.web.WebChromeClientWrapper;
import com.gorillalogic.fonemonkey.web.WebFilterBase;
import com.gorillalogic.fonemonkey.web.WebViewRecorder;
import com.gorillalogic.monkeytalk.automators.AutomatorConstants;

public class WebViewAutomator extends ViewAutomator {
	private static long JS_TIMEOUT = 100;
	private static boolean jsPopupOpen;
	private WebViewRecorder recorder;
	private static ArrayBlockingQueue<Result> jsResult = new ArrayBlockingQueue<Result>(1);
	private static String[] aliases = { "WebView", "Web" };

	static {
		Log.log("Initializing WebViewAutomator");
	}

	@Override
	public String getComponentType() {
		return "WebView";
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public Class<?> getComponentClass() {
		return WebView.class;
	}

	public WebView getWebView() {
		return (WebView) getComponent();
	}

	public WebViewRecorder getRecorder() {
		return recorder;
	}

	@Override
	public boolean installDefaultListeners() {
		getWebView().getSettings().setJavaScriptEnabled(true);
		getWebView().getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		recorder = new WebViewRecorder(getWebView());

		// not supported in 4.2.2
		// new WebViewClientWrapper(getWebView());
		new WebChromeClientWrapper(this);
		return super.installDefaultListeners();
	}

	@Override
	public boolean forSubtypeOf(String componentType) {
		// A webview might contain the componentType
		return true;
	}

	// private static final Object syncObject = new Object();

	public static String monkeyTalkJs() {
		String custom = "";

		// include custom monkeytalk if we find it
		try {
			custom = "if (CustomMonkeyTalk == undefined) {" + "var CustomMonkeyTalk = true;"
					+ fileToString("monkeytalk-custom-web.js") + "}";
		} catch (IllegalArgumentException e) {
			// ignore
		}
		return fileToString("monkeytalk.js") + fileToString("wgxpath.install.js")
				+ fileToString("monkeytalk-web.js") + custom;
	}

	/**
	 * Run the supplied Javascript statements in a WebView and (synchronously) return any returned
	 * value as a string.
	 * 
	 * @param script
	 * @return
	 */
	public String runJavaScript(final String script) {
		if (isJsPopupOpen()) {
			return null;
		}
		Log.log("Running " + script);

		AutomationManager.runOnUIThread(new Runnable() {
			@Override
			public void run() {
				final String lib = monkeyTalkJs();
				// String s =
				// "javascript:window.location = \"http://mtdummy?monkeytalkresult=\" + (function(){"
				// + lib + script + "})()";
				//
				runJS(script, lib);
			}

		});
		String result = waitForJSResult(JS_TIMEOUT);
		return result;
	}

	/**
	 * Exec the supplied Javascript statements without returning result
	 * 
	 * @param script
	 */
	public void execJavaScript(final String script, final boolean shouldTagResult) {
		if (isJsPopupOpen()) {
			return;
		}
		Log.log("Running " + script);

		AutomationManager.runOnUIThread(new Runnable() {
			@Override
			public void run() {
				final String lib = monkeyTalkJs();
				// String s =
				// "javascript:window.location = \"http://mtdummy?monkeytalkresult=\" + (function(){"
				// + lib + script + "})()";
				//
				runJS(script, lib, shouldTagResult);
			}

		});
	}

	public void execJavaScript(final String script) {
		execJavaScript(script, true);
	}

	private void runJS(final String script, final String lib) {
		runJS(script, lib, true);
	}

	private void runJS(final String script, final String lib, boolean shouldTagResponse) {

		String tag = shouldTagResponse ? "monkeytalk:" : "";
		// The following 2 lines actually execute the function. (We monitor the console log
		// and execute log messages starting with "monkeytalk:")
		// String s = "javascript:console.log('" + tag + "' + (function(){" + lib + script +
		// "})())";

		// console.log fails to return with certain js scripts linked into web
		// use alert and onJsAlert in WebChromeClientWrapper instead
		String s = "alert('" + tag + "' + (function(){" + lib + script + "})())";

		try {
			//
			// webview's javascript injection
			//
			Method methodToFind = null;
			try {
				methodToFind = getWebView().getClass().getMethod("evaluateJavascript",
						new Class[] { String.class, ValueCallback.class });
			} catch (NoSuchMethodException nsme) {
				methodToFind = null;
			}
			// Invoke it if present (API Level 19 only)
			if (methodToFind == null) {
				// "evaluateJavascript" method not found.
				//
				// API Level <= 18 detected
				//
				getWebView().loadUrl("javascript:" + s);
			} else {
				//
				// API Level 19 detected
				//
				// Method found. You can invoke the method like
				methodToFind.invoke(getWebView(), s, null);
			}
		} catch (NullPointerException e) {
			// null pointer in WebViewClassic?
			// put results so we do not block component tree dump
			Log.log(e);
			try {
				jsResult.put(new Result(null, true));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (IllegalArgumentException iare) {
			iare.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		} catch (ExceptionInInitializerError eie) {
			eie.printStackTrace();
		}
	}

	private String waitForJSResult(long timeout) {
		Result result = null;
		try {
			result = jsResult.poll(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

		}
		if (result == null) {
			throw new IllegalArgumentException("Timed out waiting for javascript");
		}
		if (result.error) {
			throw new IllegalArgumentException(result.result);
		}
		return result.result;
	}

	/**
	 * Execute a JS expression that returns a list of html elements
	 * 
	 * @param jsElemsExpr
	 * @return HtmlElement represenations of the corresponding html elements in the webview
	 */
	public List<HtmlElement> findHtmlElements(String jsElemsExpr) {
		// String encodedResult = this.runJavaScript("return window.monkeytalk.encodeElements("
		// + jsElemsExpr + ");");

		String encodedResult = runJavaScript("return MonkeyTalk.getElement(" + jsElemsExpr + ");");
		return this.decodeJsResult(encodedResult);
	}

	public List<HtmlElement> getAllElementsForComponentType(String componentType) {
		List<HtmlElement> list = new ArrayList<HtmlElement>();
		WebFilterBase filter = (WebFilterBase) WebAutomationManager.getFilter(getWebView(),
				componentType);
		String xpath = filter.getXpathNode().replace("'", "\"");
		String encodedResult = runJavaScript("return MonkeyTalk.getElementsFromXpath('" + xpath
				+ "');");

		try {
			JSONArray jsonArray = new JSONArray(encodedResult);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				HtmlElement element = decodeJsonElement(object.toString());
				list.add(element);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	protected void assureMonkeyTalkInjected() {
		// make sure MonkeyTalk is linked into the webview
		String result = runJavaScript("return typeof MonkeyTalk != 'undefined'");
		boolean isMonkeyTalkInjected = Boolean.valueOf(result);
		if (!isMonkeyTalkInjected) {
			AutomationManager.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					WebViewRecorder.attachJs(getWebView());
				}
			});
		}
	}

	public HtmlElement findElementByXpath(String xpathExpression) {
		return this.findElementByXpath(1, xpathExpression);
	}

	public HtmlElement findElementByXpath(int ordinal, String xpathExpression) {
		// String result = runJavaScript("return XPathResult.ANY_TYPE");
		// String result =
		// runJavaScript("return document.evaluate(\"//*[@id='userInput']\", document, null, XPathResult.ANY_TYPE, null);");

		String json = runJavaScript("return MonkeyTalk.getElementFromXpathWithOrdinal(\""
				+ xpathExpression + "\"," + ordinal + ");");

		if (json == null || json.equalsIgnoreCase("null")) {
			Log.log("Unable to find " + componentType + " \"" + monkeyId
					+ "\" with xpathExpression=" + xpathExpression);
			return null;
		}

		HtmlElement element = decodeJsonElement(json);
		element.putAttr("xpath", xpathExpression);

		return element;
	}

	public HtmlElement findElement(String jsElemsExpr, String componentType, String monkeyId) {
		// String encodedResult = this.runJavaScript("return window.monkeytalk.encodeElements("
		// + jsElemsExpr + ");");

		// wait until the webview has completely loaded to playback
		if (getWebView().getProgress() < 100) {
			throw new IllegalArgumentException("Unable to find " + componentType + " \"" + monkeyId
					+ "\"");
		}

		Log.log("expr: " + jsElemsExpr);
		String json = runJavaScript("return MonkeyTalk.getElement(" + jsElemsExpr + ",'" + monkeyId
				+ "','" + componentType + "');");

		if (json == null || json.equalsIgnoreCase("null")) {
			Log.log("Unable to find " + componentType + " \"" + monkeyId + "\" with jsElemsExpr="
					+ jsElemsExpr);
			return null;
		}

		return decodeJsonElement(json);
	}

	public HtmlElement findCell(HtmlElement table, String cellId) {
		// String encodedResult = this.runJavaScript("return window.monkeytalk.encodeElements("
		// + jsElemsExpr + ");");

		// wait until the webview has completely loaded to playback
		String jsElemsExpr = "document.getElementsByTagName(\'" + table.getTagName() + "')";
		if (getWebView().getProgress() < 100) {
			throw new IllegalArgumentException("Unable to find " + componentType + " \"" + monkeyId
					+ "\"");
		}

		Log.log("expr: " + jsElemsExpr);
		String json = runJavaScript("return MonkeyTalk.getCell(" + jsElemsExpr + ",'"
				+ table.getMonkeyId() + "','" + cellId + "');");

		if (json == null || json.equalsIgnoreCase("null")) {
			Log.log("not found");
			return null;
		}

		return decodeJsonElement(json);
	}

	public HtmlElement decodeJsonElement(String json) {
		HtmlElement element = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			element = new HtmlElement(getWebView());

			Iterator<?> keys = jsonObject.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				element.putAttr(key, jsonObject.getString(key));
			}

		} catch (JSONException e) {
			Log.log("Exception in WebViewAutomator.decodeJsonElement(): json was: " + json);
			Log.log("     exception message: " + e.getMessage());
			// throw new IllegalArgumentException("Unable to find " + componentType + " \"" +
			// monkeyId
			// + "\"");
		}

		return element;
	}

	public HtmlElement findNthElement(String jsElemsExpr, String componentType, int n) {
		// String encodedResult = this.runJavaScript("return window.monkeytalk.encodeElements("
		// + jsElemsExpr + ");");

		// wait until the webview has completely loaded to playback
		if (getWebView().getProgress() < 100) {
			throw new IllegalArgumentException("Unable to find " + componentType);
		}

		String json = runJavaScript("return MonkeyTalk.getElement(" + jsElemsExpr + ",'#" + n
				+ "','" + componentType + "');");
		HtmlElement element = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			element = new HtmlElement(getWebView());
			for (String field : fields) {
				element.putAttr(field, jsonObject.getString(field));
			}

		} catch (JSONException e) {
			throw new IllegalArgumentException("Unable to find " + componentType);
		}
		return element;
	}

	private static class Result {
		private String result = null;
		private boolean error = false;

		Result(String result) {
			this.result = result;
		}

		Result(String msg, boolean error) {
			result = msg;
			this.error = error;
		}

	}

	/**
	 * Notify waiting caller that called JS is done
	 * 
	 * @param result
	 */
	public static void reportResult(String result) {
		Log.log("putting result " + result);
		try {
			jsResult.put(new Result(result));
		} catch (InterruptedException e) {
			Log.log(e);
		}

	}

	/**
	 * Notify waiting caller that called JS errored out
	 * 
	 * @param result
	 */
	public static void reportError(String result) {
		Log.log("Putting error " + result);
		try {
			jsResult.put(new Result(result, true));
		} catch (InterruptedException e) {
			Log.log(e);
		}

	}

	public HtmlElement findHtmlElement(String componentType, String monkeyId, int index) {
		if (isJsPopupOpen()) {
			return null;
		}

		return WebAutomationManager.findHtmlElement(getWebView(), componentType, monkeyId, index);
	}

	/**
	 * Include the JavaScript source in the webview
	 * 
	 * @param fileName
	 *            name of resource file containing the source
	 */
	public void includeJs(String fileName) {
		final String s = fileToString(fileName);
		runJavaScript(s);
	}

	public static String fileToString(String fileName) {
		InputStream is = AutomationManager.getTopActivity().getClass().getClassLoader()
				.getResourceAsStream(fileName);

		if (is == null) {
			String msg = "WebViewAutomator: Unable to read file " + fileName;
			Log.log(msg);
			throw new IllegalArgumentException(msg);
		}
		Writer writer = new StringWriter();

		char[] buffer = new char[1024];
		try {
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n = 0;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
		} catch (Exception e) {
			Log.log("Unexpected exception reading " + fileName + ": " + e.getMessage());
		}
		return writer.toString();

	}

	public void setJsPopupOpen(boolean b) {
		jsPopupOpen = b;

	}

	public boolean isJsPopupOpen() {
		return jsPopupOpen;
	}

	public static String[] getDefaultFields() {
		return fields;
	}

	private static String[] fields = { "monkeyId", "tagName", "id", "name", "className", "value",
			"textContent", "type", "x", "y", "width", "height", "title" };

	private static String[] fields1 = { "tagName", "id", "name", "className", "value",
			"textContent", "type", "x", "y", "width", "height", "title" };

	/**
	 * Convert the delimited string representation of a list of HtmlElements to an actual list
	 * 
	 * @param s
	 *            the delimited string encoded
	 * @return the list of HtmlElements decoded from the string
	 */
	public List<HtmlElement> decodeJsResult(String s) {
		return decodeJsResult(s, fields1);
	}

	public List<HtmlElement> decodeJsResult(String s, String[] fields) {
		// getWebView().loadUrl("MonkeyTalkGetAllElements();");
		List<HtmlElement> list = new ArrayList<HtmlElement>();
		if (s == null) {
			return list;
		}

		String[] elems = s.split("<-mte->");
		for (String elem : elems) {
			String[] attrs = elem.split("<-mtf->", fields.length);
			HtmlElement h = new HtmlElement(getWebView());
			int i = 0;
			for (String field : fields) {
				if (attrs.length > i) {
					h.putAttr(field, attrs[i++]);
				}
			}

			Log.log("tag: " + h.getTagName() + "monkeyId: " + h.getMonkeyId() + "component: "
					+ h.getClassName());
			list.add(h);
		}
		return list;
	}

	// public List<HtmlElement> decodeJsResult(String s, String[] fields) {
	// getWebView().loadUrl("MonkeyTalkGetAllElements();");
	// // runJavaScript("MonkeyTalkGetAllElements();");
	// List<HtmlElement> list = new ArrayList<HtmlElement>();
	// if (s == null) {
	// return list;
	// }
	//
	// String[] elems = s.split("<-mte->");
	// for (String elem : elems) {
	// String[] attrs = elem.split("<-mtf->", fields.length);
	// HtmlElement h = new HtmlElement(this.getWebView());
	// int i = 0;
	// for (String field : fields) {
	// if (attrs.length > i) {
	// h.putAttr(field, attrs[i++]);
	// }
	// }
	//
	// Log.log("tag: " + h.getTagName() + "monkeyId: " + h.getMonkeyId() + "component: "
	// + h.getClassName());
	// list.add(h);
	// }
	// return list;
	// }

	@Override
	public String play(String action, String... args) {
		if (action.equalsIgnoreCase("dump")) {
			return (runJavaScript("return document.body.innerHTML"));
		} else if (action.equalsIgnoreCase("execjs") || action.equalsIgnoreCase("exec")) {
			assertArgCount("execJS", args, 1);
			execJavaScript(args[0], false); // don't tag console writes
			return null;
		} else if (action.equalsIgnoreCase("execandreturn")) {
			assertArgCount("execandreturn", args, 2);
			String arg = "return " + args[1];
			return runJavaScript(arg);
		} else if (action.equalsIgnoreCase(AutomatorConstants.ACTION_SCROLL)) {
			if (args.length < 2) {
				throw new IllegalArgumentException(AutomatorConstants.ACTION_SCROLL
						+ " requires an X and Y value");
			}
			scroll(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			return null;
		} else if (action.equalsIgnoreCase(AutomatorConstants.ACTION_CLEAR_COOKIES)) {
			CookieManager.getInstance().removeAllCookie();
			return "removed all the cookies";
		} else {
			return super.play(action, args);
		}
	}

	protected void enterText(String s, HtmlElement element) {
		super.enterText(s);
		int x = element.getX();
		int y = element.getY();
		runJavaScript("MonkeyTalk.enterTextAtPoint(" + x + "," + y + ",'" + s + "');");
	}

	@Override
	public boolean canAutomate(String componentType, String monkeyID) {
		return componentType.equals(getComponentType()) ? super
				.canAutomate(componentType, monkeyID) : false;

	}

	public String getComponentTreeJson() {
		String json = runJavaScript("return MonkeyTalk.getComponentTreeJson();");
		return json;
	}

}
