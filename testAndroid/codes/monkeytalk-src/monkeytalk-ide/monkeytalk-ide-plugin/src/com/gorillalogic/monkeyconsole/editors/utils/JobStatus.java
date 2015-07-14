package com.gorillalogic.monkeyconsole.editors.utils;

import org.json.JSONException;
import org.json.JSONObject;

public enum JobStatus
{
	valid, queued, running, reporting, done;
	
	public static JobStatus valueOf(JSONObject response) throws JSONException
	{
		if (response == null) {
			return null;
		}
		
		JSONObject data = response.getJSONObject("data");
		String rawStatus = data.getString("status");
		JobStatus status = valueOf(rawStatus);
		return status;
	}
}
