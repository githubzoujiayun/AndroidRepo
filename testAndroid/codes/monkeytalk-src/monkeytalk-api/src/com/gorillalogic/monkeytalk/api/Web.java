package com.gorillalogic.monkeytalk.api;

/**
 * The JavaScript backend for a webview.
 * 
 * @types script
 */
public interface Web extends MTObject {
	/**
	 * Execute JavaScript method inside an embedded web view. This feature enables you to interact
	 * with an embedded web view or a hybrid app with the full power of Javascript and the DOM.
	 * 
	 * @param method
	 *            the method to call
	 * @param args
	 *            the args to be supplied to the method
	 * @see <a
	 *      href="http://www.cloudmonkeymobile.com/monkeytalk-documentation/monkeytalk-user-guide/execute-javascript-code">
	 *      MonkeyTalk Documentation</a>
	 */
	public void exec(String method, String... args);

	/**
	 * Execute JavaScript method inside an embedded web view and fetch the return value. This
	 * feature enables you to interact with an embedded web view or a hybrid app with the full power
	 * of Javascript and the DOM. The returned value is set into the given variable name.
	 * 
	 * @param variable
	 *            the name of the variable to set
	 * @param method
	 *            the method to call
	 * @param args
	 *            the args to be supplied to the method
	 * @return the return value
	 * 
	 * @see <a
	 *      href="http://www.cloudmonkeymobile.com/monkeytalk-documentation/monkeytalk-user-guide/execute-javascript-code">
	 *      MonkeyTalk Documentation</a>
	 */
	public String execAndReturn(String variable, String method, String... args);
}
