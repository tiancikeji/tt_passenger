package com.iflytek.mscdemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
import com.findcab.activity.LocationOverlay;
import com.findcab.util.Tools;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

/**
 * 转写页面,通过调用SDK中提供的RecognizerDialog来实现转写功能.
 * 
 * @author iFlytek
 * @since 20120821
 */
public class IatActivity extends Activity implements OnClickListener,
		RecognizerDialogListener {
	// 日志TAG.
	private static final String TAG = "IatDemoActivity";

	// // title的文本内容.
	// private TextView mCategoryText;
	// 识别结果显示
	// private EditText mResultText;
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	// 识别Dialog
	private RecognizerDialog iatDialog;
	// 地图搜索关键字定义
	public static final String ENGINE_POI = "poi";

	private String start = null;
	private String end = null;
	public Context context = null;

	private Button start_cancel, end_cancel;
	private Button okButton, cancelButton;
	private EditText edit_start;

	private AutoCompleteTextView edit_end;
	Intent intent;
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	private ArrayList<MKPoiInfo> pointInfoList;
	private ListView selctList;
	private PopupWindow selectWindow = null;
	private boolean is = true;

	private int count;

	/**
	 * 页面初始化入口函数.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "[onCreate]" + savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call);

		initView();
		// 初始化转写Dialog, appid需要在http://open.voicecloud.cn获取.
		iatDialog = new RecognizerDialog(this, "appid="
				+ getString(R.string.app_id));
		iatDialog.setListener(this);

		// 初始化缓存对象.
		mSharedPreferences = getSharedPreferences(getPackageName(),
				MODE_PRIVATE);
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		context = this;
		// R.id.start_cancel是在callActivity中，现在先注释掉，以后有用 simsunny
		// start_cancel = (Button) findViewById(R.id.start_cancel);
		end_cancel = (Button) findViewById(R.id.end_mic);
		end_cancel.setOnClickListener(this);

		edit_start = (EditText) findViewById(R.id.edit_start);
		edit_end = (AutoCompleteTextView) findViewById(R.id.edit_end);

		okButton = (Button) findViewById(R.id.ok);
		okButton.setOnClickListener(this);

		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);

		mSearch = new MKSearch();
		mSearch.init(LocationOverlay.mBMapMan, new MKSearchListener() {

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_LONG)
							.show();
					return;
				}

				pointInfoList = res.getAllPoi();

				// showPrompt(edit_end, pointInfoList);
				// 将地图移动到第一个POI中心点
				if (res.getCurrentNumPois() > 0) {
					// 将poi结果显示到地图上
					String name = null;
					count = 0;
					for (int i = 0; i < res.getAllPoi().size(); i++) {

						name = res.getPoi(i).name;
						System.out.println("name=====>" + name);

						if (end != null && end.equals(name)) {
							count++;
						}

						if (count > 0) {
							is = true;
						} else {
							is = false;
						}

					}
					if (!is) {

						initPopupWindow(pointInfoList);
					} else {

						if (selectWindow == null) {

							initPopupWindow(pointInfoList);
						} else {

							sendInfo();
						}
					}

				} else if (res.getCityListNum() > 0) {
					String strInfo = "在";
					for (int i = 0; i < res.getCityListNum(); i++) {
						strInfo += res.getCityListInfo(i).city;
						strInfo += ",";
					}
					strInfo += "找到结果";
					Toast.makeText(context, strInfo, Toast.LENGTH_LONG).show();
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
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {

			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

		});
	}

	/**
	 * 初始化engine相关参数.
	 * 
	 * @param
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// 获取之前保存的引擎参数，若没有使用默认的参数sms.

	}

	/**
	 * 按钮点击事件.
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// 转写按钮
		// case R.id.end_mic:
		// showIatDialog();
		// break;

		case R.id.cancel:

			finish();
			break;
		// case R.id.start_cancel:
		//
		// edit_start.setText("");
		// break;

		case R.id.ok:
			start = edit_start.getText().toString().trim();
			end = edit_end.getText().toString().trim();

			searchNearby(end);

			break;
		default:
			break;
		}
	}

	/**
	 * 发送信息
	 */
	private void sendInfo() {
		intent = new Intent(IatActivity.this, LocationOverlay.class);
		if (start.equals("我的位置")
				&& (LocationOverlay.address == null || LocationOverlay.address
						.equals(""))) {
			Toast.makeText(context, "无法获取当前位置，请手动输入", Toast.LENGTH_LONG).show();
			return;
		}

		if (!start.equals("我的位置")) {

			intent.putExtra("start", start);
		}
		if (!end.equals("")) {

			intent.putExtra("end", end);
			setResult(1, intent);
			finish();
		} else {

			Tools.myToast(context, "请输入目的地");
		}
	}

	private void searchNearby(String location) {

		// poiSearchInCity(String city, String key)
		mSearch.poiSearchInCity("北京", location);

		return;
	}

	private void showPrompt(AutoCompleteTextView textView,
			ArrayList<MKPoiInfo> pointInfoList) {
		String[] array = new String[pointInfoList.size()];
		for (int i = 0; i < pointInfoList.size(); i++) {

			MKPoiInfo info = pointInfoList.get(i);
			array[i] = info.name;

		}
		ArrayAdapter<String> av = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, array);
		edit_end.setAdapter(av);

	}

	/**
	 * 显示转写对话框.
	 * 
	 * @param
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
		edit_end.setText(null);
		// 弹出转写Dialog.
		iatDialog.show();
	}

	/**
	 * RecognizerDialogListener的"结束识别会话"回调接口. 参数详见<MSC开发指南>.
	 * 
	 * @param error
	 */
	@Override
	public void onEnd(SpeechError error) {
	}

	/**
	 * RecognizerDialogListener的"返回识别结果"回调接口. 通常服务端会多次返回结果调用此接口，结果需要进行累加.
	 * 
	 * @param results
	 * @param isLast
	 */
	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast) {
		StringBuilder builder = new StringBuilder();
		for (RecognizerResult recognizerResult : results) {
			builder.append(recognizerResult.text);
		}

		if (builder.equals("")) {
			return;
		}

		if (builder.toString().endsWith("。")) {

			edit_end.append(builder.substring(0, builder.length() - 1));
		} else {
			edit_end.append(builder);

		}
		edit_end.setSelection(edit_end.length() - 1);
	}

	/**
	 * 初始化选择列表
	 */
	private void initPopupWindow(final ArrayList<MKPoiInfo> pointInfoList) {
		View select_view = ((Activity) context).getLayoutInflater().inflate(
				R.layout.select_win, null, false);
		final String[] array = new String[pointInfoList.size()];
		for (int i = 0; i < pointInfoList.size(); i++) {

			MKPoiInfo info = pointInfoList.get(i);
			array[i] = info.name;

		}
		selctList = (ListView) select_view.findViewById(R.id.select_List);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_dropdown_item_1line, array);

		selctList.setAdapter(adapter);

		int width = (int) getResources().getDimension(R.dimen.menu_width);
		selectWindow = new PopupWindow(select_view, width, edit_end.getWidth(),
				true);// 创建PopupWindow实例
		selectWindow.setBackgroundDrawable(new BitmapDrawable());

		selectWindow.showAsDropDown(edit_end);
		selctList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {

				end = array[index];
				edit_end.setText(array[index]);
				selectWindow.dismiss();

				sendInfo();
			}
		});

	}
}
