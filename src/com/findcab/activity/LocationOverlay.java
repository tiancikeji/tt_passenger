package com.findcab.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findcab.R;
import com.findcab.handler.BaseHandler;
import com.findcab.handler.ConversationsHandler;
import com.findcab.handler.DriversHandler;
import com.findcab.handler.TripsHandler;
import com.findcab.mywidget.MyToast;
import com.findcab.object.ConversationInfo;
import com.findcab.object.Drivers;
import com.findcab.object.DriversInfo;
import com.findcab.object.Passengers;
import com.findcab.object.TripsInfo;
import com.findcab.util.Constant;
import com.findcab.util.HttpTools;
import com.findcab.util.MapUtility;
import com.findcab.util.MyItemizedOverlay;
import com.findcab.util.MyJpushTools;
import com.findcab.util.Tools;

public class LocationOverlay extends Activity implements OnClickListener,
		BDLocationListener {

	public static MapView mMapView = null;
	public static BMapManager mBMapMan = null;
	private MapController mapController; // 用来得到mMapView的控制权,可以用它控制和驱动平移和缩放
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();// 存放overlayitem
	private LocationClient mLocClient;

	private double lat;// 经度
	private double lng;// 纬度
	private GeoPoint pt = null; // 经纬度
	List<GeoPoint> pList;// 存放经纬度的列表
	public static View mPopView = null; // 点击mark时弹出的气泡View
	private String mStrKey = "8BB7F0E5C9C77BD6B9B655DB928B74B6E2D838FD";
//	LocationListener mLocationListener = null;
	int iZoom = 0;

	/**
	 * callingTitle的布局
	 */
	private RelativeLayout callingTitle;
	private Button locate;
	private Button setting;
	private TextView title;
	/**
	 * waitingTitle的布局
	 */
	private RelativeLayout waitingTitle;
	private TextView waittingTimeEdit;
	private TextView cab_count;

	/**
	 * answerTitle的布局
	 */
	private RelativeLayout answerTitle;
	private TextView answerTitleName;
	private TextView answerTitleInfo;
	private TextView answerTitleDistance;
	private TextView answerTitlTime;
	private Button answerCallDriver;
	/**
	 * callingBottom的布局
	 */
	private LinearLayout callingBottom;
	private Button call;
	/**
	 * waitingBottom的布局
	 */
	private LinearLayout waitingBottom;
	private Button waitingCancle;
	/**
	 * answerBottom的布局
	 */
	private LinearLayout answerBottom;
	private Button answerCancle;
	/**
	 * dialog的布局
	 */
	private RelativeLayout timeOutDialog;
	private Button timeOutDialogBtnCancel;
	private Button timeOutDialogBtnContiune;
	private RelativeLayout dialog1;
	private Button dialog1BtnCancle;
	private Button dialog1BtnContiune;
	private RelativeLayout dialog2;
	private Button dialog2Btn1;
	private Button dialog2Btn2;
	private Button dialog2Btn3;
	private Button dialog2Btn4;

	private Context context;

	public static String address; // 这个addres通过myMKSearchListener来生成，具体的看看函数
	private String end;
	private String start;
	private String androidDevice;

	private boolean isCall;
	private boolean isGetConversation = false; // 标示是佛可以得到司机端的回话请求
	private boolean isAccept = true;

	private List<DriversInfo> driversList; // 司机列表
	ConversationInfo lastConversation = null;// 最新一条会话
	private Drivers acceptInfo;
	private Passengers info;
	private List<ConversationInfo> listLastConversation = new ArrayList<ConversationInfo>();

	public static final int LAND = 1;// 登陆
	public static final int SEND = 2;// 发送路线
	public static final int CANCLE_CALLING = 3;// 取消叫车
	public static final int TIMEOUT = 4;// 取消叫车
	public static final int CANCLE_ANSWER = 5;// 取消叫车
	private int judgeCancle;

	private int state;
	public static final int NORM = 1;// 正常状态
	public static final int CALLING = 2;// 請求中狀態
	public static final int ANSER = 3;// 請求中狀態

	private final int WAITING = 90;// 用来做标示的(设定时间)
	private int waitingTime = WAITING;
	public static boolean isWaiting = false;// 用来控制时间的暂停
	// private boolean isWaiting = false;// 用来控制时间的暂停
	private boolean isStartCount = true;// 控制是否启动倒数线程。只启动一次线程就好的

	private int premium;// 加价的返回值
	private int psID;// 乘客id的返回值
	private int ConversationID;
	private boolean isListern = false;
	private TelephonyManager manager;

	/**
	 * handler消息列表
	 */
	static final int MESSAGE_CONVERSATIONS_CHANGE = 10001;
	static final int MESSAGE_DRIVERS_CHANGE = 10002;
	static final int MESSAGE_PASSAGERS_CHANGE = 10003;

	// 处理所有LocationOverlay类中的handler异步处理
	Handler handlerMain = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_CONVERSATIONS_CHANGE:
				MyToast toast1 = new MyToast(context,"会话更新");
				toast1.startMyToast();
				getConversations();
				break;
			case MESSAGE_DRIVERS_CHANGE:
				MyToast toast2 = new MyToast(context,"附近司机更新");
				toast2.startMyToast();
				break;
			case MESSAGE_PASSAGERS_CHANGE:
				MyToast toast = new MyToast(context,"附近乘客更新");
				toast.startMyToast();
				break;

			}
		}

	};

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		initManager();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapview);
		Tools.init();
		iniView();
		
		// 设置设备别名，以用户名为别名，（最终要实现当用户换账号时，别名改变）
		MyJpushTools.setAlias(context, "passenger_" + psID);
		Log.e("推送别名","passenger_" + psID);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initMyBroadcastReceiver();// 动态注册广播
	}

	@Override
	protected void onStop() {
//		unregisterReceiver(MyReceiver);// 当主activity关闭，广播关闭
		super.onStop();
	}

	public class MyMKSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg0 == null) {
				System.out.println("没取到地址");
			} else {

				GeoPoint point = arg0.geoPt;
				System.out.println("地址：" + arg0.strAddr + ",坐标："
						+ point.getLatitudeE6() + "," + point.getLongitudeE6());

				if (address == null) {
					address = arg0.strAddr;

				}
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * @param locationName
	 * @return 返回地址的坐标 simsunny
	 */
	private GeoPoint getGeo(String locationName) {
		JSONObject object = MapUtility.getLocationInfo(locationName);
		GeoPoint point = MapUtility.getGeoPoint(object);
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

	private void iniView() {

		manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

		manager.listen(new PhoneCallListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

		System.out.println("启动locationOverlay-------------------------->");
		
		// 得到乘客注册的id
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		psID = sharedata.getInt("psID", 0);
		isWaiting = false;

		if (!HttpTools.isNetworkAvailable(context)) {
//			Toast.makeText(context, "请您连接网络", Toast.LENGTH_SHORT).show();
			MyToast toast = new MyToast(context,"请您连接网络");
			toast.startMyToast();
		}

		mMapView = (MapView) findViewById(R.id.bmapView);

		/**
		 * callingTitle的布局
		 */
		callingTitle = (RelativeLayout) findViewById(R.id.map_calling_title);
		locate = (Button) findViewById(R.id.map_calling_title_btn_fresh);
		locate.setOnClickListener(this);
		setting = (Button) findViewById(R.id.map_calling_title_btn_setting);
		setting.setOnClickListener(this);
		title = (TextView) findViewById(R.id.map_calling_title_txt);

		/**
		 * waitingTitle的布局
		 */
		waitingTitle = (RelativeLayout) findViewById(R.id.map_waiting_title);
		cab_count = (TextView) findViewById(R.id.map_waiting_title_txt_cab_count);
		waittingTimeEdit = (TextView) findViewById(R.id.map_waiting_title_waitintTxt);
		/**
		 * answerTitle的布局
		 */
		answerTitle = (RelativeLayout) findViewById(R.id.map_answer_title);
		answerTitleName = (TextView) findViewById(R.id.map_driver_head_text);
		answerTitleInfo = (TextView) findViewById(R.id.map_driver_air_text);
		answerTitleDistance = (TextView) findViewById(R.id.map_driver_distance_text);
		answerTitlTime = (TextView) findViewById(R.id.map_driver_time_text);
		answerCallDriver = (Button) findViewById(R.id.map_driver_call_button);
		answerCallDriver.setOnClickListener(this);
		/**
		 * callingBottom的布局
		 */
		callingBottom = (LinearLayout) findViewById(R.id.map_calling_bottom);
		call = (Button) findViewById(R.id.map_calling_bottom_btn_call);
		call.setOnClickListener(this);
		/**
		 * waitingBottom的布局
		 */
		waitingBottom = (LinearLayout) findViewById(R.id.map_waiting_bottom);
		waitingCancle = (Button) findViewById(R.id.map_waiting_bottom_btn_cancel);
		waitingCancle.setOnClickListener(this);

		/**
		 * answerBottom的布局
		 */
		answerBottom = (LinearLayout) findViewById(R.id.map_answer_bottom);
		answerCancle = (Button) findViewById(R.id.map_answr_bottom_btn_cancel);
		answerCancle.setOnClickListener(this);

		androidDevice = Tools.getDeviceId(context);
		/**
		 * dialog的布局
		 */
		timeOutDialog = (RelativeLayout) findViewById(R.id.map_dialog_timeout);
		timeOutDialogBtnCancel = (Button) findViewById(R.id.dialog_frist_cancel);
		timeOutDialogBtnCancel.setOnClickListener(this);
		timeOutDialogBtnContiune = (Button) findViewById(R.id.dialog_frist_contiune);
		timeOutDialogBtnContiune.setOnClickListener(this);

		dialog1 = (RelativeLayout) findViewById(R.id.map_dialog1);
		dialog1BtnCancle = (Button) findViewById(R.id.dialog_two_cancel);
		dialog1BtnCancle.setOnClickListener(this);
		dialog1BtnContiune = (Button) findViewById(R.id.dialog_two_contiune);
		dialog1BtnContiune.setOnClickListener(this);

		dialog2 = (RelativeLayout) findViewById(R.id.map_dialog2);
		dialog2Btn1 = (Button) findViewById(R.id.dialog_three_1);
		dialog2Btn1.setOnClickListener(this);
		dialog2Btn2 = (Button) findViewById(R.id.dialog_three_2);
		dialog2Btn2.setOnClickListener(this);
		dialog2Btn3 = (Button) findViewById(R.id.dialog_three_3);
		dialog2Btn3.setOnClickListener(this);
		dialog2Btn4 = (Button) findViewById(R.id.dialog_three_4);
		dialog2Btn4.setOnClickListener(this);

		mPopView = super.getLayoutInflater().inflate(R.layout.popview_title,
				null);
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);

		// 返回当前地图的缩放级别
		iZoom = mMapView.getZoomLevel();

		// 检查mapview是否支持双击放大效果
		// return boolean
		mMapView.setDoubleClickZooming(false);

		mMapView.setClickable(false);

		// 返回地图的MapController，这个对象可用于控制和驱动平移和缩放
		mapController = mMapView.getController();

		// 设置地图的缩放级别。 这个值的取值范围是[3,18]。
		mapController.setZoom(15);

		mLocClient = new LocationClient(this);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		mLocClient.setLocOption(option);
		mLocClient.registerLocationListener(this);// 要实现BDLocationListener的接口
		mLocClient.start();

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

	@Override
	protected void onPause() {
		// mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		// mLocationOverlay.disableMyLocation();
		// mLocationOverlay.disableCompass();
		// mBMapMan.stop();
		isGetConversation = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		// mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		// mLocationOverlay.enableMyLocation();
		// mLocationOverlay.enableCompass();
		mBMapMan.start();
		// isGetConversation = true;
		String fromActivity3 = getIntent().getStringExtra("fromDialog3");
		if (fromActivity3 != null) {

			if (fromActivity3.equals("dialog3")) {
				showNorm();
			}
		}

		super.onResume();
	}

	protected void onDestory() {
		isGetConversation = false;
		if (lastConversation != null) {
			String id = String.valueOf(ConversationID);
			System.out.print("取消的ID----------------------->" + ConversationID);
			changeConversationsStatus("-1", id);
		}
		super.onDestroy();
	}

	/**
	 * 内部类实现MKSearchListener接口,用于实现异步搜索服务
	 * 
	 * @author liufeng
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.map_calling_title_btn_fresh:

			// pt是现在的坐标
			if (HttpTools.checkNetWork(context)) {

				if (pt != null) {
					// mMapView.refresh();
					mMapView.getController().animateTo(pt);
					MyToast toast = new MyToast(context,"刷新成功");
					toast.startMyToast();
					getDrivers();
					// mMapView.refresh();

				} else {
//					Toast.makeText(LocationOverlay.this, "无法定位", Toast.LENGTH_SHORT).show();
					MyToast toast = new MyToast(context,"无法定位");
					toast.startMyToast();
				}
			}

			break;
		case R.id.map_calling_title_btn_setting:
			Intent i1 = new Intent(context, SettingActivity.class);
			startActivity(i1);
			break;
		case R.id.map_calling_bottom_btn_call:

			Intent i = new Intent(context, CallActivity.class);
			startActivityForResult(i, SEND);
			break;
		// calling界面取消
		case R.id.map_waiting_bottom_btn_cancel:
			// 并没有取消订单
			judgeCancle = CANCLE_CALLING;
			showDialog1();
			if (lastConversation != null) {
				String id = String.valueOf(ConversationID);
				System.out.print("取消的ID----------------------->"
						+ ConversationID);
				changeConversationsStatus("-1", id);
			}else{
				Log.e("等待应答中取消叫车","CANCLE_CALLING");
			}
			break;
		// answer界面取消
		case R.id.map_answr_bottom_btn_cancel:

			isGetConversation = false;// 不能在向服务器发送会话
			judgeCancle = CANCLE_ANSWER;
			if (lastConversation != null) {
				String id = String.valueOf(ConversationID);
				System.out.print("取消的ID----------------------->"
						+ ConversationID);
				changeConversationsStatus("-1", id);
			}else{
				Log.e("应答后取消叫车","CANCLE_ANSWER");
			}
			// Intent i2 = new Intent();
			// startActivityForResult(i2, CANCLE_ANSWER);
			
			showDialog1();
			break;
		// answer界面拨打电话
		case R.id.map_driver_call_button:
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ acceptInfo.getMobile()));
			startActivity(intent);
			break;
		// dialog倒计时时间到_确定取消叫车
		case R.id.dialog_frist_cancel:
			isGetConversation = false;
			
			if (lastConversation != null) {
				String id = String.valueOf(ConversationID);
				System.out.print("取消的ID----------------------->"+ ConversationID);
				changeConversationsStatus("-1", id);
			}else{
				Log.e("时间到取消叫车","CANCLE_ANSWER");
			}
			
			showNorm();
			break;
		// dialog倒计时时间到_继续叫车
		case R.id.dialog_frist_contiune:
			isGetConversation = false;
			timeOutDialog.setVisibility(View.GONE);
			getDrivers();
			sendTrips();
			break;
		// dialog确定取消叫车
		case R.id.dialog_two_cancel:
			isGetConversation = false;// 不能再发向服务器端发送会话
			if (lastConversation != null) {
				String id = String.valueOf(lastConversation.getId());
				changeConversationsStatus("-1", id);
			}
			showDialog2();
			break;
		// dialog继续叫车
		case R.id.dialog_two_contiune:
			dialog1.setVisibility(View.GONE);
			// 判断是在calling是取消的还是在answer是取消的
			if (judgeCancle == CANCLE_CALLING) {
				// getDrivers();
				// sendTrips();
				// judgeCancle=0;
			} else if (judgeCancle == CANCLE_ANSWER) {

				getDrivers();
				sendTrips();
				// judgeCancle=0;
			} else {

			}
			break;
		// 取消叫车后返回call（主界面）
		case R.id.dialog_three_1:
			showNorm();
			break;
		case R.id.dialog_three_2:
			showNorm();
			break;
		case R.id.dialog_three_3:
			showNorm();
			break;
		case R.id.dialog_three_4:
			showNorm();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// System.out.println("返回的requestCode------------------------------->"+requestCode);
		switch (requestCode) {

		case SEND:
			if (data != null) {
				if (data.hasExtra("premium")) {
					premium = data.getIntExtra("premium", 0);
					System.out.println("premium------>" + premium);
				}
				if (data.hasExtra("start")) {
					start = data.getStringExtra("start");
					System.out.println("start------>" + start);
				} else {
					start = address;
				}
				if (data.hasExtra("end")) {
					end = data.getStringExtra("end");
					// getGeo(end);
					System.out.println("end------>" + end);
				}

				if (start == null || start.equals("")) {

//					Toast.makeText(context, "无法获取当前位置，请手动输入", Toast.LENGTH_LONG).show();
					MyToast toast = new MyToast(context,"无法获取当前位置，请手动输入");
					toast.startMyToast();
				} else {
					// 发布路线
					// getDrivers();
					sendTrips();
				}
			}
			break;
		case CANCLE_CALLING:
			// 重新发布线路

			sendTrips();
			break;

		case TIMEOUT:

			waitingTime = WAITING;
			// isWaiting = true;
			// isStartCount = true;

			getDrivers();
			sendTrips();
			break;

		case CANCLE_ANSWER:

			String id = String.valueOf(lastConversation.getId());
			changeConversationsStatus("1", id);
			break;

		default:
			break;
		}

	}

	/**
	 * 获取我附近得司机
	 */
	private void getDrivers() {

		if (HttpTools.checkNetWork(context)) {
			// getDrivers标示是否可以得到司机
			new Thread(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {

					try {

						Map<String, String> map = new HashMap<String, String>();
						map.put("driver[lat]", String.valueOf(lat));
						map.put("driver[lng]", String.valueOf(lng));
						map.put("driver[androidDevice]", androidDevice);
						Log.e("位置", lat+"-"+lng);
						driversList = (List<DriversInfo>) HttpTools
								.getAndParse(Constant.DRIVERS, map,
										new DriversHandler());

						if (driversList != null && driversList.size() > 0) {

							System.out.println("司机数量------>"
									+ driversList.size());
							messageHandler.sendEmptyMessage(Constant.SUCCESS);

						} else {

							messageHandler.sendEmptyMessage(Constant.FAILURE);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		}
	}

	/**
	 * 获取我附近得司机详情
	 */
	private void getDriverInfo(final int id) {
		if (HttpTools.checkNetWork(context)) {
			new Thread(new Runnable() {
				public void run() {

					try {
						// 通过司机的id来获取司机的信息
						String result = (String) HttpTools.getAndParse(context,
								Constant.DRIVER_INFO + String.valueOf(id),
								null, new BaseHandler());
						System.out
								.println("获得我附近的司机信息这个是在conversation中的---------------->"
										+ result);

						JSONObject jsonObject = new JSONObject(result);
						JSONObject object = jsonObject.getJSONObject("driver");

						acceptInfo = new Drivers(object);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (acceptInfo != null) {
						acceptHandler.sendEmptyMessage(Constant.SUCCESS);
					}

				}
			}).start();
		}
	}

	TripsInfo tripsInfo;
	private String name;
	private String password;

	/**
	 * 发布路线
	 */
	private void sendTrips() {
		if (HttpTools.checkNetWork(context)) {

			new Thread(new Runnable() {
				public void run() {
					try {

						GeoPoint p = getGeo(end);
						double tripLat = p.getLatitudeE6() / (1e6);
						double tripLng = p.getLongitudeE6() / (1e6);
						System.out
								.println("the tripLat of the end ------------> "
										+ tripLat);
						System.out
								.println("the tripLng of the end ------------> "
										+ tripLng);
						Map<String, String> map = new HashMap<String, String>();
						map.put("trip[passenger_id]", String.valueOf(psID));
						map.put("trip[start]", start);
						map.put("trip[start_lat]", String.valueOf(lat));
						map.put("trip[start_lng]", String.valueOf(lng));
						map.put("trip[end]", end);
						map.put("trip[price]", String.valueOf(premium));
						// map.put("trip[appointment]",
						// String.valueOf(premium));//是预约叫车的接口，现在不用
						map.put("trip[end_lat]", String.valueOf(tripLat));
						map.put("trip[end_lng]", String.valueOf(tripLng));

						// 这里返回有passenger_id和trips_id
						tripsInfo = (TripsInfo) HttpTools.postAndParse(
								Constant.TRIPS, map, new TripsHandler());

						if (tripsInfo != null) {
							// 表示发送成功 可以接收会话通知（isGetConversation用来控制接收会话的循环条件）
							isGetConversation = true;
							getConversations();
							callingHandler.sendEmptyMessage(Constant.SUCCESS);

						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}
			}).start();

		}
	}

	/**
	 * 我的会话（发送给我的请求）
	 */
	private void getConversations() {
		if (HttpTools.checkNetWork(context)) {
			new Thread(new Runnable() {
				public void run() {
					try {
						Map<String, String> map = new HashMap<String, String>();
						map.put("from_id", String.valueOf(psID));// passerget的id，

						
						lastConversation = (ConversationInfo) HttpTools
								.getAndParse(context, Constant.CONVERSATIONS,
										map, new ConversationsHandler());

						
						if (lastConversation != null) {

							int to_id = lastConversation.getTo_id();
							int Status = lastConversation.getStatus();
							System.out.println("最最最里面--------------------->"
									+ lastConversation.getId());
							System.out.println("最最最里面面--------------------->"
									+ lastConversation.getStatus());
					

							if (Status == 1) {// 司机应答

								isAccept = true;
								isGetConversation = false;
								ConversationID = lastConversation.getId();
								System.out
										.println("ConversationID=------------------------"
												+ ConversationID);
								getDriverInfo(to_id);

							} else if (Status == 2) {// 司机拒绝

								isAccept = false;
								isGetConversation = true;
								acceptHandler
										.sendEmptyMessage(Constant.SUCCESS);

							} else if (Status == 4) {// 司机已经满员

								state = NORM;
								fullHandler.sendEmptyMessage(Constant.SUCCESS);
							} else if (Status == 0) { // 新呼叫的
								// callingHandler.sendEmptyMessage(Constant.SUCCESS);
							}
						}

						// 标示一秒接收一个会话消息
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}).start();
		}
	}

	/**
	 * 更新会话状态
	 */
	private void changeConversationsStatus(final String status, final String id) {
		if (HttpTools.checkNetWork(context)) {
			new Thread(new Runnable() {
				public void run() {

					try {
						Map<String, String> map = new HashMap<String, String>();

						String status_desc = "";
						if (status.equals("-1")) {

							status_desc = "giveup";
						}
						if (status.equals("1")) {

							status_desc = "accept";
						}
						if (status.equals("2")) {

							status_desc = "reject";
						}
						if (status.equals("3")) {

							status_desc = "finish";
						}
						map.put("conversation[status]", status);
						map.put("conversation[status_desc]", status_desc);

//						String result = (String) HttpTools.postAndParse(
//								Constant.CONVERSATIONS + id + "/", map,
//								new BaseHandler());

//						// if (result != null) {
//						//
//						// normHandler.sendEmptyMessage(Constant.SUCCESS);
//						// }
						
						//会话状态更新后，刷新会话

//						ConversationInfo conversation = (ConversationInfo)HttpTools.postAndParse(Constant.CONVERSATIONS + id + "/", map,new BaseHandler());				
//						if(conversation != null){
//							lastConversation.setStatus(status);
//						}
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}).start();
		}
	}

	/**
	 * 退出
	 */
	private void signOut() {

		if (HttpTools.checkNetWork(context)) {

			new Thread(new Runnable() {

				public void run() {

					// 不为空

					Map<String, String> map = new HashMap<String, String>();
					map.put("passenger[androidDevice]", androidDevice);

					try {
						String resultString = (String) HttpTools.getAndParse(
								Constant.SIGNOUT, map, new BaseHandler());
						JSONObject jsonObject = new JSONObject(resultString);
						if (jsonObject.has("message")) {
							System.out.println("message--->"
									+ jsonObject.getString("message"));

//							//退出登录后要清楚缓存
//							SharedPreferences share = getSharedPreferences("data", 0);
//							Editor editor = share.edit();
//							editor.clear();
//							editor.commit();
							
							exitPro(context);
							finish();

							return;
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					exitPro(context);
				}

			}).start();
		}

	}

	/**
	 * 正常状态
	 */
	private void showNorm() {

		callingTitle.setVisibility(View.VISIBLE);
		waitingTitle.setVisibility(View.GONE);
		answerTitle.setVisibility(View.GONE);

		callingBottom.setVisibility(View.VISIBLE);
		waitingBottom.setVisibility(View.GONE);
		answerBottom.setVisibility(View.GONE);

		timeOutDialog.setVisibility(View.GONE);
		dialog1.setVisibility(View.GONE);
		dialog2.setVisibility(View.GONE);

		isWaiting = false;
		waitingTime = WAITING;
		isGetConversation = false;

		mapController.animateTo(new GeoPoint((int) (lat * 1e6),
				(int) (lng * 1e6)), null);
		getDrivers();

	}

	/**
	 * 等待状态
	 */
	private void showCalling() {

		callingTitle.setVisibility(View.GONE);
		waitingTitle.setVisibility(View.VISIBLE);
		answerTitle.setVisibility(View.GONE);
		callingBottom.setVisibility(View.GONE);
		waitingBottom.setVisibility(View.VISIBLE);
		answerBottom.setVisibility(View.GONE);

		mapController.animateTo(new GeoPoint((int) (lat * 1e6),
				(int) (lng * 1e6)), null);

		if (driversList != null) {
			cab_count.setText("共有" + String.valueOf(driversList.size())
					+ "辆出租车车接收到消息");
		}
		isWaiting = true;
		isStartCount = true;
		waitingTime = WAITING;
		countBackwards();
		isStartCount = false; // 倒数只启动一次

	}

	/**
	 * 应答后的状态
	 */
	private void showCalled(Drivers info) {

		// 让waitingTitle的progressBar上面的文字不要在变化,防止出现乱显示dialog的情况
		isWaiting = false;

		callingTitle.setVisibility(View.GONE);
		waitingTitle.setVisibility(View.GONE);
		answerTitle.setVisibility(View.VISIBLE);
		callingBottom.setVisibility(View.GONE);
		waitingBottom.setVisibility(View.GONE);
		answerBottom.setVisibility(View.VISIBLE);

		answerTitleName.setText("司机：" + info.getName() + "已应答");
		answerTitleInfo.setText(info.getCar_license());

		displapDriversForAnswer(info);

	}

//	/**
//	 * 初始化gps
//	 * 
//	 * @return
//	 */
//	private boolean initGPS() {
//		LocationManager locationManager = (LocationManager) this
//				.getSystemService(Context.LOCATION_SERVICE);
//
//		// 判断GPS模块是否开启，如果没有则开启
//		if (!locationManager
//				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//			Toast.makeText(this, "GPS is not open,Please open it!",
//					Toast.LENGTH_SHORT).show();
//			// Intent intent = new
//			// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			// startActivityForResult(intent, 0);
//
//			return false;
//		} else {
//			Toast.makeText(this, "GPS is ready", Toast.LENGTH_SHORT);
//		}
//		return true;
//	}

//	/**
//	 * 初始化所在GPS位置
//	 */
//	private void initLocation() {
//
//		LocationManager locationManager;
//		String serviceName = Context.LOCATION_SERVICE;
//		locationManager = (LocationManager) this.getSystemService(serviceName);
//		// 查询条件
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//
//		String provider = locationManager.getBestProvider(criteria, true);
//		Location location = locationManager.getLastKnownLocation(provider);
//		if (location != null) {
//
//			lat = (int) location.getLatitude();
//			lng = (int) location.getLongitude();
//
//			pt = new GeoPoint((int) (location.getLatitude() * 1e6),
//					(int) (location.getLongitude() * 1e6));
//
//			mMapView.getController().animateTo(pt);
//			// mapController.setCenter(pt);
//
//			// 查询该经纬度值所对应的地址位置信息
//			// mMKSearch.reverseGeocode(point);
//
//			// mMKSearch.reverseGeocode(new GeoPoint((int) (lat * 1e6),
//			// (int) (lng * 1e6)));
//			// getAddress(lat, lng);
//		}
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

//			exitDialog(context);
			//点击返回键，最小化程序，进入home页面
			Intent intents = new Intent(Intent.ACTION_MAIN);
			intents.addCategory(Intent.CATEGORY_HOME);
			intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intents);
		}
		return super.onKeyDown(keyCode, event);
	}

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
	 * 得到用户
	 */
	private void getData() {
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		name = sharedata.getString("name", "");
		password = sharedata.getString("password", "");

	}

	/**
	 * 满员
	 */
	Handler fullHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:
//				Tools.myToast(context, "司机已满员！");
				MyToast toast = new MyToast(context,"司机已满员！");
				toast.startMyToast();
				showNorm();
				break;
			case Constant.FAILURE:

				break;

			}
		}
	};

	Handler normHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:
				showNorm();
				isListern = false;

				break;

			}
		}
	};
	/**
	 * 获取司机成功
	 */
	Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:

				displayDrivers(true);
				title.setText("5公里范围，" + String.valueOf(driversList.size())
						+ "辆出租车");
//				title.setTextSize(20);

				break;
			case Constant.FAILURE:
//				Tools.myToast(context, "周围没有司机哦！");
				MyToast toast = new MyToast(context,"周围没有司机哦！");
				toast.startMyToast();
				break;

			}
		}
	};
	// 发布路线成功
	Handler callingHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:
				showCalling();
				break;
			case Constant.FAILURE:

				break;

			}
		}
	};
	/**
	 * 司机应答成功
	 */
	Handler acceptHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:

				System.out.println("acceptInfo.getName()---"
						+ acceptInfo.getName());
				if (isAccept) {

					showCalled(acceptInfo);
					isListern = true;
//					listernConversation();

				} else {
					isGetConversation = true;// 继续向服务器发送消息
				}

				break;
			case Constant.FAILURE:

				break;

			}
		}
	};

	Handler waitingHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case WAITING:
				if (waitingTime >= 0) {
					waittingTimeEdit.setText("" + waitingTime);
					break;
				} else if (waitingTime == -1) {

					isWaiting = false;
					isGetConversation = false;// 不能在向服务器发送会话
					if (lastConversation != null) {
						// String id = String.valueOf(lastConversation.getId());
						String id = String.valueOf(ConversationID);
						Log.e("时间到取消的ID----------------------->","="+ConversationID);
						changeConversationsStatus("-1", id);
					}
					showTimeOutDialog();

					// Intent i = new Intent(context, DialogFrist.class);
					// startActivityForResult(i, TIMEOUT);

					break;
				} else {

				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, "登出");
		menu.add(0, 2, 2, "返回");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			save("", "");
			signOut();
		}
		return true;
	}

	/**
	 * 退出应用
	 * 
	 * @param context
	 */
	public void exitPro(Context context) {

		// 杀死Application
		String packName = context.getPackageName();
		ActivityManager activityMgr = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityMgr.restartPackage(packName);
		activityMgr.killBackgroundProcesses(packName);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void exitDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		View aalayout = View.inflate(context, R.layout.exit, null);

		builder.setView(aalayout);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (info != null) {

					signOut();
				} else {

					exitPro(context);
				}
			}
		});

		builder.create().show();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {

		// TODO Auto-generated method stub
		System.out.println("---------------------------->調用onReceiveLocation");
		if (HttpTools.checkNetWork(context)) {

			lat = location.getLatitude();
			lng = location.getLongitude();
			
			System.out.println("onReceiveLocation的lat---------------->" + lat);
			System.out.println("onReceiveLocation的lng---------------->" + lng);
			pt = new GeoPoint((int) (lat * 1e6), (int) (lng * 1e6));

			// 通过经纬度用来获取现在位置的名称
			MKSearch search = new MKSearch();
			search.init(mBMapMan, new MyMKSearchListener());
			// 根据地理坐标点获取地址信息 异步函数，返回结果在MKSearchListener里的onGetAddrResult方法通知
			search.reverseGeocode(pt);

			OverlayItem item = new OverlayItem(new GeoPoint((int) (lat * 1e6),
					(int) (lng * 1e6)), "", "");

			Drawable maker = getResources().getDrawable(R.drawable.person);
			item.setMarker(maker);
			mGeoList.add(item); // List<OverlayItem> mGeoList 是存放overlayitem

			MyItemizedOverlay overlay = new MyItemizedOverlay(context, maker,
					mGeoList,true);

			overlay.onTap(pt, mMapView);//设置点击事件
			
			List<Overlay> list = mMapView.getOverlays();
			list.clear();

			mMapView.getOverlays().add(0, overlay);
			mMapView.refresh();
			mapController.animateTo(new GeoPoint((int) (lat * 1e6),
					(int) (lng * 1e6)), null);
			
			
			// 得到司机的信息
			getDrivers();
		}
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 倒數的時間
	 * 
	 * @param start
	 *            simsunny
	 */
	private void countBackwards() {

		// isWaiting = true;

		if (isStartCount) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (isWaiting) {

						try {
							Thread.currentThread().sleep(1000);
							Message msg = new Message();
							waitingTime--;
							msg.what = WAITING;
							waitingHandler.sendMessage(msg);

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}).start();

		}

	}

