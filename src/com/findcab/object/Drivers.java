package com.findcab.object;

import org.json.JSONObject;

/**
 * 附近得司机
 * 
 * @author yuqunfeng
 * 
 */
public class Drivers {

	private String androidDevice;
	private String car_license;
	private String car_service_number;
	private String car_type;
	private String created_at;
	private int id;
	private String lat;
	private String lng;
	private String mobile;
	private String name;
	private String password;
	private int rate;
	private String updated_at;

	public Drivers(JSONObject jObject) {

		androidDevice = jObject.optString("androidDevice");
		car_license = jObject.optString("car_license");
		car_service_number = jObject.optString("car_service_number");
		car_type = jObject.optString("car_type");
		created_at = jObject.optString("created_at");
		id = jObject.optInt("id");
		lat = jObject.optString("lat");
		lng = jObject.optString("lng");
		mobile = jObject.optString("mobile");
		name = jObject.optString("name");
		password = jObject.optString("password");
		rate = jObject.optInt("rate");
		updated_at = jObject.optString("updated_at");

	}

	public String getAndroidDevice() {
		return androidDevice;
	}

	public void setAndroidDevice(String androidDevice) {
		this.androidDevice = androidDevice;
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

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
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
