package com.findcab.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.findcab.object.ConversationInfo;

public class ConversationHandler extends AbsHandler {

	@Override
	public Object parseResponse(String responseStr) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("conversation---------------------------------->"+responseStr);
		ConversationInfo conversatinon = null;
		
		try {
			JSONObject object = new JSONObject(responseStr);
			conversatinon = new ConversationInfo(object.getJSONObject("conversation"));
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//return lastConversation;
		return conversatinon;

	}
}
