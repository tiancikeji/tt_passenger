package com.findcab.activity;

import com.findcab.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DialogTwo extends Activity {
	
	
	private Button btnContiune;
	private Button btnCancel;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.dialog2);
        init();
    }
	
	private void init(){
		
		//进入下一个activity
		btnCancel = (Button)findViewById(R.id.dialog_two_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new  Intent();
				i.setClass(DialogTwo.this, DialogThree.class);
				startActivity(i);
				DialogTwo.this.finish();
				
			}
		});
		
		
		//返回调用的map_activity
		btnContiune=(Button)findViewById(R.id.dialog_two_contiune);
		btnContiune.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(1);
				finish();
			}
		});
		
	}
}

