package com.findcab.handler;

import org.json.JSONException;
import org.json.JSONObject;

import com.findcab.object.TripsInfo;

public class TripsHandler extends AbsHandler {
	/**
	 * 返回路线对象
	 */
	@Override
	public TripsInfo parseResponse(String responseStr) {
		// TODO Auto-generated method stub
		System.out.println("Trips------------>" + responseStr);
		JSONObject object = null;
		try {
			object = new JSONObject(responseStr);
			return new TripsInfo(object.getJSONObject("trip"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
