package com.findcab.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findcab.R;
import com.findcab.handler.TripsHandler;
import com.findcab.mywidget.MyToast;
import com.findcab.object.TripsInfo;
import com.findcab.util.Constant;
import com.findcab.util.HttpTools;
import com.findcab.util.MapUtility;
import com.findcab.util.Tools;

/**
 * 叫车
 * 
 * @author yuqunfeng
 * 
 */
public class CallActivity extends Activity implements OnClickListener,BDLocationListener {

	private static final int MESSAGE_PUSHTRIP_SUCCESS = 10001;//发布路线成功
	private static final int MESSAGE_PUSHTRIP_FAILED = 10002;//发布路线成功
	
	private String start = null;
	private String end = null;
	public Context context = null;

	private Intent putIntent;
	private Intent getIntent;

	// private Button start_cancel, end_cancel;
	private Button okButton, cancelButton;
	//	private EditText edit_start, edit_end;
	private Button button_start;
	private Button button_end;
	private LinearLayout linearlayout_premium;
	private Button btn0,btn5,btn10,btn15,btn20;


	private int premium=0;

	private int ownerID;//用户id
	
	TripsInfo tripsInfo;//发布路线成功后返回TripInfo
	
	private final int START =12;
	private final int END = 22;
	private final int BLACK=0xff000000;//纯黑
	private final int WHITE=0xffffffff;//白色

	public ProgressDialog pd;//加载进度条
	
	GeoPoint startGP;
	GeoPoint endGP;
	public static BMapManager mBMapMan = null;
	private String mStrKey = "8BB7F0E5C9C77BD6B9B655DB928B74B6E2D838FD";

	Handler messageHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MESSAGE_PUSHTRIP_SUCCESS:
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				//保存用户输入的起点和终点
				saveInputRecord();

				//跳转页面
				putIntent = new Intent();
				putIntent.putExtra("put_trip_status", "success");
				putIntent.putExtra("premium", premium);
				//将路线信息返回LocationOverlay页面
				putIntent.putExtra("appointent",tripsInfo.getAppointment());
				putIntent.putExtra("created_at",tripsInfo.getCreated_at());
				putIntent.putExtra("end",tripsInfo.getEnd());
				putIntent.putExtra("end_lat",tripsInfo.getEnd_lat());
				putIntent.putExtra("end_lng",tripsInfo.getEnd_lng());
				putIntent.putExtra("start",tripsInfo.getStart());
				putIntent.putExtra("start_lat",tripsInfo.getStart_lat());
				putIntent.putExtra("start_lng",tripsInfo.getStart_lng());
				putIntent.putExtra("id",tripsInfo.getId());
				putIntent.putExtra("passenger_id",tripsInfo.getPassenger_id());
				setResult(1, putIntent);
				CallActivity.this.finish();
				
				break;
			case MESSAGE_PUSHTRIP_FAILED:
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				MyToast toast1 = new MyToast(context,"发布打车请求失败，请重新发送");
				toast1.startMyToast();
				break;
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call);
		
		// 得到乘客注册的id
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		ownerID = sharedata.getInt("psID", 0);
		
		Tools.init();
		initManager();
		initView();
		getInputRecord();//获取最近一次输入记录
	}


	@Override
	protected void onStop() {
		super.onStop();
	}


	@Override
	protected void onResume() {
		mBMapMan.start();
		super.onResume();
	}


	/**
	 * 初始化view
	 */
	private void initView() {

		context = this;
		button_start = (Button)findViewById(R.id.call_button_start);
		button_end = (Button)findViewById(R.id.call_button_end);

		button_start.setOnClickListener(this);
		button_end.setOnClickListener(this);

		okButton = (Button) findViewById(R.id.ok);
		okButton.setOnClickListener(this);

		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);

		//加价控件
		linearlayout_premium = (LinearLayout)findViewById(R.id.calling_premium);

		btn0=(Button)findViewById(R.id.calling_premium_btn0);
		btn0.setOnClickListener(this);
		btn0.setBackgroundResource(R.drawable.calling_premium_p);
		btn0.setTextColor(WHITE);

		btn5=(Button)findViewById(R.id.calling_premium_btn5);
		btn5.setOnClickListener(this);

		btn10=(Button)findViewById(R.id.calling_premium_btn10);
		btn10.setOnClickListener(this);

		btn15=(Button)findViewById(R.id.calling_premium_btn15);
		btn15.setOnClickListener(this);

		btn20=(Button)findViewById(R.id.calling_premium_btn20);
		btn20.setOnClickListener(this);
		//加价功能先取消
		linearlayout_premium.setVisibility(View.GONE);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.call_button_start:
			getIntent= new Intent();
			getIntent.setClass(CallActivity.this,PutDestinationActivity.class );
			getIntent.putExtra("putDestination", START);
			getIntent.putExtra("address_name", button_start.getText().toString());

			startActivityForResult(getIntent,START);
			break;
		case R.id.call_button_end:
			getIntent= new Intent();
			getIntent.putExtra("putDestination", END);
			getIntent.putExtra("address_name", button_end.getText().toString());
			getIntent.setClass(CallActivity.this,PutDestinationActivity.class );
			startActivityForResult(getIntent,END);
			break;
		case R.id.cancel:
			clearInputRecord();
			finish();
			break;

		case R.id.ok:
