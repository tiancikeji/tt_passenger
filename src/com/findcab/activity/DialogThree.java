package com.findcab.activity;

import com.findcab.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DialogThree extends Activity {
	
	private Button btn1,btn2,btn3,btn4;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.dialog3);
        
        init();
    }
	
private void init(){
		
		//返回调用的map_activity
		btn1 = (Button)findViewById(R.id.dialog_three_1);
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//isWaiting = false;
				LocationOverlay.isWaiting=false;
				Intent i = new  Intent();
				i.setClass(DialogThree.this, LocationOverlay.class);
				startActivity(i);
				DialogThree.this.finish();
				
			}
		});
		
		
		//返回调用的map_activity
		btn2=(Button)findViewById(R.id.dialog_three_2);
		btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LocationOverlay.isWaiting=false;
				Intent i = new  Intent();
				i.setClass(DialogThree.this, LocationOverlay.class);
				startActivity(i);
				DialogThree.this.finish();
				
			}
		});
		
		//返回调用的map_activity
		btn3=(Button)findViewById(R.id.dialog_three_3);
		btn3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LocationOverlay.isWaiting=false;
				Intent i = new  Intent();
				i.setClass(DialogThree.this, LocationOverlay.class);
				startActivity(i);
				DialogThree.this.finish();
				
			}
		});
		//返回调用的map_activity
		btn4=(Button)findViewById(R.id.dialog_three_4);
		btn4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LocationOverlay.isWaiting=false;
			
				Intent i = new  Intent();
				i.putExtra("fromDialog3", "dialog3");
				i.setClass(DialogThree.this, LocationOverlay.class);
				startActivity(i);
				DialogThree.this.finish();
				
			}
		});
		
	}
}
