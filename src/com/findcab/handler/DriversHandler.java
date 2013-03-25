package com.findcab.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.findcab.object.DriversInfo;

/**
 * 在线司机
 * 
 * @author yuqunfeng
 * 
 */
public class DriversHandler extends AbsHandler {

	@Override
	public Object parseResponse(String responseStr) {
		// TODO Auto-generated method stub
		List<DriversInfo> listInfo = null;
		if (responseStr != null) {

			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(responseStr);
				JSONArray array = jsonObject.getJSONArray("drivers");
				listInfo = new ArrayList<DriversInfo>();
				DriversInfo driversInfo;
				for (int i = 0; i < array.length(); i++) {
					
					driversInfo = new DriversInfo((JSONObject) array.get(i));

					if (driversInfo.getOnline() == 1) {

						System.out.println("driversInfo.getOnline() == 1----->"
								+ driversInfo.getName());
						listInfo.add(driversInfo);
					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return listInfo;

	}
}
