package com.gorillalogic.monkeytalk.processor.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;
import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.sender.CommandSender;
import com.gorillalogic.monkeytalk.sender.Response;
import com.gorillalogic.monkeytalk.utils.FileUtils;
import com.gorillalogic.monkeytalk.verify.Verify;

public class Debug extends BaseCommand {
	public static final String TEXT = "text";
	public static final String JSON = "json";

	private String projectDirectory;
	private CommandSender sender;

	private String filePath = null;
	private String debugFileName = null;

	public Debug(Command cmd, Scope scope, PlaybackListener listener, String projectDirectory) {
		this(cmd, scope, listener, projectDirectory, null);
	}

	public Debug(Command cmd, Scope scope, PlaybackListener listener, String projectDirectory,
			CommandSender sender) {
		super(cmd, scope, listener);
		this.projectDirectory = projectDirectory;
		this.sender = sender;
	}

	public PlaybackResult globals() {
		listener.onStart(scope);

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : com.gorillalogic.monkeytalk.processor.Globals
				.getGlobals().entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append("\n");
		}

		listener.onComplete(scope, new Response());
		String message = sb.toString();
		listener.onPrint(message);

		PlaybackResult result = new PlaybackResult(PlaybackStatus.OK, null, scope);
		result.setDebug(message);
		return result;
	}

	public PlaybackResult vars() {
		listener.onStart(scope);

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : scope.getVariables().entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append("\n");
		}

		listener.onComplete(scope, new Response());
		String message = sb.toString();
		listener.onPrint(message);

		PlaybackResult result = new PlaybackResult(PlaybackStatus.OK, null, scope);
		result.setDebug(message);
		return result;
	}

	private void setDebugFileProps(String value) {
		String[] path = cmd.getMonkeyId().split("/");

		if (!value.contains("/")) {
			// In project root
			filePath = projectDirectory;
		} else if (!cmd.getMonkeyId().substring(0, 1).equals("/")) {
			// Relative path
			filePath = projectDirectory;
			for (int i = 0; i < path.length - 1; i++) {
				filePath = filePath + "/" + path[i];
			}
		} else {
			// Absolute path
			for (int i = 0; i < path.length - 1; i++) {
				filePath = filePath + "/" + path[i];
			}
		}

		debugFileName = path[path.length - 1];
		// If no file extension provided, use ".txt".
		if (!debugFileName.contains(".")) {
			debugFileName += ".txt";
		}
	}

	public PlaybackResult erase() {
		listener.onStart(scope);

		listener.onComplete(scope, new Response());
		PlaybackResult result = null;
		boolean err = false;
		String message = null;

		setDebugFileProps(cmd.getMonkeyId());

		File debugFile = new File(filePath, debugFileName);

		if (debugFile.exists()) {
			if (!debugFile.delete()) {
				err = true;
				message = "Cannot delete file at path: " + debugFile;
			}
		}

		Response resp = (err ? new Response.Builder().error().message(message).build()
				: new Response());
		result = new PlaybackResult(resp, scope);
		result.setDebug(message);
		return result;
	}

	public PlaybackResult print() {
		listener.onStart(scope);

		StringBuilder sb = new StringBuilder();
		for (String arg : cmd.getArgs()) {
			sb.append(arg).append(' ');
		}

		listener.onComplete(scope, new Response());
		String message = (sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "");
		PlaybackResult result = null;
		boolean err = false;

		if (cmd.getMonkeyId().equals("*")) {
			listener.onPrint(message + "\n");
			result = new PlaybackResult(PlaybackStatus.OK, null, scope);
		} else {
			setDebugFileProps(cmd.getMonkeyId());

			File fileDir = new File(filePath);
			if (!fileDir.exists()) {
				if (!fileDir.mkdirs()) {
					err = true;
					message = "Cannot create directory at path: " + filePath;
				}
			}

			if (!err) {
				File debugFile = new File(filePath, debugFileName);
				if (debugFile.exists())
					try {
						FileUtils.appendStringToFile(debugFile, message);
					} catch (IOException e) {
						err = true;
						message = e.getMessage();
						e.printStackTrace();
					}
				else
					try {
						FileUtils.writeFile(debugFile, message);
					} catch (IOException e) {
						err = true;
						message = e.getMessage();
						e.printStackTrace();
					}
			}
			Response resp = (err ? new Response.Builder().error().message(message).build()
					: new Response());
			result = new PlaybackResult(resp, scope);
		}
		result.setDebug(message);
		return result;
	}

	public PlaybackResult tree(String filter, String format) {
		return tree(filter, format, null);
	}

	public PlaybackResult tree(String filter, String format, Integer timeout) {
		format = format.toLowerCase();
		listener.onStart(scope);

		StringBuilder sb = new StringBuilder();
		boolean err = false;
		if (format.equals(TEXT) || format.equals(JSON)) {
			Response resp = null;
			try {
				resp = sender.dumpTree(timeout);
				JSONObject json = resp.getBodyAsJSON();
				if (json != null && json.has("message")) {
					JSONObject msg = json.getJSONObject("message");

					if (format.equals(JSON)) {
						if (filter == null || filter.equals("*")) {
							sb = new StringBuilder(_treeJson(msg).toString()).append("\n");
						} else {
							JSONArray matches = new JSONArray();
							_treeJson(msg, matches, filter);
							sb = new StringBuilder(matches.toString()).append("\n");
						}
					} else {
						_tree(msg, filter, sb, "");
					}
				} else {
					err = true;
					sb = new StringBuilder("bad tree");
				}
			} catch (JSONException ex) {
				err = true;
				sb = new StringBuilder("error parsing tree: ");
				sb.append(ex.getMessage());
				System.err.println("#################################");
				System.err.println(sb.toString());
				ex.printStackTrace();
				System.err.println("RESP IS:\n" + resp);
				System.err.println("#################################");
			}
		} else {
			err = true;
			sb = new StringBuilder("unknown tree format, allowed values are: [text, json]");
		}

		String message = sb.toString();
		Response resp = (err ? new Response.Builder().error().message(message).build()
				: new Response());

		listener.onComplete(scope, resp);
		if (!err) {
			listener.onPrint(message);
		}

		PlaybackResult result = new PlaybackResult(resp, scope);
		result.setDebug(message);
		return result;
	}

	private void _tree(JSONObject node, String filter, StringBuilder sb, String indent)
			throws JSONException {
		if (node.optBoolean("visible", false)) {
			String componentType = node.optString("ComponentType", "View");

			if (filter == null || filter.equals("*")) {
				sb.append(indent).append(componentType).append("(")
						.append(node.optString("monkeyId", "*")).append(")\n");
			} else if (Verify.verifyWildcard(filter.toLowerCase(), componentType.toLowerCase())) {
				sb.append(componentType).append("(").append(node.optString("monkeyId", "*"))
						.append(")\n");
			}

			if (node.has("children")) {
				JSONArray children = node.getJSONArray("children");
				for (int i = 0; i < children.length(); i++) {
					JSONObject child = children.getJSONObject(i);
					_tree(child, filter, sb, indent + "  ");
				}
			}
		}
	}

	private JSONObject _treeJson(JSONObject node) throws JSONException {
		JSONObject out = null;
		if (node.optBoolean("visible", false)) {
			List<String> names = new ArrayList<String>(Arrays.asList(JSONObject.getNames(node)));
			names.remove("children");
			out = new JSONObject(node, names.toArray(new String[] {}));

			if (node.has("children")) {
				JSONArray children = new JSONArray();
				for (int i = 0; i < node.getJSONArray("children").length(); i++) {
					JSONObject child = _treeJson(node.getJSONArray("children").getJSONObject(i));
					if (child != null) {
						children.put(child);
					}
				}
				if (children.length() > 0) {
					out.put("children", children);
				}
			}
		}
		return out;
	}

	private void _treeJson(JSONObject node, JSONArray matches, String filter) throws JSONException {
		if (node.optBoolean("visible", false)) {
			String componentType = node.optString("ComponentType", "View");

			if (Verify.verifyWildcard(filter.toLowerCase(), componentType.toLowerCase())) {
				List<String> names = new ArrayList<String>(Arrays.asList(JSONObject.getNames(node)));
				names.remove("children");
				JSONObject obj = new JSONObject(node, names.toArray(new String[] {}));

				matches.put(obj);
			}

			if (node.has("children")) {
				for (int i = 0; i < node.getJSONArray("children").length(); i++) {
					_treeJson(node.getJSONArray("children").getJSONObject(i), matches, filter);
				}
			}
		}
	}
}