//	private void listernConversation() {
//		if (HttpTools.checkNetWork(context)) {
//			new Thread(new Runnable() {
//				public void run() {
//					while (isListern) {
//
//						lastConversation = (ConversationInfo) HttpTools
//								.getAndParse(Constant.CONVERSATIONS,
//										ConversationID,
//										new ConversationHandler());
//						try {
//							Thread.currentThread().sleep(1000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						if (lastConversation != null) {
//
//							int Status = lastConversation.getStatus();
//
//							if (Status == 4) {// 司机应答
//								normHandler.sendEmptyMessage(Constant.SUCCESS);
//
//							} else {
//
//							}
//						}
//					}
//				}
//			}).start();
//		}
//	}

	private void displapDriversForAnswer(Drivers info) {

		Drawable marker = getResources().getDrawable(R.drawable.cear);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		mGeoList.clear();

		OverlayItem item1 = new OverlayItem(new GeoPoint((int) (lat * 1e6),
				(int) (lng * 1e6)), "", "");

		Drawable maker1 = getResources().getDrawable(R.drawable.person);
		item1.setMarker(maker1);
		mGeoList.add(item1);

		int size = driversList.size();
		OverlayItem item = null;
		double driverLat, driverLng;
		DriversInfo drivers;
		Drawable maker = getResources().getDrawable(R.drawable.cear);

		driverLat = Double.parseDouble(info.getLat());
		driverLng = Double.parseDouble(info.getLng());
		item = new OverlayItem(new GeoPoint((int) (driverLat * 1e6),
				(int) (driverLng * 1e6)), info.getName(), " ");
		item.setMarker(maker);
		mGeoList.add(item);

		for (int i = 0; i < mGeoList.size(); i++) {
			System.out.println("应答后mGeoList---------->"
					+ mGeoList.get(i).getTitle());
		}

		MyItemizedOverlay overlay = new MyItemizedOverlay(context, maker,
				mGeoList, info);

		mMapView.getOverlays().clear();// 只是清理的图层。没有买mGeoList的什么事
		mMapView.getOverlays().add(0, overlay);
		mMapView.refresh();

	}

	/**
	 * 显示周围司机
	 * 
	 * @param isDisplay
	 */
	private void displayDrivers(boolean isDisplay) {

		if (isDisplay) {

			Drawable marker = getResources().getDrawable(R.drawable.cear);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					marker.getIntrinsicHeight());

			for (int i = 0; i < mGeoList.size(); i++) {
				System.out.println("前面的mGeoList---------->"
						+ mGeoList.get(i).getTitle());
			}

			// for (int i = 1; i < mGeoList.size(); i++) {
			// mGeoList.remove(i);
			// }
			mGeoList.clear();
			OverlayItem item1 = new OverlayItem(new GeoPoint((int) (lat * 1e6),
					(int) (lng * 1e6)), "", "");

			Drawable maker1 = getResources().getDrawable(R.drawable.person);
			item1.setMarker(maker1);
			mGeoList.add(item1);
			for (int i = 0; i < mGeoList.size(); i++) {
				System.out.println("中间的的mGeoList---------->"
						+ mGeoList.get(i).getTitle());
			}

			int size = driversList.size();
			OverlayItem item = null;
			double driverLat, driverLng;
			DriversInfo drivers;
			Drawable maker = getResources().getDrawable(R.drawable.cear);

			for (int i = 0; i < size; i++) {
				drivers = driversList.get(i);
				driverLat = drivers.getLat();
				driverLng = drivers.getLng();

				item = new OverlayItem(new GeoPoint((int) (driverLat * 1e6),
						(int) (driverLng * 1e6)), drivers.getName(), " ");
				item.setMarker(maker);
				mGeoList.add(item);
			}

			for (int i = 0; i < mGeoList.size(); i++) {
				System.out.println("后面的mGeoList---------->"
						+ mGeoList.get(i).getTitle());
			}

			MyItemizedOverlay overlay = new MyItemizedOverlay(context, maker,
					mGeoList, driversList);

			mMapView.getOverlays().clear();// 只是清理的图层。没有买mGeoList的什么事
			for (int i = 0; i < mGeoList.size(); i++) {
				System.out.println("cleae后的mGeoList---------->"
						+ mGeoList.get(i).getTitle());
			}
			mMapView.getOverlays().add(0, overlay);
			for (int i = 0; i < mGeoList.size(); i++) {
				System.out.println("add后的mGeoList---------->"
						+ mGeoList.get(i).getTitle());
			}
			mMapView.refresh();
		}
	}

	/**
	 * 显示TimeOutDialog simsunny
	 */
	private void showTimeOutDialog() {
		timeOutDialog.setVisibility(View.VISIBLE);
		dialog1.setVisibility(View.GONE);
		dialog2.setVisibility(View.GONE);
	}

	/**
	 * 显示Dialog1 simsunny
	 */
	private void showDialog1() {
		timeOutDialog.setVisibility(View.GONE);
		dialog1.setVisibility(View.VISIBLE);
		dialog2.setVisibility(View.GONE);
	}

	/**
	 * 显示dialog2 simsunny
	 */
	private void showDialog2() {
		timeOutDialog.setVisibility(View.GONE);
		dialog1.setVisibility(View.GONE);
		dialog2.setVisibility(View.VISIBLE);
	}

	private class PhoneCallListener extends PhoneStateListener {
		private boolean bphonecalling = false;

		@Override
		public void onCallStateChanged(int state, String incomingnumber) {
			// seems the incoming number is this call back always ""
			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				bphonecalling = true;
			} else if (TelephonyManager.CALL_STATE_IDLE == state
					&& bphonecalling) {

				bphonecalling = false;

				Intent intent = new Intent();

				intent.setClass(LocationOverlay.this, LocationOverlay.class);
				//
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// intent.setFlags( Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(intent);

				// startActivity(intent);
			}
			super.onCallStateChanged(state, incomingnumber);
		}
	}


	/**
	 * 推送广播接收器
	 */
	private void initMyBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.jpush.android.intent.REGISTRATION");// 用户注册SDK的intent
		filter.addAction("cn.jpush.android.intent.UNREGISTRATION");
		filter.addAction("cn.jpush.android.intent.MESSAGE_RECEIVED");// 用户接收SDK消息的intent
		filter.addAction("cn.jpush.android.intent.NOTIFICATION_RECEIVED");// 用户接收SDK通知栏信息的intent
		filter.addAction("cn.jpush.android.intent.NOTIFICATION_OPENED");// 用户打开自定义通知栏的intent
		filter.addCategory("com.findcab");
		getApplicationContext().registerReceiver(MyReceiver, filter); // 注册
	}

	/**
	 * 自定义广播,动态注册
	 */
	private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
		private static final String TAG = "MyReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			
			if (bundle.containsKey("cn.jpush.android.ALERT")) {
				Log.e(
						TAG,
						"接收到推送下来的自定义消息: "
						+ bundle.getString("cn.jpush.android.ALERT"));

				// 判断收到消息内容
				refreshViewByJPushInfo(bundle
						.getString("cn.jpush.android.ALERT"));
			}
		}

		// 打印所有的 intent extra 数据
		private String printBundle(Bundle bundle) {
			StringBuilder sb = new StringBuilder();
			for (String key : bundle.keySet()) {
				if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
				} else {
					sb.append("\nkey:" + key + ", value:"
							+ bundle.getString(key));
				}
			}
			return sb.toString();
		}
	};

	// 通过推送消息内容判断刷新内容，并通知handler操作;1,conversation_change 2,drivers_change
	// 3,passagers_change
	private void refreshViewByJPushInfo(String text) {
		if (text.equals("conversations")) {
			handlerMain.sendEmptyMessage(MESSAGE_CONVERSATIONS_CHANGE);
		} else if (text.equals("drivers")) {
			handlerMain.sendEmptyMessage(MESSAGE_DRIVERS_CHANGE);
		} else if (text.equals("passagers")) {
			handlerMain.sendEmptyMessage(MESSAGE_PASSAGERS_CHANGE);
		} else {

		}
	}

}
