package com.findcab.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.findcab.R;
import com.findcab.util.DBHelper;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PutDestinationActivity extends Activity  {
	
	private EditText putDestination;
	private Button btn,btnCancel;
	private MKSearch search;//
	private ArrayList<MKPoiInfo> pointInfoList =null;
	
	private ListView listView,DBListView;
	private boolean is=true;
	private List<Map<String ,String>> listMap = new ArrayList<Map<String ,String>>() ;
	private List<Map<String ,String>> dbListMap = new ArrayList<Map<String ,String>>() ;
	
	private RecognizerDialog iatDialog;
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	// 地图搜索关键字定义
	private  static final String ENGINE_POI = "poi";
	private DBHelper helper;//数据库
	
	private int startOrEnd;
	private final int START =12;
	private final int END = 22;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.put_destination);
		// 初始化缓存对象.
		mSharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
	
		initView();
		search = new MKSearch();
		initSearch();
	}
	
	/**
	 * 初始化view
	 */
	private void initView(){
		
		startOrEnd=getIntent().getIntExtra("putDestination",0);
		System.out.println("the startOrEnd is ----------------->"+startOrEnd);
		
		
		putDestination= (EditText)findViewById(R.id.put_destination_put_txt);
		putDestination.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				//以后要加接口的，替换掉北京的
				search.poiSearchInCity("北京", s.toString());
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		helper = new DBHelper(PutDestinationActivity.this);
		
		listView = (ListView) findViewById(R.id.put_destination_list);
		DBListView=(ListView)findViewById(R.id.put_destination_dblist);
		
		//先从数据库里面读数据
		initListFromDB();
		
		btn = (Button)findViewById(R.id.end_mic);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showIatDialog();
			}
		});
		
		btnCancel=(Button)findViewById(R.id.put_destination_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//FromDB();
				PutDestinationActivity.this.finish();
			}
		});
		
		
		iatDialog = new RecognizerDialog(this, "appid="+getString(R.string.app_id));
		iatDialog.setListener(new RecognizerDialogListener() {
			
			@Override
			public void onResults(ArrayList<RecognizerResult> results, boolean isLast) {
				// TODO Auto-generated method stub
				StringBuilder builder = new StringBuilder();
				for (RecognizerResult recognizerResult : results) {
					builder.append(recognizerResult.text);
				}

				if (builder.equals("")) {
					return;
				}

				if (builder.toString().endsWith("。")) {

					putDestination.append(builder.substring(0, builder.length() - 1));
				} else {
					putDestination.append(builder);

				}
				putDestination.setSelection(putDestination.length() - 1);
			}
			
			@Override
			public void onEnd(SpeechError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	/**
	 * 初始化联想词模块
	 * simsunny
	 */
	private void initSearch(){
		//MKSearch.init()方法要实现MKSearchListener()的接口
		//百度地图的API
		search.init(LocationOverlay.mBMapMan, new MKSearchListener() {

			public void onGetPoiResult(MKPoiResult res, int type, int error) {

				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(PutDestinationActivity.this,"抱歉，未找到结果", 1000);
					return;
				}
				
				//pointInfoList是一个private ArrayList<MKPoiInfo>列表
				//MKPoiInfo存放poi的信息（名字和地点）
				pointInfoList = res.getAllPoi();
				
				if (res.getCurrentNumPois() > 0) {
					initList();
				}
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int iError) {
				
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

		});
	}
	/**
	 * 初始化listview
	 * simsunny
	 */
	private void initList(){
		
		DBListView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		
		if(listMap.size()>0){
			listMap.clear();
		}
	
		if(pointInfoList!=null){
			
			for (int i = 0; i < pointInfoList.size(); i++) {
				
				MKPoiInfo info = pointInfoList.get(i);
				Map<String,String> map= new HashMap<String,String>();
				map.put("poiName", info.name);
				map.put("poiAddress", info.address);
				listMap.add(map);
			}
			
		}else{

			
		}	
		

		SimpleAdapter adapter = new SimpleAdapter(PutDestinationActivity.this, listMap, R.layout.put_destination_listview_item, 
						new String[]{"poiName","poiAddress"}, new int[]{R.id.put_destination_listview_name,R.id.put_destination_listview_address});
		
		listView.setAdapter(adapter);
		
		
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				Map<String,String> map= new HashMap<String,String>();
				map=listMap.get(arg2);
				
				String poiName =(String)map.get("poiName");
				String poiAddress=(String)map.get("poiAddress");
				
				ContentValues  values = new ContentValues();
				values.put("POIName", poiName);
				values.put("POIAddress", poiAddress);
				helper.insert(values);
				
				if(startOrEnd==START){
					
					intent.putExtra("start", poiName);
					PutDestinationActivity.this.setResult(RESULT_OK, intent);
					PutDestinationActivity.this.finish();
					
				}else if(startOrEnd==END){
					
					intent.putExtra("end", poiName);
					PutDestinationActivity.this.setResult(RESULT_OK, intent);
					PutDestinationActivity.this.finish();
					
				}
				else{
					Toast.makeText(PutDestinationActivity.this, "有異常", 1000);
				}

			}
		}) ;
		
		
	}
	
	
	/**
	 * 从数据库初始化DBlistview
	 */
	private void initListFromDB(){
		
		if(dbListMap.size()>0){
			dbListMap.clear();
		}
		
		Map<String,String> defaultMap= new HashMap<String,String>();
		defaultMap.put("poiName", "我的位置");
		defaultMap.put("poiAddress", "");
		dbListMap.add(defaultMap);
		
		int count=0;
		Cursor cursor;
		cursor=helper.query();
		startManagingCursor(cursor);//很重要
		
		if(cursor.moveToLast()){
			while(count<=9){
				
				if(!cursor.isBeforeFirst()){
					
					Map<String,String> map= new HashMap<String,String>();
					map.put("poiName", cursor.getString(cursor.getColumnIndex("POIName")));
					map.put("poiAddress", cursor.getString(cursor.getColumnIndex("POIAddress")));
					dbListMap.add(map);
					
					System.out.println("从数据库中读取的id----------------"+cursor.getInt(cursor.getColumnIndex("_id")));
					System.out.println("从数据库中读取的name----------------"+cursor.getString(cursor.getColumnIndex("POIName")));
					System.out.println("从数据库中读取的address----------------"+cursor.getString(cursor.getColumnIndex("POIAddress")));
					cursor.moveToPrevious();
					count++;
				}
				else{
					break;
				}
			}
		}
		
		helper.close();
		
		SimpleAdapter adapter = new SimpleAdapter(PutDestinationActivity.this, dbListMap, R.layout.put_destination_listview_item, 
				new String[]{"poiName","poiAddress"}, new int[]{R.id.put_destination_listview_name,R.id.put_destination_listview_address});

		DBListView.setAdapter(adapter);
		
		DBListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2==0){
					Intent intent = new Intent();
					
					if(startOrEnd==START){
						
						intent.putExtra("start", "我的位置");
						PutDestinationActivity.this.setResult(RESULT_OK, intent);
						PutDestinationActivity.this.finish();
						
					}else if(startOrEnd==END){
						
						intent.putExtra("end", "");
						PutDestinationActivity.this.setResult(RESULT_OK, intent);
						PutDestinationActivity.this.finish();
						
					}
					else{
						Toast.makeText(PutDestinationActivity.this, "有異常", 1000);
					}
				}
				else{
					
					Intent intent = new Intent();
					
					Map<String,String> map= new HashMap<String,String>();
					map=dbListMap.get(arg2);
					String poiName =(String)map.get("poiName");
					//String poiAddress=(String)map.get("poiAddress");
					
					if(startOrEnd==START){
						
						intent.putExtra("start", poiName);
						PutDestinationActivity.this.setResult(RESULT_OK, intent);
						PutDestinationActivity.this.finish();
						
					}else if(startOrEnd==END){
						
						intent.putExtra("end", poiName);
						PutDestinationActivity.this.setResult(RESULT_OK, intent);
						PutDestinationActivity.this.finish();
						
					}
					else{
						Toast.makeText(PutDestinationActivity.this, "有異常", 1000);
					}
				}

			}
		}) ;
	}

	
	/**
	 * 显示语音的dialog()
	 */
	public void showIatDialog() {
		// 获取引擎参数
		String engine = mSharedPreferences.getString(
				getString(R.string.preference_key_iat_engine),
				getString(R.string.preference_default_iat_engine));

		// 获取area参数，POI搜索时需要传入.
		String area = null;
		if (ENGINE_POI.equals(engine)) {
			final String defaultProvince = getString(R.string.preference_default_poi_province);
			String province = mSharedPreferences.getString(
					getString(R.string.preference_key_poi_province),
					defaultProvince);
			final String defaultCity = getString(R.string.preference_default_poi_city);
			String city = mSharedPreferences.getString(
					getString(R.string.preference_key_poi_city), defaultCity);

			if (!defaultProvince.equals(province)) {
				area = "search_area=" + province;
				if (!defaultCity.equals(city)) {
					area += city;
				}
			}
		}

		if (TextUtils.isEmpty(area))
			area = "";
		else
			area += ",";
		// 设置转写Dialog的引擎和poi参数.
		iatDialog.setEngine(engine, area, null);
		//iatDialog.setEngine("poi", "北京市", null);

		// 设置采样率参数，由于绝大部分手机只支持8K和16K，所以设置11K和22K采样率将无法启动录音.
		String rate = mSharedPreferences.getString(
				getString(R.string.preference_key_iat_rate),
				getString(R.string.preference_default_iat_rate));
		if (rate.equals("rate8k"))
			iatDialog.setSampleRate(RATE.rate8k);
		else if (rate.equals("rate11k"))
			iatDialog.setSampleRate(RATE.rate11k);
		else if (rate.equals("rate16k"))
			iatDialog.setSampleRate(RATE.rate16k);
		else if (rate.equals("rate22k"))
			iatDialog.setSampleRate(RATE.rate22k);
		// mResultText.setText(null);
		putDestination.setText(null);
		// 弹出转写Dialog.
		iatDialog.show();
	}
	
	
	
	
	
	
}
