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

package com.gorillalogic.fonemonkey.web;

import java.lang.reflect.Method;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.EditText;

import com.gorillalogic.fonemonkey.FunctionalityAdder;
import com.gorillalogic.fonemonkey.automators.WebViewAutomator;

/* 
 * Hack alert:
 * WebView.addJavaScriptInterface doesn't work if called after a page has already been loaded.
 * AndroidWebDriver relies on addJavaScriptInterface to bridge between JS and Java.
 * Because we might not find out about a WebView until after its initial page has been loaded,
 * we use this mechanism instead whereby we intercept messages being written to the Web Console
 * via Android's built-in JavaScript console() function.
 */
public class WebChromeClientWrapper extends WebChromeClient {
	private WebChromeClient client;
	private WebViewAutomator auto;

	// private WebViewRecorder recorder;

	public WebChromeClientWrapper(WebViewAutomator auto) {
		WebView view = auto.getWebView();
		this.auto = auto;
		// recorder = new WebViewRecorder(view);
		Method meth;
		try {
			meth = WebView.class.getDeclaredMethod("getWebChromeClient", (Class<?>[]) null);
			client = (WebChromeClient) meth.invoke(view, (Object[]) null);
			if (client == null) {
				client = new WebChromeClient();
			}
		} catch (Exception e) {
			// webviewclient moved inside WebProvider in 4.1 JellyBean
			try {
				meth = WebView.class.getDeclaredMethod("getWebViewProvider", (Class<?>[]) null);
				Object provider = meth.invoke(view, (Object[]) null);
				if (provider != null) {
					try {
						meth = provider.getClass().getMethod("getWebChromeClient",
								(Class<?>[]) null);
						client = (WebChromeClient) meth.invoke(provider, (Object[]) null);
					} catch (NoSuchMethodException ex) {
						// in 4.4 KitKat getWebChromeClient no longer exists
						client = null;
					}
				}
				if (client == null) {
					client = new WebChromeClient();
				}

			} catch (Exception e1) {
				throw new IllegalStateException(
						"Error getting WebChromeClient: " + e1.getMessage(), e1);
			}
		}
		view.setWebChromeClient(this);
	}

