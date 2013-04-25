package com.findcab.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.util.Log;

import com.findcab.handler.Ihandler;
import com.findcab.mywidget.MyToast;

/**
 * 网络交互
 * 
 * @author zhangkun
 * @date
 */
public class HttpTools {
	public static int responseStatus;

	

	/**
	 * 用来返回url的地址 simsunny
	 * @param url
	 * @param data
	 * @return String 
	 */
	private static String buildUri(String url, Map<String,String> data){
		String result=null;
		result=url+"?";
		for(Object obj: data.keySet()){
			String key = (String)obj;
			result=result+key+"=";
			String value =(String)data.get(key);
			result=result+value+"&";
		}
		
		result=result.substring(0,result.length()-1);
		
		return  result;
	}
	
	private static Uri.Builder buildGetMethod(String url,
			Map<String, String> data) {
		final Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(url);

		if (data != null) {
			for (Map.Entry<String, String> m : data.entrySet()) {
				builder.appendQueryParameter(m.getKey(), m.getValue());
			}
		}
		return builder;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 先检查网络连接是否连接
	 * 
	 * @return
	 */
	public static boolean checkNetWork(final Context context) {
//		if (!isNetworkAvailable(context)) {
		if(!getConnectNetState(context)){
			MyToast toast = new MyToast(context,"网络连接错误，请检查网络连接！");
			toast.startMyToast();
//			new AlertDialog.Builder(context).setIcon(
//					android.R.drawable.ic_dialog_alert).setTitle("网络连接错误")
//					.setMessage("请检查网络连接！").setPositiveButton("确定",
//							new OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated method stub
//									Intent netIntent = new Intent(
//											"android.settings.WIRELESS_SETTINGS");
//									context.startActivity(netIntent);
//								}
//							}).show();
			return false;
		}
		return true;
	}

	/**
	 * 先检查网络连接是否连接
	 * 
	 * @return
	 */
	public static boolean checkNetWorkAndFinish(final Context context) {
		if (!isNetworkAvailable(context)) {

			// MyUtils.myAlertDialogForNetError(context, "网络链接失败").show();

			return false;
		}
		return true;
	}

	/**
	 * 获得当前手机联网状态
	 * @param connManager
	 * @param context
	 * @return
	 */
	public static boolean getConnectNetState(Context context ){//ConnectivityManager connManager,
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			try{
				NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();//获取网络的连接情况  
				if (activeNetInfo == null || !activeNetInfo.isAvailable()){     
					return false;   
				} else if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){  
				//判断WIFI网  
					State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
					 if(State.CONNECTED != state){  
//						 Toast.makeText(context, "WIFI网络没有连接", Toast.LENGTH_SHORT).show();
						 return false;
					 } 
				}else if(activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
				//判断3G网  
					 State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
					 if(State.CONNECTED != state){  
//						 Toast.makeText(context, "3G网络没有连接", Toast.LENGTH_SHORT).show();
						 return false;
					 }  
				}  
			}catch(Exception e){
				e.getStackTrace();
			}
			return true;
		}
	
