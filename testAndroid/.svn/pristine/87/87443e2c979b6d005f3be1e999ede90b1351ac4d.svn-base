package com.gorillalogic.fonemonkey.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.gorillalogic.fonemonkey.Log;
import com.gorillalogic.fonemonkey.Recorder;
import com.gorillalogic.fonemonkey.automators.WebViewAutomator;
import com.gorillalogic.monkeytalk.Command;

public class WebViewRecorder {
	public int elementCount;
	private WebView webView;
	private boolean jsAttached;

	public WebViewRecorder(WebView webView) {
		super();
		this.webView = webView;
		this.webView.addJavascriptInterface(this, "mtrecorder");

		// webView.setOnTouchListener(new View.OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// // Log.d("webview", "element: " + " x:" + event.getX() + " y:" + event.getY());
		// // Log.d("webview", "raw: " + " x:" + event.getRawX() + " y:" +
		// // event.getRawY());
		//
		// WebView webView = (WebView) v;
		// float zoom = webView.getScale();
		// int x = (int) (event.getX() / zoom) - webView.getScrollX();
		// int y = (int) (event.getY() / zoom) - webView.getScrollY();
		// // Log.d("webview", "js: " + " x:" + x + " y:" + y);
		// webView.loadUrl("javascript:( function () { MonkeyTalk.recordTap(" + x + ", "
		// + y + "); } ) ()");
		// }
		//
		// return false;
		// }
		// });
	}

	public int getElementCount() {
		return elementCount;
	}

	public void setElementCount(int count) {
		this.elementCount = count;
	}

	public boolean isJsAttached() {
		return jsAttached;
	}

	public void setJsAttached(boolean jsAttached) {
		this.jsAttached = jsAttached;
	}

	public static void attachJs(WebView webView) {
		try {
			// hack to make sure we make it into webview
			Thread.sleep(500);
			String libJs = WebViewAutomator.monkeyTalkJs();
			
			Method methodToFind = null;
			try {
				methodToFind = webView.getClass().getMethod("evaluateJavascript", new Class[] {String.class, ValueCallback.class});
			} catch (NoSuchMethodException nsme) {
				methodToFind = null;
			}
			//Invoke it if present (API Level >=19 only)
			if(methodToFind == null) {
			   	// "evaluateJavascript" method not found.
				//
				// API Level <= 18 detected
				//
				webView.loadUrl( "javascript:" + libJs);
			} else {
				//
				// API Level 19 detected
				//
			   	// Method found. You can invoke the method like
				methodToFind.invoke(webView, libJs, null);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void webViewDidChange() {
		// if (!isJsAttached()) {
		// Log.d("recorder", "---- attach js");
		// attachJs();
		// setJsAttached(true);
		// }
	}

	public void elementCountCallback(String result) {
		// if (!isJsAttached()) {
		// Log.d("recorder", "count: " + result);
		// attachJs();
		// setJsAttached(true);
		// }
	}

	public void recordJson(String json) {
		Log.log(json);

		try {
			JSONObject jsonObject = new JSONObject(json);
			String componentType = jsonObject.getString("component");
			String monkeyID = jsonObject.getString("monkeyId");
			String action = jsonObject.getString("action");
			// String args = jsonObject.getString("args");

			ArrayList<String> args = new ArrayList<String>();

			try {
				JSONArray jsonArgs = jsonObject.getJSONArray("args");
				if (jsonArgs != null) {
					for (int i = 0; i < jsonArgs.length(); i++) {
						args.add(jsonArgs.get(i).toString());
					}
				}
			} catch (JSONException e) {
				String argsString = jsonObject.getString("args");
				if (argsString.length() > 0) {
					args.add(argsString);
				}
			}

			Map<String, String> modifiers = new HashMap<String, String>();
			Command cmd = new Command(componentType, monkeyID, action, args, modifiers);

			Recorder.recordCommand(cmd);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// AutomationManager.record(action, view, null)
		// AutomationManager.record(AutomatorConstants.ACTION_SELECT, group, label);
	}
}
