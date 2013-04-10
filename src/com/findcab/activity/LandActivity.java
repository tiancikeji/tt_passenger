package com.findcab.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findcab.R;
import com.findcab.handler.BaseHandler;
import com.findcab.object.Passengers;
import com.findcab.util.Constant;
import com.findcab.util.HttpTools;
import com.findcab.util.MD5;
import com.findcab.util.MyLogTools;
import com.findcab.util.RandomValidateCode;
import com.findcab.util.Tools;

/**
 * 登陆
 * 
 * @author yuqunfeng
 * 
 */
public class LandActivity extends Activity implements OnClickListener {

	//private EditText nameEditText = null;修改ui后没用了 simsunny
	//private EditText passEditText = null;修改ui后没用了 simsunny
	public int pssengersID;
	private String error;
	private String code="1";
	private  int timer =60;
	private boolean is=false;
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

	String DeviceId;

	public ProgressDialog pd;
	
	Passengers info;
	private String randomStr = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		context = this;
		setContentView(R.layout.land2);
		Tools.init();
		initView();

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		//提交验证码（登录）
		case R.id.ph_verficition_btn_sub_veri:

			phNum = phEditText.getText().toString().trim();
			verificationCode = edit_verification.getText().toString().trim();
			land(phNum, verificationCode);
			break;
		//获取验证码
		case R.id.ph_verficition_btn_get_veri:
			phNum = phEditText.getText().toString().trim();
			timer=60;
			if (!phNum.equals("")&&phNum.length()==11) {
				countRunning=true;
				btnChanged();
				getVerification();
					;
			} else {
				Tools.myToast(context, "请输入手机号码！");
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
					butt_verification.setText(msg.obj+"秒后重获");
					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_p);
					butt_verification.setEnabled(false);
					break;
				}
				else{
					countRunning=false;
					butt_verification.setText("获取验证码");
					butt_verification.setBackgroundResource(R.drawable.ph_verifi_get_ver_n);
					butt_verification.setEnabled(true);
					//这里应该有个杀死进程的方法的的，以后再想吧
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
		//输入验证码的editview
		edit_verification = (EditText) findViewById(R.id.ph_verificition_edit_veri);
		//用来登录
		butt_land = (Button) findViewById(R.id.ph_verficition_btn_sub_veri);
		butt_land.setOnClickListener(this);
		//用来发送验证码
		butt_verification = (Button) findViewById(R.id.ph_verficition_btn_get_veri);
		butt_verification.setOnClickListener(this);
		
		if (!HttpTools.checkNetWork(context)){
			Toast. makeText(this, "请您连接网络", 1000);
		}
	}

	/**
	 * 发送验证信息  
	 * 现在不用这个了方法了  simsunny
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
	/**
	 * 获得验证码函数
	 * simsunny 
	 * 用(string)code=0来验证是否成功获得验证码
	 */
	private void getVerification(){
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("mobile", phNum);
				String result = HttpTools.GetDate(Constant.VERIFICATION, map);
				System.out.println("the verification is--------------------->"+result);
				
				try {
					
					JSONObject json = new JSONObject(result);
					code = json.getString("code");
					System.out.println("code------------------------->"+code);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	/**
	 * 登陆
	 */
	private void land(final String mobile, final String verification) {
		
		
		
		if(code.equals("0")){
			
			System.out.println("--------------------------->"+"摁下登录");
						
			Map<String, String> map = new HashMap<String, String>();
			map.put("passenger[name]", "simsunny");
			map.put("passenger[mobile]", mobile);
			map.put("passenger[password]", verification);
			map.put("passenger[androidDevice]", Tools.getDeviceId(this));
			
			String result = HttpTools.PostDate(Constant.SIGNUP, map);
			
			
			if(result!=null){
				try {
					MyLogTools.e("LandActivity-Land", "="+result);
					JSONObject jsonObject = new JSONObject(result);
					JSONObject passenger = jsonObject.optJSONObject("passenger");
					if(passenger == null){
						MyLogTools.e("认证失败", "注册失败");
						Toast.makeText(context, "登录失败!", Toast.LENGTH_SHORT).show();
					}else{
						psInfo= new Passengers(passenger);
						if(passenger!=null){
							
//								String androidDevice = passenger.optString("androidDevice");
//								String creat_at = passenger.optString("created_at");
//								String lat = passenger.optString("lat");
//								String lag = passenger.optString("lag");
//								String mobile = a.optString("mobile");
//							    pssengersID = passenger.optInt("id");
//								psInfo= new Passengers(passenger );
							
							String psMobile = passenger.optString("mobile");
							String psPssword = passenger.optString("password");
							int   psID=passenger.optInt("id");
							
							System.out.println("注册乘客的电话--------------------->"+psMobile);
							System.out.println("注册乘客的密码--------------------->"+psPssword);
							System.out.println("注册乘客的Id--------------------->"+psID);
							
							save(psMobile,psPssword,psID);
							
							if(psMobile.equals(mobile)&&verification.equals(psPssword)){
								
								Intent i =new Intent(LandActivity.this,LocationOverlay.class );
								startActivity(i);
								this.finish();
							}else{
								Toast.makeText(context, "登录失败，请核对手机号和验证码", 1000).show();
							}
						}
					}
					
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
}