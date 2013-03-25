package com.findcab.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MapUtility {
	public static JSONObject getLocationInfo(String address) {

		HttpGet httpGet = new HttpGet("http://maps.google."

		+ "com/maps/api/geocode/json?address=" + address

		+ "&sensor=false");

		HttpClient client = new DefaultHttpClient();

		HttpResponse response;

		StringBuilder stringBuilder = new StringBuilder();

		try {

			response = client.execute(httpGet);

			HttpEntity entity = response.getEntity();

			InputStream stream = entity.getContent();

			int b;

			while ((b = stream.read()) != -1) {

				stringBuilder.append((char) b);

			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		JSONObject jsonObject = new JSONObject();

		try {

			jsonObject = new JSONObject(stringBuilder.toString());

		} catch (JSONException e) {

			e.printStackTrace();

		}

		return jsonObject;

	}

	// converts JSONObject into a GeoPoint.

	public static GeoPoint getGeoPoint(JSONObject jsonObject) {

		Double lon = new Double(0);

		Double lat = new Double(0);

		try {

			lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)

			.getJSONObject("geometry").getJSONObject("location")

			.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)

			.getJSONObject("geometry").getJSONObject("location")

			.getDouble("lat");

		} catch (JSONException e) {

			e.printStackTrace();

		}

		return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));

	}

	/**
	 * 由经纬度获得地址
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @return
	 */

	private static JSONObject geocodeAddr(double lat, double lng) {
		String urlString = "http://ditu.google.com/maps/geo?q=+" + lat + ","
				+ lng + "&output=json&oe=utf8&hl=zh-CN&sensor=false";

		StringBuilder sTotalString = new StringBuilder();
		try {

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			InputStream urlStream = httpConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlStream));

			String sCurrentLine = "";
			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				sTotalString.append(sCurrentLine);
			}
			bufferedReader.close();
			httpConnection.disconnect(); // 关闭http连接

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(sTotalString.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static String getAddressByLatLng(double lat, double lng) {
		String address = null;
		JSONObject jsonObject = geocodeAddr(lat, lng);
		try {
			JSONArray placemarks = jsonObject.getJSONArray("Placemark");
			JSONObject place = placemarks.getJSONObject(0);
			address = place.getString("address");
			address = address.trim();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return address;
	}

	public static String getAddress(Location location) {
		String address = " ";
		try {
			URL url = new URL(
					"http://maps.google.cn/maps/geo?output=csv&key=abcdef&q="
							+ location.getLatitude() + ","
							+ location.getLongitude());
			try {
				URLConnection urlConn = url.openConnection();
				InputStreamReader ireader = new InputStreamReader(urlConn
						.getInputStream(), "utf-8");
				BufferedReader bf = new BufferedReader(ireader);
				while ((address = bf.readLine()) != null) {
					String[] addInfo = address.split(",");
					if (addInfo.length >= 3) {

						return addInfo[2].split(" ")[0];
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return address;

	}
}