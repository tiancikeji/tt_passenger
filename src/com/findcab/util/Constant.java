package com.findcab.util;

/**
 * @author xy
 * @date 2012-3-3
 */
public class Constant {
	/**
	 * 这个用来存储从信息对象
	 */
	public static final int SUCCESS = 111;
	public static final int FAILURE = 222;
	public static final int ERROR = 333;

	public static final String BASEURL = "http://vissul.com:8989/api/";
	public static final String DRIVERS = BASEURL + "drivers/";
	public static final String SIGNUP = BASEURL + "passengers/signup/";// 注册
	public static final String SIGNIN = BASEURL + "passengers/signin/";// 乘客登陆
	public static final String DRIVER_INFO = BASEURL + "drivers/";// 附近得司机信息
	public static final String TRIPS = BASEURL + "trips/";// 发布路线
	public static final String VERIFICATION= BASEURL+"/passengers/get_verification_code";//获取veification simsunny modify

	public static final String CONVERSATIONS = BASEURL + "conversations/";// 更新会话状态

	public static final String SIGNOUT = BASEURL + "passengers/signout/";// 退出

	public static final String GEOCODE = "http://maps.googleapis.com/maps/api/geocode/xml";

	
	public static final String VERSION_ID= "1.0.0";//版本号
	public static final int DATABASRE_VERSION = 1;//数据库版本
	
}