	/*
	 * GEt请求
	 */
	public static String GetDate(String url, Map<String, String> data) {

		if (data != null) {
			url = buildGetMethod(url, data).build().toString();
			System.out.println("------------------------------------->"+url);
		}

		System.out.println("url----->" + url);
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(get);// 执行Post方法
			String resultString = EntityUtils.toString(response.getEntity());
			return resultString;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/*
	 * POST请求
	 */
	public static String PostDate(String url, Map<String, String> data) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (data != null) {
			NameValuePair pair;
			for (Map.Entry<String, String> m : data.entrySet()) {
				pair = new BasicNameValuePair(m.getKey(), m.getValue());
				list.add(pair);
			}
		}
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(list, HTTP.UTF_8);//
			// 使用编码构建Post实体
			HttpPost post = new HttpPost(url);
//			System.out.println("url--------------------------->" + url.toString());
			Log.e("url--------------------------->", url.toString());
			post.setEntity(httpEntity);// 设置Post实体
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);// 执行Post方法
			String resultString = EntityUtils.toString(response.getEntity());
			System.out.println("resultString------->" + resultString);
			return resultString;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 执行post方法
	 * 
	 * @param context
	 * @param url
	 * @param data
	 * @param ihandler
	 * @return
	 */
	public static Object postAndParse(String url, Map<String, String> data,
			Ihandler ihandler) {
		responseStatus = 0; // 1代表正常 2代表其它错误 2代表服务端相应错误，3代表解析错误
		ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		if (data != null) {
			for (Map.Entry<String, String> m : data.entrySet()) {
				postData.add(new BasicNameValuePair(m.getKey(), m.getValue()));
			}
		}
		System.out.println("url---------------------->" + url);
		HttpPost httpPost = new HttpPost(url);
		BasicHttpParams httpParams = new BasicHttpParams();
		// HttpConnectionParams.setConnectionTimeout(httpParams, 80000);
		// HttpConnectionParams.setSoTimeout(httpParams, 80000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = null;

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData,
					HTTP.UTF_8);
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				responseStatus = 1;
			} else {
				responseStatus = 2;
				httpPost.abort();
			}
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e("HttpTools", e.getMessage());
			responseStatus = 2;

			return null;
		}

		// 响应正常
		if (responseStatus != 2) {
			try {
				HttpEntity httpEntity = response.getEntity();
				InputStream ins = httpEntity.getContent();
				Object resultMessage = ihandler.parseResponse(ins);
				return resultMessage;
			} catch (Exception e) {
				Log.e("HttpTools", e.getMessage());
				responseStatus = 3;
				return null;
			}
		}

		return null;
	}

	public static Object getAndParse(String url, Map<String, String> data,
			Ihandler ihandler) {
		
		url=buildUri(url,data);
		System.out.println("------url url----------" + url);
		
//		List<NameValuePair> list = new ArrayList<NameValuePair>();
//		if (data != null) {
//			NameValuePair pair;
//			for (Map.Entry<String, String> m : data.entrySet()) {
//				pair = new BasicNameValuePair(m.getKey(), m.getValue());
//				list.add(pair);
//			}
//		}
//		url += list.toString();
		
//		if (data != null) {
//			url = buildGetMethod(url, data).build().toString();
//			System.out.println("------------------------------------->"+url);
//		}
		
		System.out.println("------url url----------" + url);
		int status = 0;
		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			// HttpConnectionParams.setSoTimeout(httpParameters, 50000);
			HttpConnectionParams.setTcpNoDelay(httpParameters, true);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = httpclient.execute(httpRequest);

			status = httpResponse.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {

				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream ins = httpEntity.getContent();
				Object resultMessage = ihandler.parseResponse(ins);
				return resultMessage;
			}

		} catch (Exception e) {
			e.printStackTrace();

			System.out
					.println("==============connection wifi fail,e.printStackTrace() : "
							+ e.getMessage());
			return null;
		}
		return null;
	
	}
	/**
	 * simsunny
	 * @param url
	 * @param data
	 * @param ihandler
	 * @return
	 */
	public static Object getAndParse(String url, int data,
			Ihandler ihandler) {
		
		url=url+data;
		System.out.println("------url simsunny url----------" + url);
		
//		List<NameValuePair> list = new ArrayList<NameValuePair>();
//		if (data != null) {
//			NameValuePair pair;
//			for (Map.Entry<String, String> m : data.entrySet()) {
//				pair = new BasicNameValuePair(m.getKey(), m.getValue());
//				list.add(pair);
//			}
//		}
//		url += list.toString();
		
//		if (data != null) {
//			url = buildGetMethod(url, data).build().toString();
//			System.out.println("------------------------------------->"+url);
//		}
		
		System.out.println("------url url----------" + url);
		int status = 0;
		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			// HttpConnectionParams.setSoTimeout(httpParameters, 50000);
			HttpConnectionParams.setTcpNoDelay(httpParameters, true);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			
			status = httpResponse.getStatusLine().getStatusCode();
			
			if (status == HttpStatus.SC_OK) {
				
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream ins = httpEntity.getContent();
				Object resultMessage = ihandler.parseResponse(ins);
				return resultMessage;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			System.out
			.println("==============connection wifi fail,e.printStackTrace() : "
					+ e.getMessage());
			return null;
		}
		return null;
		
	}

	/**
	 * 执行get方法
	 * 
	 * @param context
	 * @param url
	 * @param data
	 * @param ihandler
	 * @return
	 */
	public static Object getAndParse(Context context, String url,
			Map<String, String> data, Ihandler ihandler) {
		// try {
		// SpeakTitle.fromRegisterGetSessionId();
		// } catch (Exception e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		// }
		responseStatus = 0;
		if (data != null) {
			//url=buildUri(url,data);
			url = buildGetMethod(url, data).build().toString();
			System.out.println("url------------------------------------->"+url);
		}
		/*
		 * % | ^针对于http://202.165.178.45/sns/api/weibo/search/?sessionId=
		 * dbb8ee259b82afdd0c9e2266975152732e18ab6d&keyword=？ &page=1
		 */
		if (url.toString().trim().contains(" ")
				|| url.toString().trim().contains("%")
				|| url.toString().trim().contains("^")
				|| url.toString().trim().contains("|")
				|| url.toString().trim().contains("\\")) {
			return null;
		}
		HttpGet httpRequest = new HttpGet(url);
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpRequest);
		} catch (ClientProtocolException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			long start = System.currentTimeMillis();
			// Log.e("HttpTools-get-start", "start: " + start + ", context: "
			// + context);

			System.out.println("start: ------->getAndParse" + start);
			response = httpClient.execute(httpRequest);

			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				responseStatus = 1;
			} else {
				responseStatus = 2;
				// httpPost.abort();
			}
			// Log.e("HttpTools-get-end", "end: "
			// + (System.currentTimeMillis() - start) + "ms , " + url);
			System.out.println("end:getAndParse " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e("HttpTools-c", e.getMessage());
			try {

				Tools.myToast(context, "网络状态较差，链接超时");
				responseStatus = 2;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// 响应正常
		if (responseStatus != 2) {
			try {
				HttpEntity httpEntity = response.getEntity();
				InputStream ins = httpEntity.getContent();
				Object resultMessage = ihandler.parseResponse(ins);
				return resultMessage;
			} catch (Exception e) {
				if (e.getMessage() != null)
					Log.e("HttpTools-d", e.getMessage());
				responseStatus = 3;
			} finally {
				httpClient.getConnectionManager().shutdown();
			}

		}

		return null;
	}
}
