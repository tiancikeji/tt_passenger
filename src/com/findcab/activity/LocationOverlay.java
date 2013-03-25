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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.findcab.object.ConversationInfo;
import com.findcab.object.Drivers;
import com.findcab.object.DriversInfo;
import com.findcab.object.Passengers;
import com.findcab.object.TripsInfo;
import com.findcab.util.Constant;
import com.findcab.util.HttpTools;
import com.findcab.util.MD5;
import com.findcab.util.MapUtility;
import com.findcab.util.MyItemizedOverlay;
import com.findcab.util.Tools;
import com.iflytek.mscdemo.IatActivity;

public class LocationOverlay extends Activity implements OnClickListener,
		BDLocationListener {

	public static MapView mMapView = null;
	public static View mPopView = null; // 点击mark时弹出的气泡View
	
	LocationListener mLocationListener = null;
	// BaiLocationOverlay mLocationOverlay = null;
	public Context context;
	String mStrKey = "8BB7F0E5C9C77BD6B9B655DB928B74B6E2D838FD";
	public static BMapManager mBMapMan = null;
	List<GeoPoint> pList;
	int iZoom = 0;
	MapController mapController;
	private Button locate;
	private Button call;
	private GeoPoint pt = null;

	private double lat;
	private double lng;
	/**
	 * 司机列表
	 */
	private List<DriversInfo> driversList;
	private Drivers acceptInfo;
	private Passengers info;

	private TextView title;

	public static String address;
	private String end;
	private String start;
	private ProgressBar progressBar;

	private boolean isCall;
	private boolean isRun = true;
	private boolean isAccept = true;

	/**
	 * // 最后的会话
	 */
	ConversationInfo lastConversation = null;
	private String androidDevice;
	private Button called_finish;

	public static final int LAND = 1;// 登陆
	public static final int SEND = 2;// 发送路线

	private RelativeLayout search_title;
	private LinearLayout head_icon;
	private TextView cab_count;
	private TextView car_license;
	private Button call_button;
	private LinearLayout line;
	private LinearLayout distanceAndTime;
	private Button cancel;
	private LinearLayout answers_button;
	private RelativeLayout calling;
	private Button called_cancel;
	private int state;
	public static final int NORM = 1;// 正常状态
	public static final int CALLING = 2;// 請求中狀態
	public static final int ANSER = 3;// 請求中狀態

	ProgressDialog pd;
	// 存放overlayitem
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private LocationClient mLocClient;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.mapview);
		Tools.init();
		iniView();
		initGPS();

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
	 * 得到初始化数据   从landActivity 中startMainActivity()方法得到数据
	 * 现在可以先忽略 simsunny
	 */
	private void getInitData() {

		Intent data = getIntent();
		if (data != null) {
			if (data.hasExtra("name")) {
				name = data.getStringExtra("name");
			}
			if (data.hasExtra("password")) {
				password = data.getStringExtra("password");
			}
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				info = (Passengers) bundle.getSerializable("Passengers");

			}
		}
	}

	/**
	 * 地址转换为经纬度
	 * 
	 * @param locationName
	 *            地址
	 */
	private void getGeo(String locationName) {
		JSONObject object = MapUtility.getLocationInfo(locationName);
		GeoPoint point = MapUtility.getGeoPoint(object);
		mMapView.getController().setCenter(point);
	}

	/**
	 * 经纬度转地址
	 * 
	 * @param lat
	 * @param lng
	 */
	private void getAddress(double lat, double lng) {

		address = MapUtility.getAddressByLatLng(lat, lng);

		// address = MapUtility.getAddress(location);

		if (address != null) {
			if (address.startsWith("中国")) {

				address = address.substring(2);

			}
			if (address.startsWith(" ")) {

				address = address.substring(0, address.indexOf(" "));

			}

		}

		System.out.println("address------>" + address);

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
		
//		更变ui simsunny
//		locate = (Button) findViewById(R.id.locate);
//		locate.setOnClickListener(this);
//		call = (Button) findViewById(R.id.call);
//		call.setOnClickListener(this);
//
//		title = (TextView) findViewById(R.id.title);
		
		//旧的ui 觉得是第二个 LinearLayout的 tilte progressbar  先注释掉 simsunny
		//progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		//初始化第一个界面 也就是LinearLayout_1  simsunny
		//LinearLayout_1 title 的 布局
		mMapView = (MapView) findViewById(R.id.bmapView);
		locate=(Button)findViewById(R.id.map_calling_title_btn_fresh);
		locate.setOnClickListener(this);
		title=(TextView)findViewById(R.id.map_calling_title_txt);
		//LinearLayout_1 bottom 的 btn
		call =(Button)findViewById(R.id.map_calling_bottom_btn_call);
		call.setOnClickListener(this);
		/**
		 * 得到初始化数据   从landActivity 中startMainActivity()方法得到数据
		 * 现在可以先忽略 simsunny
		 */
		//getInitData();
		
		//旧的ui 先注释掉 simsunny
//		search_title = (RelativeLayout) findViewById(R.id.search_title);
//		head_icon = (LinearLayout) findViewById(R.id.head_icon);
//		cab_count = (TextView) findViewById(R.id.cab_count);
//		car_license = (TextView) findViewById(R.id.car_license);
//		call_button = (Button) findViewById(R.id.call_button);
//		line = (LinearLayout) findViewById(R.id.line);
//		distanceAndTime = (LinearLayout) findViewById(R.id.distanceAndTime);
//		cancel = (Button) findViewById(R.id.cancel);
//		answers_button = (LinearLayout) findViewById(R.id.answers_button);
//		calling = (RelativeLayout) findViewById(R.id.calling);
//		called_cancel = (Button) findViewById(R.id.called_cancel);
//		called_finish = (Button) findViewById(R.id.called_finish);
//
//		call_button.setOnClickListener(this);
//		cancel.setOnClickListener(this);
//		called_cancel.setOnClickListener(this);
//		called_finish.setOnClickListener(this);

		//mMapView = (MapView) findViewById(R.id.bmapView);
		androidDevice = Tools.getDeviceId(context);
		mPopView = super.getLayoutInflater().inflate(R.layout.popview_title,
				null);

		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);
		
		//返回当前地图的缩放级别  
		//return int 
		iZoom = mMapView.getZoomLevel();
		//检查mapview是否支持双击放大效果
		//return boolean
		mMapView.setDoubleClickZooming(false);
		
		mMapView.setClickable(false);
		//返回地图的MapController，这个对象可用于控制和驱动平移和缩放
		mapController = mMapView.getController();
		//设置地图的缩放级别。 这个值的取值范围是[3,18]。
		mapController.setZoom(14);
		
		mLocClient = new LocationClient(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		mLocClient.setLocOption(option);
		mLocClient.registerLocationListener(this);
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
		isRun = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		// mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		// mLocationOverlay.enableMyLocation();
		// mLocationOverlay.enableCompass();
		mBMapMan.start();
		isRun = true;
		super.onResume();
	}

	// class OverItemT extends ItemizedOverlay<OverlayItem> {
	//
	// public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	// private Drawable marker;
	//
	// public OverItemT(Drawable marker, Context context,
	// List<DriversInfo> list) {
	//
	// super(boundCenterBottom(marker));
	// this.marker = marker;
	//
	// DriversInfo info;
	// GeoPoint point = null;
	// for (int i = 0; i < driversList.size(); i++) {
	// info = driversList.get(i);
	// point = new GeoPoint((int) (info.getLat() * 1e6), (int) (info
	// .getLng() * 1e6));
	// if (info.getStatus() == 1) {
	//
	// mGeoList.add(new OverlayItem(point, "乘客已满", ""));
	// } else if (info.getStatus() == 0) {
	//
	// mGeoList.add(new OverlayItem(point, "", ""));
	// }
	// System.out.println("name--->" + info.getName());
	// }
	//
	// populate();
	// }
	//
	// public OverItemT(Drawable marker, Context context) {
	// super(boundCenterBottom(marker));
	//
	// this.marker = marker;
	//
	// populate();
	// }
	//
	// public void updateOverlay() {
	// populate();
	// }
	//
	// @Override
	// public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	//
	// // Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
	// Projection projection = mapView.getProjection();
	// for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
	// OverlayItem overLayItem = getItem(index); // 得到给定索引的item
	//
	// String title = overLayItem.getTitle();
	// // 把经纬度变换到相对于MapView左上角的屏幕像素坐标
	// Point point = projection.toPixels(overLayItem.getPoint(), null);
	//
	// // 可在此处添加您的绘制代码
	// Paint paintText = new Paint();
	// paintText.setColor(Color.RED);
	// paintText.setTextSize(18);
	// canvas.drawText(title, point.x - 30, point.y, paintText); // 绘制文本
	// }
	//
	// super.draw(canvas, mapView, shadow);
	// // 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
	// boundCenterBottom(marker);
	// }
	//
	// @Override
	// protected OverlayItem createItem(int i) {
	// // TODO Auto-generated method stub
	// return mGeoList.get(i);
	// }
	//
	// @Override
	// public int size() {
	// // TODO Auto-generated method stub
	// return mGeoList.size();
	// }
	//
	// @Override
	// // 处理当点击事件
	// protected boolean onTap(int i) {
	// setFocus(mGeoList.get(i));
	// // 更新气泡位置,并使之显示
	// GeoPoint pt = mGeoList.get(i).getPoint();
	// LocationOverlay.mMapView.updateViewLayout(LocationOverlay.mPopView,
	// new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT, pt,
	// MapView.LayoutParams.BOTTOM_CENTER));
	// // LocationOverlay.mPopView.setVisibility(View.VISIBLE);
	// // TextView name = (TextView) mPopView.findViewById(R.id.text_name);
	// // name.setText(listInfo.get(i).getName());
	// if (driversList.get(i).getStatus() == 1) {
	//
	// Toast.makeText(context, "乘客已满", Toast.LENGTH_SHORT).show();
	// }
	// return true;
	// }
	//
	// @Override
	// public boolean onTap(GeoPoint arg0, MapView arg1) {
	// // TODO Auto-generated method stub
	// // 消去弹出的气泡
	// // LocationOverlay.mPopView.setVisibility(View.GONE);
	//
	// return super.onTap(arg0, arg1);
	// }
	// }

	/**
	 * 内部类实现MKSearchListener接口,用于实现异步搜索服务
	 * 
	 * @author liufeng
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.call_button:

			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ acceptInfo.getMobile()));
			startActivity(intent);

			break;
			//linearLayout1  的刷新按钮
		case R.id.map_calling_title_btn_fresh:

			if (pt != null) {
				mMapView.getController().animateTo(pt);
			} else {

				initLocation();
			}

			break;
			//linearLayout1  bottom 的叫车btn
		case R.id.map_calling_bottom_btn_call:
			Toast.makeText(this, "ok-------------------->test", 1000);
			Intent i = new Intent();
			i.setClass(context , CallActivity.class);
			//在onActivityResult的方法中接受到SEND的常量，表示从这个activity种跳到
			//另一个activity是，返回的值
			//startActivityForResult(i, SEND);
			startActivity(i);
			
//			if (!isCall) {
//
//				Intent intent1 = new Intent(context, IatActivity.class);
//				startActivityForResult(intent1, SEND);
//
//			}
			break;

		case R.id.cancel:
			isCall = false;
			isRun = false;
			state = NORM;

			if (lastConversation != null) {
				String id = String.valueOf(lastConversation.getId());
				changeConversationsStatus("-1", id);
			}

			break;
			//旧的ui 先注释掉 simsunny
//		case R.id.called_cancel:
//			isRun = false;
//			state = NORM;
//			showNorm();
//			changeConversationsStatus("-1", String.valueOf(lastConversation
//					.getId()));
//
//			break;
//		case R.id.called_finish:
//			isRun = false;
//			state = NORM;
//			changeConversationsStatus("3", String.valueOf(lastConversation
//					.getId()));
//
//			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SEND:
			if (data != null) {

				if (data.hasExtra("start")) {

					start = data.getStringExtra("start");
					System.out.println("start------>" + start);
				} else {
					start = address;
				}
				if (data.hasExtra("end")) {

					end = data.getStringExtra("end");
					getGeo(end);
					System.out.println("end------>" + end);
				}

				if (start == null || start.equals("")) {

					Toast
							.makeText(context, "无法获取当前位置，请手动输入",
									Toast.LENGTH_LONG).show();
				} else {
					// 发布路线
					count = 0;
					sendTrips();
					callingHandler.sendEmptyMessage(Constant.SUCCESS);
				}
			}
			break;
		case LAND:
			if (data != null) {
				if (data.hasExtra("name")) {
					name = data.getStringExtra("name");
				}
				if (data.hasExtra("password")) {
					password = data.getStringExtra("password");
				}
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					info = (Passengers) bundle.getSerializable("Passengers");

					System.out.println("LAND---------------->bundle != null");
					Intent intent = new Intent(context, IatActivity.class);
					startActivityForResult(intent, SEND);
				}
				save(name, password);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 登陆
	 */
	private void land(final String name, final String password) {

		if (HttpTools.checkNetWork(context)) {

			new Thread(new Runnable() {

				public void run() {
					try {

						// 不为空
						Map<String, String> map = new HashMap<String, String>();
						MD5 md5 = new MD5();
						map.put("passenger[mobile]", name);
						map.put("passenger[password]", md5
								.getMD5ofStr(password));

						System.out.println("landing");
						String resultString = (String) HttpTools.postAndParse(
								Constant.SIGNIN, map, new BaseHandler());

						System.out.println("land-------->" + resultString);
						JSONObject jsonObject;
						if (resultString == null) {
							exitPro(context);
							return;
						}
						try {
							jsonObject = new JSONObject(resultString);
							if (jsonObject.has("error")) {
								Intent i = new Intent();
								startActivityForResult(i, LAND);
								return;
							}

							if (jsonObject.has("passenger")) {

								info = new Passengers(jsonObject
										.getJSONObject("passenger"));
								// callingHandler.sendEmptyMessage(Constant.SUCCESS);
								// getConversations(); // /测试
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
	 * 获取我附近得司机
	 */
	private void getDrivers() {
		if (HttpTools.checkNetWork(context)) {

			new Thread(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {

					try {
						Map<String, String> map = new HashMap<String, String>();

						map.put("driver[lat]", String.valueOf(lat));
						map.put("driver[lng]", String.valueOf(lng));
						map.put("driver[androidDevice]", androidDevice);

						driversList = (List<DriversInfo>) HttpTools
								.getAndParse(Constant.DRIVERS, map,
										new DriversHandler());

						if (driversList != null && driversList.size() > 0) {
							messageHandler.sendEmptyMessage(Constant.SUCCESS);
						} else {

							messageHandler.sendEmptyMessage(Constant.FAILURE);
						}

					} catch (Exception e) {
						// TODO: handle exception
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
						String result = (String) HttpTools.getAndParse(context,
								Constant.DRIVER_INFO + String.valueOf(id),
								null, new BaseHandler());
						JSONObject jsonObject = new JSONObject(result);

						JSONObject object = jsonObject.getJSONObject("driver");

						acceptInfo = new Drivers(object);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (acceptInfo != null) {
						acceptHandler.sendEmptyMessage(Constant.SUCCESS);
						count++;
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

						Map<String, String> map = new HashMap<String, String>();
						map.put("trip[passenger_id]", String.valueOf(info
								.getId()));
						map.put("trip[start]", start);
						map.put("trip[start_lat]", String.valueOf(lat));
						map.put("trip[start_lng]", String.valueOf(lng));
						map.put("trip[end]", end);
						map.put("trip[appointment]", "10");
						map.put("trip[end_lat]", String
								.valueOf(39.876757965948));
						map.put("trip[end_lng]", String
								.valueOf(116.65188108138));

						tripsInfo = (TripsInfo) HttpTools.postAndParse(
								Constant.TRIPS, map, new TripsHandler());

						if (tripsInfo != null) {
							// isCall = true;
							isRun = true;
							state = CALLING;
							callingHandler.sendEmptyMessage(Constant.SUCCESS);
							getConversations();

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
					while (isRun) {

						try {

							Map<String, String> map = new HashMap<String, String>();

							map.put("from_id", String.valueOf(info.getId()));

							lastConversation = (ConversationInfo) HttpTools
									.getAndParse(context,
											Constant.CONVERSATIONS, map,
											new ConversationsHandler());

							if (lastConversation != null) {

								int to_id = lastConversation.getTo_id();
								int Status = lastConversation.getStatus();

								System.out
										.println("------lastConversation----Status----=="
												+ Status);
								if (Status == 1 && isRun) {// 司机应答
									isAccept = true;
									if (state != ANSER) {
										state = ANSER;
										getDriverInfo(to_id);
									}
								} else if (Status == 2) {// 司机拒绝

									isAccept = false;
									isRun = false;
									state = NORM;

									if (acceptInfo != null) {
										acceptHandler
												.sendEmptyMessage(Constant.SUCCESS);
									}
								} else if (Status == 4) {// 司机已经满员

									isRun = false;
									state = NORM;
									fullHandler
											.sendEmptyMessage(Constant.SUCCESS);
								} else if (Status == 0) { // 新呼叫的
									// callingHandler
									// .sendEmptyMessage(Constant.SUCCESS);
								}
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
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

						String result = (String) HttpTools.postAndParse(
								Constant.CONVERSATIONS + id + "/", map,
								new BaseHandler());

						if (status.equals("-1") || status.equals("3")) {
							isRun = false;
							if (status.equals("-1")) {
								getDrivers();
							}
						}

						if (result != null) {

							normHandler.sendEmptyMessage(Constant.SUCCESS);
						}

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
	 * 
	 */
	private void showCalling() {
		search_title.setVisibility(View.GONE);
		calling.setVisibility(View.VISIBLE);
		head_icon.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		if (driversList != null) {

			cab_count.setText("共有" + String.valueOf(driversList.size())
					+ "辆出租车车接收到消息");
		}
		call_button.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
		distanceAndTime.setVisibility(View.GONE);
		cancel.setVisibility(View.VISIBLE);
		call.setVisibility(View.GONE);
		answers_button.setVisibility(View.GONE);
	}

	/**
	 * 应答后view
	 */
	private void showCalled(Drivers info) {
		progressBar.setVisibility(View.GONE);
		cab_count.setText("司机：" + info.getName() + "已应答");
		car_license.setText(info.getCar_license());
		search_title.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		head_icon.setVisibility(View.VISIBLE);
		call_button.setVisibility(View.VISIBLE);
		line.setVisibility(View.VISIBLE);
		distanceAndTime.setVisibility(View.VISIBLE);
		calling.setVisibility(View.VISIBLE);
		cancel.setVisibility(View.GONE);
		call.setVisibility(View.GONE);
		answers_button.setVisibility(View.VISIBLE);
	}

	/**
	 * 正常状态
	 */
	private void showNorm() {

		isCall = false;
		search_title.setVisibility(View.VISIBLE);
		calling.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
		distanceAndTime.setVisibility(View.GONE);
		call.setVisibility(View.VISIBLE);
		cancel.setVisibility(View.GONE);
		answers_button.setVisibility(View.GONE);
		car_license.setText("请等待时间应答……");
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

			pt = new GeoPoint((int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			mMapView.getController().animateTo(pt);

			// 查询该经纬度值所对应的地址位置信息
			// mMKSearch.reverseGeocode(point);

			// mMKSearch.reverseGeocode(new GeoPoint((int) (lat * 1e6),
			// (int) (lng * 1e6)));
			// getAddress(lat, lng);
		}
	}

	private boolean initGPS() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// 判断GPS模块是否开启，如果没有则开启
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS is not open,Please open it!",
					Toast.LENGTH_SHORT).show();
			// Intent intent = new
			// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			// startActivityForResult(intent, 0);

			return false;
		} else {
			Toast.makeText(this, "GPS is ready", Toast.LENGTH_SHORT);
		}
		return true;
	}

	/**
	 * 满员
	 */
	Handler fullHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:
				Tools.myToast(context, "司机已满员！");
				showNorm();
				break;
			case Constant.FAILURE:

				break;

			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			exitDialog(context);
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

	Handler normHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:
				showNorm();

				break;

			}
		}
	};
	Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.SUCCESS:
				List<Overlay> list = mMapView.getOverlays();

				Drawable marker = getResources().getDrawable(R.drawable.cear);
				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
						.getIntrinsicHeight());

				list.clear();

				for (int i = 1; i < mGeoList.size(); i++) {

					mGeoList.remove(i);
				}

				int size = driversList.size();
				OverlayItem item = null;
				int lat,
				lng;
				DriversInfo drivers;
				Drawable maker = getResources().getDrawable(R.drawable.cear);
				for (int i = 0; i < size; i++) {
					drivers = driversList.get(i);

					lat = (int) (drivers.getLat() * 1e6);
					lng = (int) (drivers.getLng() * 1e6);
					item = new OverlayItem(new GeoPoint(lat, lng), drivers
							.getName(), "");
					item.setMarker(maker);

					mGeoList.add(item);
				}
				MyItemizedOverlay overlay = new MyItemizedOverlay(context,
						maker, mGeoList);

				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(overlay);

				mMapView.postInvalidate();
				title.setText("5公里范围，" + String.valueOf(driversList.size())
						+ "辆出租车");

				break;
			case Constant.FAILURE:
				Tools.myToast(context, "周围没有司机哦！");
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
	protected int count;
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

					if (count == 0) {

						Tools.myToast(context, "司机" + acceptInfo.getName()
								+ "已经应答");
					}

					showCalled(acceptInfo);

				} else {

					Tools
							.myToast(context, "司机" + acceptInfo.getName()
									+ "拒绝应答");
					isRun = false;
					showNorm();
				}

				break;
			case Constant.FAILURE:

				break;

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

		int lat = (int) (location.getLatitude() * 1e6);
		int lng = (int) (location.getLongitude() * 1e6);
		pt = new GeoPoint(lat, lng);
		OverlayItem item = new OverlayItem(new GeoPoint(lat, lng), "item1",
				"item1");
		Drawable maker = getResources().getDrawable(R.drawable.person);
		item.setMarker(maker);
		mGeoList.add(item);

		MyItemizedOverlay overlay = new MyItemizedOverlay(context, maker,
				mGeoList);

		List<Overlay> list = mMapView.getOverlays();
		if (list != null && list.size() > 0) {
			list.remove(0);
		}
		mMapView.getOverlays().add(0, overlay);

		mMapView.refresh();
		mapController.animateTo(new GeoPoint(lat, lng), null);

		MKSearch search = new MKSearch();
		search.init(mBMapMan, new MyMKSearchListener());
		search.reverseGeocode(pt);
		getAddress(lat, lng);
		getDrivers();
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub

	}
}