	/**
	 * This is how we return data from JS to Java!
	 * 
	 * For messages beginning with "monkeytalk:", LOG level messages provide normal return values.
	 * ERROR level messages signify that an error ocurred and contain error message. All other
	 * sources are passed through for actual writing to the console.
	 */
	@Override
	public boolean onConsoleMessage(ConsoleMessage msg) {

		if (msg.message().startsWith("mtrecorder:")) {
			String parts[] = msg.message().split("mtrecorder:");
			String json = parts[1];
			auto.getRecorder().recordJson(json);
			return false;
		}

		if (msg.message().startsWith("monkeytalk:")) {
			String parts[] = msg.message().split("monkeytalk:");
			if (parts.length < 2) {
				WebViewAutomator.reportResult("");
				return true;
			}
			String message = parts[1];
			if (msg.messageLevel() == ConsoleMessage.MessageLevel.LOG) {
				WebViewAutomator.reportResult(message);
				return false;
			}

			if (msg.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
				WebViewAutomator.reportError(message);
				return true;
			}
		}
		if (client != null) {
			return client.onConsoleMessage(msg);
		}
		return false;

	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

		if (client.onJsConfirm(view, url, message, result)) {
			return true;
		}

		AlertDialog dialog = new AlertDialog.Builder(auto.getWebView().getContext()).setTitle(url)
				.setMessage(message).setPositiveButton("OK", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
						auto.setJsPopupOpen(false);
					}
				}).setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
						auto.setJsPopupOpen(false);
					}
				}).setCancelable(false).create();
		dialog.show();
		FunctionalityAdder.walkTree(dialog.getWindow().peekDecorView());
		auto.setJsPopupOpen(true);
		return true;
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
			final JsPromptResult result) {

		if (client.onJsPrompt(view, url, message, defaultValue, result)) {
			return true;
		}
		Context context = auto.getWebView().getContext();
		EditText input = new EditText(context);
		input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		AlertDialog dialog = new AlertDialog.Builder(auto.getWebView().getContext()).setTitle(url)
				.setView(input).setMessage(message)
				.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
						auto.setJsPopupOpen(false);
					}
				}).setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
						auto.setJsPopupOpen(false);
					}
				}).setCancelable(false).create();
		dialog.show();
		FunctionalityAdder.walkTree(dialog.getWindow().peekDecorView());
		auto.setJsPopupOpen(true);
		return true;
	}

	@Override
	public boolean onJsTimeout() {
		return true;
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String msg, final JsResult result) {
		// ignore undefined alert when running Web exec
		if (msg.equalsIgnoreCase("undefined")) {
			result.confirm();
			return true;
		}

		if (msg.startsWith("monkeytalk:")) {
			String parts[] = msg.split("monkeytalk:");
			if (parts.length < 2) {
				WebViewAutomator.reportResult("");
				return true;
			}
			String message = parts[1];

			WebViewAutomator.reportResult(message);
			auto.setJsPopupOpen(false);
			result.confirm();
			return true;
		}

		if (msg.startsWith("mtrecorder:")) {
			String parts[] = msg.split("mtrecorder:");
			String json = parts[1];
			auto.getRecorder().recordJson(json);
			auto.setJsPopupOpen(false);
			result.confirm();
			return true;
		}

		auto.setJsPopupOpen(true);

		// Create our own dialog for the message to make sure we link in
		AlertDialog dialog = new AlertDialog.Builder(auto.getWebView().getContext())
				.setTitle("Alert").setMessage(msg)
				.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
						auto.setJsPopupOpen(false);
					}
				}).setCancelable(false).create();
		dialog.show();
		FunctionalityAdder.walkTree(dialog.getWindow().peekDecorView());
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return client.equals(obj);
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		client.onProgressChanged(view, newProgress);

		// view.requestFocus(View.FOCUSABLES_TOUCH_MODE);
		// if (newProgress > 50)
		WebViewRecorder.attachJs(view);
		// else
		// recorder.setJsAttached(false);
	}

	@Override
	public void onHideCustomView() {
		client.onHideCustomView();
	}

	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
		return client.onJsBeforeUnload(view, url, message, result);
	}

	@Override
	public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
		client.onGeolocationPermissionsShowPrompt(origin, callback);
	}

	@Override
	public Bitmap getDefaultVideoPoster() {
		return client.getDefaultVideoPoster();
	}

	@Override
	public View getVideoLoadingProgressView() {
		return client.getVideoLoadingProgressView();
	}

	@Override
	public void getVisitedHistory(ValueCallback<String[]> callback) {
		client.getVisitedHistory(callback);
	}

	@Override
	public int hashCode() {
		return client.hashCode();
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture,
			Message resultMsg) {
		return client.onCreateWindow(view, dialog, userGesture, resultMsg);
	}

	@Override
	public void onCloseWindow(WebView window) {
		client.onCloseWindow(window);
	}

	@Override
	public void onConsoleMessage(String message, int lineNumber, String sourceID) {
		client.onConsoleMessage(message, lineNumber, sourceID);
	}

	@Override
	public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
			long estimatedSize, long totalUsedQuota, QuotaUpdater quotaUpdater) {
		client.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota, estimatedSize,
				totalUsedQuota, quotaUpdater);
	}

	@Override
	public void onGeolocationPermissionsHidePrompt() {
		client.onGeolocationPermissionsHidePrompt();
	}

	@Override
	public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota,
			QuotaUpdater quotaUpdater) {
		client.onReachedMaxAppCacheSize(spaceNeeded, totalUsedQuota, quotaUpdater);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		client.onReceivedTitle(view, title);
	}

	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		client.onReceivedIcon(view, icon);
	}

	@Override
	public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
		client.onReceivedTouchIconUrl(view, url, precomposed);
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		client.onShowCustomView(view, callback);
	}

	@Override
	public void onRequestFocus(WebView view) {
		client.onRequestFocus(view);
	}

	@Override
	public String toString() {
		return client.toString();
	}
}
