package com.findcab.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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

	private Button start_cancel, end_cancel;
	private Button okButton, cancelButton;
	private EditText edit_start, edit_end;

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
		start_cancel = (Button) findViewById(R.id.start_cancel);
		start_cancel.setOnClickListener(this);
		end_cancel = (Button) findViewById(R.id.end_mic);
		end_cancel.setOnClickListener(this);

		edit_start = (EditText) findViewById(R.id.edit_start);
		edit_end = (EditText) findViewById(R.id.edit_end);

		okButton = (Button) findViewById(R.id.ok);
		okButton.setOnClickListener(this);

		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);
	}

	Intent intent;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.cancel:

			finish();
			break;
		case R.id.start_cancel:

			edit_start.setText("");
			break;
		case R.id.end_mic:
			intent = new Intent(CallActivity.this, IatActivity.class);
			startActivity(intent);

			break;
		case R.id.ok:

			start = edit_start.getText().toString().trim();
			end = edit_end.getText().toString().trim();
			intent = new Intent(CallActivity.this, LocationOverlay.class);

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

			break;

		}

	}

}