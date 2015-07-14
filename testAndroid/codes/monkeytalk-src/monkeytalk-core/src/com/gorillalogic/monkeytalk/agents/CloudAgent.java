package com.gorillalogic.monkeytalk.agents;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.gorillalogic.monkeytalk.sender.CommandSender;
import com.gorillalogic.monkeytalk.sender.Response;

public class CloudAgent extends MTAgent {
	private String deviceToken = null;

	public void setDeviceToken(String deviceNumber) {
		this.deviceToken = deviceNumber;
	}

	@Override
	public String getName() {
		return "Cloud";
	}

	@Override
	public String validate() {
		// TODO some cloud validation
		return super.validate();
	}

	@Override
	protected CommandSender createCommandSender(String host, int port) {
		return new CloudCommandSender(host, port, deviceToken);
	}

	private class CloudCommandSender extends CommandSender {
		URL url;
		String deviceToken;

		public CloudCommandSender(String host, int port, String deviceNumber) {
			super(host, port);
			this.deviceToken = deviceNumber;
			try {
				url = new URL("http", host, port, "/remote/passthrough/post");
			} catch (Exception e) {
				url = null;
			}
		}

		@Override
		protected Response sendCommand(String mtcommand, JSONObject json) {
			try {
				json.put("mtversion", 1);
				json.put("mtcommand", mtcommand);
				json.put("timestamp", System.currentTimeMillis());
			} catch (JSONException ex) {
				return new Response(0, "failed to build outbound JSON message");
			}
			JSONObject cloudJSON;
			try {
				cloudJSON = new JSONObject();
				cloudJSON.put("token", deviceToken);
				cloudJSON.put("endpoint", "fonemonkey");
				cloudJSON.put("postdata", json);
			} catch (Exception e) {
				return new Response(0, "failed to build outbound JSON message");
			}

			return sendJSON(url, cloudJSON);
		}
	}
}
