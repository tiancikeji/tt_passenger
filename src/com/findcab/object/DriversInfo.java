package com.findcab.object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 附近得司机信息
 * 
 * @author yuqunfeng
 * 
 */
public class DriversInfo {

	/**
	 * // 0为空，1为满
	 */
	private int status;
	private int online;
	private String car_license;
	private String car_service_number;
	private String car_type;
	private String created_at;
	private int id;
	private double lat;
	private double lng;
	private int mobile;
	private String name;
	private String password;
	private int rate;
	private String updated_at;

	public DriversInfo(JSONObject jObject) {

		try {
			car_license = jObject.getString("car_license");
			car_service_number = jObject.getString("car_service_number");
			car_type = jObject.getString("car_type");
			created_at = jObject.getString("created_at");
			id = jObject.getInt("id");
			lat = jObject.getDouble("lat");
			lng = jObject.getDouble("lng");
			mobile = jObject.getInt("mobile");
			name = jObject.getString("name");
			password = jObject.getString("password");
			rate = jObject.getInt("rate");
			updated_at = jObject.getString("updated_at");
			status = jObject.getInt("status");
			online = jObject.getInt("online");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String createdAt) {
		created_at = createdAt;
	}

	public String getCar_license() {
		return car_license;
	}

	public void setCar_license(String carLicense) {
		car_license = carLicense;
	}

	public String getCar_service_number() {
		return car_service_number;
	}

	public void setCar_service_number(String carServiceNumber) {
		car_service_number = carServiceNumber;
	}

	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String carType) {
		car_type = carType;
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

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updatedAt) {
		updated_at = updatedAt;
	}

}
