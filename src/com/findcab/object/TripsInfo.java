package com.findcab.object;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 路线的信息
 * 
 * @author yuqunfeng
 * 
 */
public class TripsInfo implements Serializable {

	/**
	 * 
	 */
	private static long serialVersionUID = 8246601128965437499L;
	private String appointment;
	private String created_at;
	private String end;
	private String end_lat;
	private String end_lng;
	private int id;
	private int passenger_id;
	private String start;
	private String start_lat;
	private String start_lng;
	private String updated_at;

	/**
	 * 
	 */

	public TripsInfo(JSONObject jObject) {
		try {
			appointment = jObject.getString("appointment");
			created_at = jObject.getString("created_at");
			end = jObject.getString("end");
			end_lat = jObject.getString("end_lat");
			end_lng = jObject.getString("end_lng");
			id = jObject.getInt("id");
			passenger_id = jObject.getInt("passenger_id");
			start = jObject.getString("start");
			start_lat = jObject.getString("start_lat");
			start_lng = jObject.getString("start_lng");
			updated_at = jObject.getString("updated_at");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public static void setSerialVersionUID(long serialVersionUID) {
		TripsInfo.serialVersionUID = serialVersionUID;
	}

	public String getAppointment() {
		return appointment;
	}

	public void setAppointment(String appointment) {
		this.appointment = appointment;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getEnd_lat() {
		return end_lat;
	}

	public void setEnd_lat(String end_lat) {
		this.end_lat = end_lat;
	}

	public String getEnd_lng() {
		return end_lng;
	}

	public void setEnd_lng(String end_lng) {
		this.end_lng = end_lng;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPassenger_id() {
		return passenger_id;
	}

	public void setPassenger_id(int passenger_id) {
		this.passenger_id = passenger_id;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getStart_lat() {
		return start_lat;
	}

	public void setStart_lat(String start_lat) {
		this.start_lat = start_lat;
	}

	public String getStart_lng() {
		return start_lng;
	}

	public void setStart_lng(String start_lng) {
		this.start_lng = start_lng;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

}
