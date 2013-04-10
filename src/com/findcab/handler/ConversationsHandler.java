package com.findcab.handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.findcab.object.ConversationInfo;

/**
 * 得到最最近一次会话
 * 
 * @author yuqunfeng
 * 
 */
public class ConversationsHandler extends AbsHandler {

	@Override
	public Object parseResponse(String responseStr) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("lastConversation---------------------------------->"+responseStr);
		ConversationInfo lastConversation = null;
		try {
			JSONObject object = new JSONObject(responseStr);
			JSONArray array = object.getJSONArray("conversations");

			// 得到最最近一次会话
			lastConversation = new ConversationInfo(array.getJSONObject(array
					.length() - 1));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 司机应答

		return lastConversation;

	}
}
