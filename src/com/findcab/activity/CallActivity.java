package com.findcab.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.findcab.R;
import com.findcab.util.Tools;
import com.iflytek.mscdemo.IatActivity;

/**
 * 叫车
 * 
 * @author yuqunfeng
 * 
 */
public class CallActivity extends Activity implements OnClickListener {

	private String start = null;
	private String end = null;
	public Context context = null;
	
	private Intent putIntent;
	private Intent getIntent;

	// private Button start_cancel, end_cancel;
	private Button okButton, cancelButton;
	private EditText edit_start, edit_end;
	private LinearLayout linearlayout_premium;
	private Button btn0,btn5,btn10,btn15,btn20;

	
	private int premium=0;
	
	private final int START =12;
	private final int END = 22;
	private final int BLACK=0xff000000;//纯黑
	private final int WHITE=0xffffffff;//白色

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call);
		Tools.init();
		initView();
	}

	/**
	 * 初始化view
	 */
	private void initView() {

		context = this;

		edit_start = (EditText) findViewById(R.id.edit_start);
		edit_end = (EditText) findViewById(R.id.edit_end);
		
		edit_start.setClickable(true);
		edit_start.setFocusable(true);
		edit_start.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					 getIntent= new Intent();
					 getIntent.setClass(CallActivity.this,PutDestinationActivity.class );
					 getIntent.putExtra("putDestination", START);
					 startActivityForResult(getIntent,START);
				}
				return false;
			}
		});

		edit_end.setClickable(true);
		edit_end.setFocusable(true);
		edit_end.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					 getIntent= new Intent();
					 getIntent.putExtra("putDestination", END);
					 getIntent.setClass(CallActivity.this,PutDestinationActivity.class );
					 startActivityForResult(getIntent,END);
				}
				return false;
			}
		});
		//edit_end.setOnClickListener(this);

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

		case R.id.cancel:

			finish();
			break;
			
		case R.id.ok:
			
			putIntent = new Intent();
			start = edit_start.getText().toString().trim();
			end = edit_end.getText().toString().trim();
			
			putIntent.putExtra("premium", premium);
			
			if (!start.equals("我的位置")) {
				putIntent.putExtra("start", start);
			}
			if (!end.equals("")) {
				putIntent.putExtra("end", end);
				setResult(1, putIntent);
				finish();
			} else {
				Tools.myToast(context, "请输入目的地");
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
					 edit_start.setText(start );
					 
					 
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
						 edit_end.setText(end);
						 
					 } else {
						 
					 //start = startAddress;
					 
					 }
				 }
				 
		 }
	
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
}