package com.findcab.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.findcab.R;
import com.findcab.mywidget.MyToast;
import com.findcab.object.Passengers;
import com.findcab.util.Constant;
import com.findcab.util.HttpTools;
import com.findcab.util.MD5;
import com.findcab.util.RandomValidateCode;
import com.findcab.util.Tools;

/**
 * 登陆
 * 
 * @author yuqunfeng
 * 
 */
public class LandActivity extends Activity implements OnClickListener,BDLocationListener,OnTouchListener{

	//private EditText nameEditText = null;修改ui后没用了 simsunny
	//private EditText passEditText = null;修改ui后没用了 simsunny
	public int pssengersID;
	private String error;
	private String code="1";
	private String code_getTime = null;
	private  int timer =60;
	//	private boolean is=false;
	private Passengers psInfo;

	private Button butt_land = null;
	private Button butt_verification = null;
	private EditText phEditText =null;
	private EditText edit_verification = null;
	public static Context context = null;

	private static String phNum=null;
	private static String verificationCode = null;
	private boolean countRunning ;

	public static final int SUCCESS = 1;
	public static final int PHONENULL = 2;
	public static final int PASSWORDNULL = 3;
	public static final int PHONEERROR = 4;
	public static final int PASSWORDERROR = 5;
	private static final int BTN_CHANGE=6;
	private static final int GETCODE_SUCCESS = 7;
	private static final int GETCODE_FAILED = 8;
	
	
	String DeviceId;

	public ProgressDialog pd;

	Passengers info;
	private String randomStr = null;

	private double lat;// 经度
	private double lng;// 纬度
	private LocationClient mLocClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		context = this;
		setContentView(R.layout.land2);
		Tools.init();
		initView();
		startLocation();
		
		HttpTools.checkNetWork(context);
		
	}

	@Override
	protected void onDestroy() {
		mLocClient.stop();
		super.onDestroy();
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()){
		case R.id.ph_verficition_btn_get_veri:
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(!countRunning){//如果正在倒计时,则按钮状态不变
					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
				}else{
					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_p);
				}
				
				break;
			case MotionEvent.ACTION_CANCEL:
