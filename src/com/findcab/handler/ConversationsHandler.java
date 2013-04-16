package com.findcab.handler;

import java.util.ArrayList;
import java.util.List;

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
		ConversationInfo conversatinon = null;
		List<ConversationInfo> listLastConversation=new ArrayList<ConversationInfo>();
		try {
			JSONObject object = new JSONObject(responseStr);
			JSONArray array = object.getJSONArray("conversations");
			System.out.println("lastConversation_array_length------------>"+array
					.length() );
			
			
			// 得到最最近一次会话
			lastConversation = new ConversationInfo(array.getJSONObject(array
					.length() - 1));
			System.out.println("里面lastConversation---------------------------->"+lastConversation.getId());
			System.out.println("里面lastConversation--------------------------->"+lastConversation.getStatus());
			
//			int j=array.length() - 1;
//			
//			for(int i= array.length() - 1-10;i<j;i++){
//				conversatinon =  new ConversationInfo(array.getJSONObject(i));
//				System.out.println("里面--------------------->"+conversatinon.getId());
//				System.out.println("里面--------------------->"+conversatinon.getStatus());
//				if(conversatinon!=null){
//					listLastConversation.add(new ConversationInfo(array.getJSONObject(i)));
//				}
//				else{
//					break;
//				}
//			}
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 司机应答

		return lastConversation;
		//return listLastConversation;

	}
}
