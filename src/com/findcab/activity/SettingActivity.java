package com.findcab.activity;

import com.findcab.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener{
	private Button back;
	private TextView phoneTxt;
	private LinearLayout settingPhone;
	private RelativeLayout settingOrder;
	private RelativeLayout connectService;
	private RelativeLayout update;
	private RelativeLayout connectUs;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		init();
	}
	private void init(){
		
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		String mobile = sharedata.getString("psMobile", "");
		
		back=(Button)findViewById(R.id.setting_back);
		back.setOnClickListener(this);
		
		phoneTxt=(TextView)findViewById(R.id.setting_phone_txt);
		phoneTxt.setText(mobile);
		
		settingPhone=(LinearLayout)findViewById(R.id.setting_phone);
		settingPhone.setOnClickListener(this);
		
		settingOrder=(RelativeLayout)findViewById(R.id.setting_myorder);
		settingOrder.setOnClickListener(this);
		
		connectService=(RelativeLayout)findViewById(R.id.setting_connect_service);
		connectService.setOnClickListener(this);
		
		update=(RelativeLayout)findViewById(R.id.setting_update);
		update.setOnClickListener(this);
		
		connectUs=(RelativeLayout)findViewById(R.id.setting_connect_us);
		connectUs.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.setting_back:
			this.finish();
			break;
		case R.id.setting_phone:
			Toast.makeText(SettingActivity.this, "o", 1000);
			this.finish();
			break;
		case R.id.setting_myorder:
			Toast.makeText(SettingActivity.this, "o", 1000);
			break;
		case R.id.setting_connect_service:
			Toast.makeText(SettingActivity.this, "o", 1000);
			break;
		case R.id.setting_update:
			Toast.makeText(SettingActivity.this, "o", 1000);
			break;
		case R.id.setting_connect_us:
			Toast.makeText(SettingActivity.this, "o", 1000);
			break;
		}
		
	}
}