//				butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
				break;
			case MotionEvent.ACTION_UP:
				butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
				if(!countRunning){
					phNum = phEditText.getText().toString().trim();
					timer=60;
					if (!phNum.equals("")&&phNum.length()==11) {
						if(HttpTools.isNetworkAvailable(this)){
							countRunning=true;
							btnChanged();
							getVerification();
						}else{
							MyToast toast = new MyToast(context,"网络连接错误，请检查网络连接！");
							toast.startMyToast();
						}
						
					} else {
						MyToast toast = new MyToast(context,"请正确输入手机号码！");
						toast.startMyToast();
					}
				}else{
					MyToast toast = new MyToast(context,"请倒计时结束后点击获取验证码！");
					toast.startMyToast();
				}
				break;
				
			}
			break;
			
			
		}
		return false;
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		//提交验证码（登录）
		case R.id.ph_verficition_btn_sub_veri:
			phNum = phEditText.getText().toString().trim();
			verificationCode = edit_verification.getText().toString().trim();
			land(phNum, verificationCode);
			break;
			//获取验证码
		case R.id.ph_verficition_btn_get_veri:
			if(!countRunning){
				phNum = phEditText.getText().toString().trim();
				timer=60;
				if (!phNum.equals("")&&phNum.length()==11) {
					if(HttpTools.isNetworkAvailable(this)){
						countRunning=true;
						btnChanged();
						getVerification();
					}else{
						MyToast toast = new MyToast(context,"网络连接错误，请检查网络连接！");
						toast.startMyToast();
					}
					
				} else {
					MyToast toast = new MyToast(context,"请正确输入手机号码！");
					toast.startMyToast();
				}
			}else{
				MyToast toast = new MyToast(context,"请倒计时结束后点击获取验证码！");
				toast.startMyToast();
			}
			
			break;

		default:

			break;
		}
	}

	Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BTN_CHANGE:
				if(timer>=0){
					butt_verification.setTextColor(Color.GRAY);
					butt_verification.setText(msg.obj+"秒后重获");
//					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
//					butt_verification.setEnabled(false);

					break;
				}
				else{
					countRunning=false;
					butt_verification.setTextColor(Color.BLACK);
					butt_verification.setText("获取验证码");
//					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
//					butt_verification.setEnabled(true);
					break;
				}
			case SUCCESS:
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				//startMainActivity();

				break;

			case PHONENULL:
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				// Tools.landDialog(context, "手机号码不能为空，请输入手机号", "登陆失败");
				butt_land.setEnabled(true);
				break;

			case PASSWORDNULL:
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				// Tools.landDialog(context, "密码不能为空，请输入密码", "登陆失败");
				butt_land.setEnabled(true);
				break;
			case Constant.FAILURE:
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				// Tools.landDialog(context, error, "登陆失败");
				Tools.myToast(context, error);
				butt_land.setEnabled(true);
				break;
			case GETCODE_SUCCESS:
				MyToast toast = new MyToast(LandActivity.this,"验证码已发送，请您注意查收短信！");
				toast.startMyToast();
				break;
			case GETCODE_FAILED:
				MyToast toast1 = new MyToast(LandActivity.this,"验证码获取失败！");
				toast1.startMyToast();
				break;
			}
		}

	};


	/**
	 * 保存用户信息
	 */
	private void save(String  mobile, String password ,int psID) {
		Editor sharedata = getSharedPreferences("data", 0).edit();

		sharedata.putString("psMobile", mobile );

		sharedata.putString("psPassword", password);

		sharedata.putInt("psID", psID);

		sharedata.commit();

	}
	/**
	 * 初始化view
	 */
	private void initView() {

		//输入电话号码的editview
		phEditText = (EditText) findViewById(R.id.ph_verificition_edit_ph);
		openInputMethod(phEditText);
		//输入验证码的editview
		edit_verification = (EditText) findViewById(R.id.ph_verificition_edit_veri);

		//用来登录
		butt_land = (Button) findViewById(R.id.ph_verficition_btn_sub_veri);
		butt_land.setOnClickListener(this);
		//用来发送验证码
		butt_verification = (Button) findViewById(R.id.ph_verficition_btn_get_veri);
//		butt_verification.setOnClickListener(this);
		butt_verification.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					if(countRunning){//如果正在倒计时,则按钮状态不变
						butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
					}else{
						butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_p);
					}
					
					break;
				case MotionEvent.ACTION_CANCEL:
					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
					break;
				case MotionEvent.ACTION_UP:
					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
					if(!countRunning){
						phNum = phEditText.getText().toString().trim();
						timer=60;
						if (!phNum.equals("")&&phNum.length()==11) {
							if(HttpTools.isNetworkAvailable(LandActivity.this)){
								countRunning=true;
								btnChanged();
								getVerification();
							}else{
								MyToast toast = new MyToast(context,"网络连接错误，请检查网络连接！");
								toast.startMyToast();
							}
							
						} else {
							MyToast toast = new MyToast(context,"请正确输入手机号码！");
							toast.startMyToast();
						}
					}else{
						MyToast toast = new MyToast(context,"请倒计时结束后点击获取验证码！");
						toast.startMyToast();
					}
					break;
					
				}
				return true;
			}
			
		});

	}

	//	/**
	//	 * 发送验证信息  
	//	 * 现在不用这个了方法了  simsunny
	//	 */
	//	private void sendSMS(String phone) {
	//		randomStr = RandomValidateCode.getRandomString();
	//		Map<String, String> map = new HashMap<String, String>();
	//		String url = "http://www.smsbao.com/sms";
	//
	//		MD5 md5 = new MD5();
	//		map.put("u", "fpwang");
	//		map.put("p", md5.getMD5ofStr("tiantiandache"));
	//		map.put("m", phone);
	//		map.put("c", randomStr);
	//		String result = HttpTools.PostDate(url, map);
	//
	//		if (result.equals("0")) {
	//
	////			Tools.myToast(context, "验证码已发送！");
	//			MyToast toast = new MyToast(context,"验证码已发送！");
	//			toast.startMyToast();
	//		} else {
	////			Tools.myToast(context, "发送失败！");
	//			MyToast toast = new MyToast(context,"发送失败！");
	//			toast.startMyToast();
	//
	//		}
	//
	//	}
	/**
	 * 获得验证码函数
	 * simsunny 
	 * 用(string)code=0来验证是否成功获得验证码
	 */
	private void getVerification(){
		//检查网络
		if(HttpTools.checkNetWork(this)){
			new Thread(new Runnable(){
				@Override
				public void run(){

					Map<String, String> map = new HashMap<String, String>();
					map.put("mobile", phNum);
					String result = HttpTools.GetDate(Constant.VERIFICATION, map);
					System.out.println("the verification is--------------------->"+result);

					try {
						if(result != null){
							JSONObject json = new JSONObject(result);
							code = json.getString("code");
							if(code.equals("0")){
								
								clearGetCodeTime();
								saveGetCodeTime(getNowTime());
								messageHandler.sendEmptyMessage(GETCODE_SUCCESS);
							}
						}else{
							messageHandler.sendEmptyMessage(GETCODE_FAILED);
						}
						

						System.out.println("code------------------------->"+code);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}


	}

	/**
	 * 登陆
	 */
	private void land(final String mobile, final String verification) {
		//检查网络
		if(HttpTools.checkNetWork(this)){
			//		if(code.equals("0")){
			//读取获得验证码时间
			double a = Double.parseDouble(getCodeTime());
			double b = Double.parseDouble(getNowTime());
			//判断验证码发送时间未超过30分钟

			if(getCodeTime() != null && a!= 0 && b-a < 1800 && b-a >0){
				System.out.println("--------------------------->"+"摁下登录");

				Map<String, String> map = new HashMap<String, String>();
				//			map.put("passenger[name]", "simsunny");//测试数据，具体用户名如何修改还不知道，无需求
				map.put("passenger[mobile]", mobile);
				map.put("passenger[password]", verification);
				map.put("passenger[androidDevice]", Tools.getDeviceId(this));
				map.put("passenger[lat]", String.valueOf(lat));
				map.put("passenger[lng]", String.valueOf(lng));
				Log.e("定位2", lat+"-"+lng+"-"+Tools.getDeviceId(this));
				String result = HttpTools.PostDate(Constant.SIGNIN, map);//原逻辑是通过手机号获取验证码，用验证码调用注册接口，现在逻辑是用手机号获取验证码，此时服务器就去注册，返回的验证码就是密码，用密码掉登陆接口

				if(result!=null){
					try {
						System.out.println("乘客注册的返回 -------------------->"+result);

						JSONObject jsonObject = new JSONObject(result);
						JSONObject passenger = jsonObject.optJSONObject("passenger");

						if(passenger!=null){
							psInfo= new Passengers(passenger);

							String psMobile = passenger.optString("mobile");
							String psPssword = passenger.optString("password");
							int   psID=passenger.optInt("id");

							System.out.println("注册乘客的电话--------------------->"+psMobile);
							System.out.println("注册乘客的密码--------------------->"+psPssword);
							System.out.println("注册乘客的Id--------------------->"+psID);

							save(psMobile,psPssword,psID);

							if(psMobile.equals(mobile)&&verification.equals(psPssword)){
								MyToast toast = new MyToast(context,"登陆成功");
								toast.startMyToast();
								Intent i =new Intent(LandActivity.this,LocationOverlay.class );
								startActivity(i);
								this.finish();
							}else{
								MyToast toast = new MyToast(context,"登录失败，请核对手机号和验证码");
								toast.startMyToast();
							}

						}else{
							//登陆失败
							MyToast toast = new MyToast(context,"登录失败");
							toast.startMyToast();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				MyToast toast = new MyToast(context,"请重新获取验证码！");
				toast.startMyToast();
			}
		}
	}


	/**
	 * 改变 提交验证button的状态
	 * simsunny
	 */
	private void btnChanged(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				//countRunning = true;
				while(countRunning){
					try{

						Thread.currentThread().sleep(1000);
						Message msg = new Message();
						timer--;
						msg.obj=timer;
						msg.what=BTN_CHANGE;
						messageHandler.sendMessage(msg);
						if(timer<0){
							Thread.currentThread().interrupt();
						}

					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		}).start();
	}


	private void startLocation(){
		mLocClient = new LocationClient(this);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		mLocClient.setLocOption(option);
		mLocClient.registerLocationListener(this);// 要实现BDLocationListener的接口
		mLocClient.start();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (HttpTools.checkNetWork(this)) {

			lat = location.getLatitude();
			lng = location.getLongitude();
			Log.e("定位1", lat+"-"+lng);
		}

	}


	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 关闭软键盘
	 */
	private void closeInputMethod(EditText et){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

	/**
	 * 打开软键盘
	 */
	private void openInputMethod(EditText et){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et, 0);
	}

	private void saveGetCodeTime(String time){
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		Editor temp_editor = sharedata.edit();
		temp_editor.putString("getcodetime", time);
		temp_editor.commit();
	}

	private String getCodeTime(){
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		code_getTime = sharedata.getString("getcodetime", "0");
		return code_getTime;
	}

	private void clearGetCodeTime(){
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		Editor temp_editor = sharedata.edit();
		temp_editor.putString("getcodetime", null);
		temp_editor.commit();
	}

	private String getNowTime(){
		Calendar calendar_now = Calendar.getInstance();
		calendar_now.set(calendar_now.get(Calendar.YEAR), calendar_now.get(Calendar.MONTH), calendar_now.get(Calendar.DAY_OF_MONTH), calendar_now.get(Calendar.HOUR_OF_DAY), calendar_now.get(Calendar.MINUTE), calendar_now.get(Calendar.SECOND));
		calendar_now.set(Calendar.MILLISECOND, 0);

		String time = String.format("%1$04d%2$02d%3$02d%4$02d%5$02d%6$02d",calendar_now.get(Calendar.YEAR),calendar_now.get(Calendar.MONTH)+1,calendar_now.get(Calendar.DAY_OF_MONTH),calendar_now.get(Calendar.HOUR_OF_DAY),calendar_now.get(Calendar.MINUTE),calendar_now.get(Calendar.SECOND));
		return time;
	}

	

	//	 public void initMyToast1(String text){
	//		 Toast temp_toast;
	//			LayoutInflater inflater = getLayoutInflater();
	//			View layout = inflater.inflate(R.layout.mywidget_toast1,null);
	//			LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(180, 70);
	//			layout.setLayoutParams(ly);
	//				   TextView title = (TextView) layout.findViewById(R.id.mywidget_toast1_textview);
	//				   title.setText(text);
	//				   temp_toast = new Toast(context);
	//				   temp_toast.setGravity(Gravity.CENTER, 0, 0);
	//				   temp_toast.setDuration(Toast.LENGTH_LONG);
	//				   temp_toast.setView(layout);
	//				   temp_toast.show();
	//		}

}