package com.findcab.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.findcab.R;
import com.findcab.handler.BaseHandler;
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
public class LandActivity extends Activity implements OnClickListener {

	//private EditText nameEditText = null;修改ui后没用了 simsunny
	//private EditText passEditText = null;修改ui后没用了 simsunny
	private  int timer = 10;
	private EditText phEditText =null;
	private EditText edit_verification = null;
	
	//btn_lan 用来登录
	private Button butt_land = null;
	//butt_verification  用来获取验证码
	private Button butt_verification = null;
	
	//private Button signup = null; 修改ui后没用了 simsunny
	//butt_verification  用来获取验证码

	//private static String name = null;   
	//private static String password = null;
	private static String phNum=null;
	private static String verificationCode = null;
	public static Context context = null;
	


	public static final int SUCCESS = 1;
	public static final int PHONENULL = 2;
	public static final int PASSWORDNULL = 3;
	public static final int PHONEERROR = 4;
	public static final int PASSWORDERROR = 5;
	private static final int BTN_CHANGE=6;

	View aalayout;
	String DeviceId;

	String[] str = new String[3];

	//private TextView text_forget;
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
		// sendSMS("13811571125");

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//case R.id.butt_land:
		//提交验证码（登录）
		case R.id.ph_verficition_btn_sub_veri:

			phNum = phEditText.getText().toString().trim();
			verificationCode = edit_verification.getText().toString().trim();
			//password = passEditText.getText().toString().trim();
			//land(name, password);
			land(phNum, verificationCode);
			// if (verificationCode != null && !verificationCode.equals("")) {

			// } else
			if (verificationCode.equals("")) {
				// Tools.myToast(context, "请输入验证码！");
			} else if (!verificationCode.toLowerCase().equals(
					randomStr.toLowerCase())) {

				// Tools.myToast(context, "请输入正确验证码！");
			}
			break;
			/**
//		case R.id.signup: 修改ui后没有用了 simsunny
//			Intent intent = new Intent(LandActivity.this, Signup.class);
//			startActivityForResult(intent, 1);
//			break;
			 */
		//case R.id.butt_verification:
		//获取验证码
		case R.id.ph_verficition_btn_get_veri:
			phNum = phEditText.getText().toString().trim();
			
			timer=10;
			btn_changed();

			if (!phNum.equals("")&&phNum.length()==11) {

				sendSMS(phNum);
			} else {
				Tools.myToast(context, "请输入手机号码！");
			}
			break;

		default:
			break;
		}
	}

	private String error;


	Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BTN_CHANGE:
				if(timer>=0){
					butt_verification.setText("还有"+ msg.obj+"获取验证码");
					butt_verification.setEnabled(false);
					break;
				}
				else{
					butt_verification.setText("获取验证码");
					butt_verification.setEnabled(true);
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
	 * 登陆成功跳转页面
	 */
//	public void startMainActivity() {
//		Intent intent = new Intent(LandActivity.this, LocationOverlay.class);
//		intent.putExtra("name", name);
//		intent.putExtra("password", password);
//		intent.putExtra("Passengers", info);
//
//		startActivity(intent);
//		// setResult(0, intent);
//		finish();
//	}

	//貌似现在不用返回了，所以我给也去掉了simsunny
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if (resultCode == 1) {
//
//			name = data.getStringExtra("name");
//			password = data.getStringExtra("password");
//			land(name, password);
//		}
//	}

	/**
	 * 保存用户信息
	 */
	private void save(String name, String password) {
		Editor sharedata = getSharedPreferences("data", 0).edit();

		sharedata.putString("password", password);

		sharedata.putString("name", name);

		sharedata.commit();

	}
	/**
	 * 初始化view
	 */
	private void initView() {
		// data = getData();
		
		//nameEditText = (EditText) findViewById(R.id.edit_account);
		
		//输入电话号码的editview
		phEditText = (EditText) findViewById(R.id.ph_verificition_edit_ph);
		//passEditText = (EditText) findViewById(R.id.edit_password);
		//输入验证码的editview
		edit_verification = (EditText) findViewById(R.id.ph_verificition_edit_veri);
		//edit_verification = (EditText) findViewById(R.id.edit_verification);

		//nameEditText.addTextChangedListener(nameWatcher);

		
		
//		text_forget = (TextView) findViewById(R.id.forget_password);
//		text_forget.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
//		text_forget.setTextColor(Color.BLUE);

		//butt_land = (Button) findViewById(R.id.butt_land);
		//用来登录
		butt_land = (Button) findViewById(R.id.ph_verficition_btn_sub_veri);
		butt_land.setOnClickListener(this);
		
//		signup = (Button) findViewById(R.id.signup);
//		signup.setOnClickListener(this);
		
		//butt_verification = (Button) findViewById(R.id.butt_verification);
		//用来发送验证码
		butt_verification = (Button) findViewById(R.id.ph_verficition_btn_get_veri);
		butt_verification.setOnClickListener(this);
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

	/**
	 * 登陆
	 */
	private void land(final String name, final String password) {

		if (HttpTools.checkNetWork(context)) {

			pd = ProgressDialog.show(context, "", "正在登陆...", true, true);
			
			Intent i =new Intent(LandActivity.this,LocationOverlay.class );
			startActivity(i);
			/**
			 * 用来验证手机和验证码是佛正确，现在对后台的部署还不是很了解，等
			 * 雷哥回来再问问，这个先留作接口
			 */
//			new Thread(new Runnable() {
//
//				public void run() {
//
//					if (name.equals("")) {
//						messageHandler.sendEmptyMessage(PHONENULL);
//					} else if (password.equals("")) {
//
//						messageHandler.sendEmptyMessage(PASSWORDNULL);
//					} else if (name.equals("") && password.equals("")) {
//
//						messageHandler.sendEmptyMessage(PHONENULL);
//					} else {// 不为空
//
//						Map<String, String> map = new HashMap<String, String>();
//						MD5 md5 = new MD5();
//						map.put("passenger[mobile]", name);
//						map.put("passenger[password]", md5
//								.getMD5ofStr(password));
//
//						try {
//							String resultString = (String) HttpTools
//									.postAndParse(Constant.SIGNIN, map,
//											new BaseHandler());
//							if (resultString != null) {
//
//								JSONObject jsonObject = new JSONObject(
//										resultString);
//								if (jsonObject.has("error")) {
//
//									error = jsonObject.getString("error");
//									messageHandler
//											.sendEmptyMessage(Constant.FAILURE);
//									return;
//								}
//
//								if (jsonObject.has("passenger")) {
//
//									info = new Passengers(jsonObject
//											.getJSONObject("passenger"));
//								}
//							}
//
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							Tools.myToast(context, "请检查网络");
//							return;
//						}
//						save(name, password);
//						messageHandler.sendEmptyMessage(SUCCESS);
//					}
//				}
//			}).start();
		}
	}
	/**
	 * 名字Watcher
	 */
	private TextWatcher nameWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	/**
	 * 改变 提交验证button的状态
	 */
	private void btn_changed(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				
				boolean isRunning = true;
				while(isRunning){
					try{
						Thread.currentThread().sleep(1000);
						Message msg = new Message();
						timer--;
						msg.obj=timer;
						msg.what=BTN_CHANGE;
						messageHandler.sendMessage(msg);
					
					}
					catch(InterruptedException e){
						
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}