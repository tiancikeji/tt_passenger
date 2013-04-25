package com.findcab.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findcab.R;
import com.findcab.handler.BaseHandler;
import com.findcab.mywidget.MyToast;
import com.findcab.object.PassengerInfo;
import com.findcab.util.Constant;
import com.findcab.util.HttpTools;
import com.findcab.util.MD5;
import com.findcab.util.RandomValidateCode;
import com.findcab.util.Tools;

/**
 * 注册
 * 
 * @author yuqunfeng
 * 
 */
public class Signup extends Activity implements OnClickListener {

	private Button back, start;
	private Button butt_verification;
	private EditText edit_name, edit_mobile, edit_password;
	private String name, mobile, password;
	private CheckBox checkBox;
	private static Context context;
	private Location location;
	private double lat;
	private double lng;
	private TextView item;
	private PassengerInfo info;
	public static final int PHONENULL = 2;
	public static final int PASSWORDNULL = 3;
	public static final int PHONEERROR = 4;
	public static final int PASSWORDERROR = 5;
	private String randomStr = null;
	private EditText edit_verification;
	private static String verificationCode = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		initView();
		initLocation();
		Tools.init();
	}

	private void initView() {
		context = this;
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		start = (Button) findViewById(R.id.start);
		start.setOnClickListener(this);

		edit_name = (EditText) findViewById(R.id.name);
		edit_mobile = (EditText) findViewById(R.id.mobile);
		edit_password = (EditText) findViewById(R.id.password);
		edit_verification = (EditText) findViewById(R.id.edit_verification);
		butt_verification = (Button) findViewById(R.id.butt_verification);
		butt_verification.setOnClickListener(this);
		checkBox = (CheckBox) findViewById(R.id.checkBox);

		item = (TextView) findViewById(R.id.item);
		item.setOnClickListener(this);

		if (initGPS()) {

			LocationManager locationManager;
			String serviceName = Context.LOCATION_SERVICE;
			locationManager = (LocationManager) this
					.getSystemService(serviceName);
			// 查询条件
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);

			String provider = locationManager.getBestProvider(criteria, true);
			location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				lat = location.getLatitude();
				lng = location.getLongitude();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.butt_verification:
			mobile = edit_mobile.getText().toString().trim();

			if (!mobile.equals("")) {

				sendSMS(mobile);
			} else {
				Tools.myToast(context, "请输入手机号码！");
			}
			break;
		case R.id.back:

			finish();
			break;
		case R.id.start:
			name = edit_name.getText().toString().trim();
			mobile = edit_mobile.getText().toString().trim();
			password = edit_password.getText().toString().trim();

			verificationCode = edit_verification.getText().toString().trim();

			if (verificationCode.toLowerCase().equals(randomStr.toLowerCase())) {
				// 验证码匹配成功
				if (name.equals("")) {
					messageHandler.sendEmptyMessage(PHONENULL);
				} else if (password.equals("")) {

					messageHandler.sendEmptyMessage(PASSWORDNULL);
				} else if (name.equals("") && password.equals("")) {

					messageHandler.sendEmptyMessage(PHONENULL);
				} else {

					postInfo();
				}

			} else if (verificationCode.equals("")) {
				Tools.myToast(context, "请输入验证码！");
			} else if (!verificationCode.toLowerCase().equals(
					randomStr.toLowerCase())) {

				Tools.myToast(context, "请输入正确验证码！");
			}

			break;
		case R.id.item:
			Tools
					.landDialog(
							context,
							"1注册条款的接受一旦会员在注册页面点击“我同意接受以上注册条款”后，这就表示会员已经阅读并且同意与第一眼相亲网网站达成协议，并接受所有的注册条款。",
							"注册条款");
			break;
		default:
			break;
		}
	}

	/**
	 * 提交信息
	 */
	private void postInfo() {
		new Thread(new Runnable() {
			public void run() {
				name = edit_name.getText().toString().trim();
				mobile = edit_mobile.getText().toString().trim();
				password = edit_password.getText().toString().trim();

				// if (!checkBox.isChecked()) {
				//
				// Tools.myToast(context, "请确认是否已经阅读同意条款！");
				//
				// }
				MD5 md5 = new MD5();
				Map<String, String> map = new HashMap<String, String>();
				map.put("passenger[name]", name);
				map.put("passenger[mobile]", mobile);
				map.put("passenger[password]", md5.getMD5ofStr(password));
				map.put("passenger[androidDevice]", Tools.getDeviceId(context));

				if (lat == 0) {
					lat = 39.876757965948;
					lng = 116.65188108138;

				}
				map.put("passenger[lat]", String.valueOf(lat));
				map.put("passenger[lng]", String.valueOf(lng));
				String result = (String) HttpTools.postAndParse(
						Constant.SIGNUP, map, new BaseHandler());

				try {
					JSONObject object = new JSONObject(result);
					JSONObject jsonObject = object.getJSONObject("passenger");
					info = new PassengerInfo(jsonObject);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (info != null) {
					messageHandler.sendEmptyMessage(Constant.SUCCESS);

					return;
				}
				messageHandler.sendEmptyMessage(Constant.FAILURE);
				return;
			}
		}).start();
	}

	Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:
				// Tools.landDialog(context, null, "注册成功");

				Intent intent = new Intent(Signup.this, LandActivity.class);
				intent.putExtra("name", mobile);
				intent.putExtra("password", password);

				setResult(1, intent);
				finish();
				break;
			case Constant.FAILURE:
				Tools.landDialog(context, null, "注册失败");

				break;
			case PHONENULL:

				// Tools.landDialog(context, "手机号码不能为空，请输入手机号", "登陆失败");
				myDialog().show();
				break;

			case PASSWORDNULL:

				// Tools.landDialog(context, "密码不能为空，请输入密码", "登陆失败");
				Tools.myToast(context, "密码不能为空");

				break;
			}
		}
	};

	private boolean initGPS() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// 判断GPS模块是否开启，如果没有则开启
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//			Toast.makeText(this, "GPS is not open,Please open it!",
//					Toast.LENGTH_SHORT).show();
			MyToast toast = new MyToast(this,"GPS is not open,Please open it!");
			toast.startMyToast();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);

			return false;
		} else {
//			Toast.makeText(this, "GPS is ready", Toast.LENGTH_SHORT);
			MyToast toast = new MyToast(this,"GPS is ready");
			toast.startMyToast();
		}
		return true;
	}

	/**
	 * 初始化所在GPS位置
	 */
	private void initLocation() {

		LocationManager locationManager;
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) this.getSystemService(serviceName);
		// 查询条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {

			lat = location.getLatitude();
			lng = location.getLongitude();

		}
	}

	/**
	 * 发送验证信息
	 */
	private void sendSMS(String phone) {
		randomStr = RandomValidateCode.getRandomString();
		Map<String, String> map = new HashMap<String, String>();
		String url = "http://www.smsbao.com/sms";

		MD5 md5 = new MD5();
		map.put("u", "fpwang");
		map.put("p", md5.getMD5ofStr("tiantiandache"));
		map.put("m", phone);
		map.put("c", randomStr);
		String result = HttpTools.PostDate(url, map);

		if (result.equals("0")) {

			Tools.myToast(context, "验证码已发送！");
		} else {
			Tools.myToast(context, "发送失败！");

		}

	}

	public static Dialog myDialog() {
		final Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog);

		LinearLayout dialog_layout = (LinearLayout) dialog
				.findViewById(R.id.dialog_layout);

		dialog_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		return dialog;
	}
}
