package com.findcab.handler;

import org.json.JSONObject;

import com.findcab.object.TripsInfo;

public class TripsHandler extends AbsHandler {
	/**
	 * 返回路线对象
	 */
	@Override
	public Object parseResponse(String responseStr) {
		// TODO Auto-generated method stub
		System.out.println("Trips------------>" + responseStr);
		JSONObject object = null;

		TripsInfo info = null;
		try {
			object = new JSONObject(responseStr);

			info = new TripsInfo(object.getJSONObject("trip"));
			return info;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
