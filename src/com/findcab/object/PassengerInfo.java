package com.findcab.object;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 乘客注册信息
 * 
 * @author yuqunfeng
 * 
 */
public class PassengerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7856519465440792211L;
	/**
	 * 
	 */

	private String created_at;
	private double distance;
	private int id;
	private double lat;
	private double lng;
	private int mobile;
	private String name;
	private String password;
	private String updated_at;

	public PassengerInfo(JSONObject jObject) {

		try {
			created_at = jObject.getString("created_at");
			distance = jObject.getDouble("distance");
			id = jObject.getInt("id");
			lat = jObject.getDouble("lat");
			lng = jObject.getDouble("lng");
			mobile = jObject.getInt("mobile");
			name = jObject.getString("name");
			password = jObject.getString("password");
			updated_at = jObject.getString("updated_at");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String createdAt) {
		created_at = createdAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updatedAt) {
		updated_at = updatedAt;
	}

}