//			start = button_start.getText().toString().trim();
			end = button_end.getText().toString().trim();
			//判断起点终点是否输入
			if (!end.equals("")) {
				//不能获取经纬度，因此此代码注释
				sendTrips();
				
			}else{
				MyToast toast = new MyToast(context,"请输入目的地");
				toast.startMyToast();
			}
			break;

		case R.id.calling_premium_btn0:
			setBtn();
			btn0.setBackgroundResource(R.drawable.calling_premium_p);
			btn0.setTextColor(WHITE);
			premium=putPremium(0);
			break;
		case R.id.calling_premium_btn5:
			setBtn();
			btn5.setBackgroundResource(R.drawable.calling_premium_p);
			btn5.setTextColor(WHITE);
			premium=putPremium(5);
			break;  
		case R.id.calling_premium_btn10:
			setBtn();
			btn10.setBackgroundResource(R.drawable.calling_premium_p);
			btn10.setTextColor(WHITE);
			premium=putPremium(10);
			break;
		case R.id.calling_premium_btn15:
			setBtn();
			btn15.setBackgroundResource(R.drawable.calling_premium_p);
			btn15.setTextColor(WHITE);
			premium=putPremium(15);
			break;
		case R.id.calling_premium_btn20:
			setBtn();
			btn20.setBackgroundResource(R.drawable.calling_premium_p);
			btn20.setTextColor(WHITE);
			premium=putPremium(20);
			break;

		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent
			data) {

		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode){
		case START:
			if(data!=null){
				if (data.hasExtra("start")) {

					start = data.getStringExtra("start");
					System.out.println("start------>" + start);
					//					 edit_start.setText(start );
					button_start.setText(start);
					Log.e("返回数据", "="+start);

				} else {

					//start = startAddress;

				}
			}
			break;
		case END:
			if(data!=null){
				if (data.hasExtra("end")) {

					end = data.getStringExtra("end");
					System.out.println("end------>" + end);
					//						 edit_end.setText(end);
					button_end.setText(end);
					Log.e("返回数据", "="+end);
				} else {

					//start = startAddress;

				}
			}

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//按返回键返回前以页面，此页面输入地址不保存
			clearInputRecord();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	private void setBtn(){
		btn0.setBackgroundResource(R.drawable.calling_premium_n);
		btn0.setTextColor(BLACK);
		btn5.setBackgroundResource(R.drawable.calling_premium_n);
		btn5.setTextColor(BLACK);
		btn10.setBackgroundResource(R.drawable.calling_premium_n);
		btn10.setTextColor(BLACK);
		btn15.setBackgroundResource(R.drawable.calling_premium_n);
		btn15.setTextColor(BLACK);
		btn20.setBackgroundResource(R.drawable.calling_premium_n);
		btn20.setTextColor(BLACK);
	}

	private int putPremium(int premium){
		return premium;
	}

	/**
	 * 保存用户输入地址
	 */
	private void saveInputRecord(){
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		Editor temp_editor = sharedata.edit();
		temp_editor.putString("inputrecord_start", button_start.getText().toString());
		temp_editor.putString("inputrecord_end", button_end.getText().toString());
		temp_editor.commit();
	}

	/**
	 * 读取用户输入地址
	 */
	private void getInputRecord(){
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		String temp_start = sharedata.getString("inputrecord_start", null);
		String temp_end = sharedata.getString("inputrecord_end", null);
		if(temp_start != null && !temp_start.equals("")){
			//			 edit_start.setText(temp_start);
			button_start.setText(temp_start);
			startGP = getGeo(temp_start);
		}
		if(temp_end != null && !temp_end.equals("")){
			//			 edit_end.setText(temp_end);
			button_end.setText(temp_end);
			endGP = getGeo(temp_end);
		}
	}

	//清空保存起点终点
	private void clearInputRecord(){
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		Editor temp_editor = sharedata.edit();
		temp_editor.putString("inputrecord_start", null);
		temp_editor.putString("inputrecord_end", null);
		temp_editor.commit();
	}

	/**
	 * 发布路线
	 */
	private void sendTrips() {
		pd = ProgressDialog.show(context, "", "正在发布打车请求", true, true);
		if (HttpTools.checkNetWork(context)) {

			new Thread(new Runnable() {
				public void run() {
					try {
						if(button_start.getText().toString().trim().equals("我的位置")){
							startGP = getGeo(start);
						}else{
							startGP = getGeo(start);
						}
						endGP = getGeo(end);
						
						double startLat = startGP.getLatitudeE6() / (1e6);
						double startLng = startGP.getLongitudeE6() / (1e6);
						double endLat = endGP.getLatitudeE6()/(1e6);
						double endLng = endGP.getLongitudeE6()/(1e6);
						
						Log.e("发布路线时的经纬度", startLat+"="+startLng+"="+endLat+"="+endLng);
						Map<String, String> map = new HashMap<String, String>();
						map.put("trip[passenger_id]", String.valueOf(ownerID));
						map.put("trip[start]", start);
						map.put("trip[start_lat]", String.valueOf(startLat));
						map.put("trip[start_lng]", String.valueOf(startLng));
						map.put("trip[end]", end);
						// map.put("trip[appointment]",
						// String.valueOf(premium));//是预约叫车的接口，现在不用
						map.put("trip[end_lat]", String.valueOf(endLat));
						map.put("trip[end_lng]", String.valueOf(endLng));
						map.put("trip[price]", String.valueOf(premium));
						// 这里返回有passenger_id和trips_id
						tripsInfo = (TripsInfo) HttpTools.postAndParse(
								Constant.TRIPS, map, new TripsHandler());

						if (tripsInfo != null) {
							Log.e("测试发布路线是否成功", tripsInfo.getCreated_at()+"="+tripsInfo.getStart()+"="+tripsInfo.toString());
							// 表示发送成功 可以接收会话通知（isGetConversation用来控制接收会话的循环条件）
//							isGetConversation = true;
//							getConversations();
							messageHandler.sendEmptyMessage(MESSAGE_PUSHTRIP_SUCCESS);

						}else{
							messageHandler.sendEmptyMessage(MESSAGE_PUSHTRIP_FAILED);
						}
					} catch (Exception e) {
						e.printStackTrace();
						messageHandler.sendEmptyMessage(MESSAGE_PUSHTRIP_FAILED);
					}
				}
			}).start();

		}
	}
	
	/**
	 * @param locationName
	 * @return 返回地址的坐标 simsunny
	 */
	private GeoPoint getGeo(String locationName) {
		Log.e("解析地址", locationName);
		JSONObject object = MapUtility.getLocationInfo(locationName);
		GeoPoint point = MapUtility.getGeoPoint(object);
		Log.e("解析地址-Point", point.getLatitudeE6()+"="+point.getLongitudeE6());
		return point;
	}

	/**
	 * 初始化BMapManager
	 */
	private void initManager() {
		context = this;
		if (mBMapMan == null) {
			mBMapMan = new BMapManager(this);
		}
		mBMapMan.init(mStrKey, new MyGeneralListener());

	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {

	}


	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}
	
	class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is " + iError);
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "
					+ iError);
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {

			}
		}
	}
}