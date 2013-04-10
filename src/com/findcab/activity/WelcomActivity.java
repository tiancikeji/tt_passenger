package com.findcab.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

import com.findcab.R;

public class WelcomActivity extends Activity {
	
	//RelativeLayout welcom_layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		//welcom_layout = (RelativeLayout) findViewById(R.id.welcom_layout);
		AnimationSet animationset = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(1100);
		animationset.addAnimation(alphaAnimation);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent mainIntent = null;
				mainIntent = new Intent(WelcomActivity.this,
						LandActivity.class);
				if (isSignup()) {

					mainIntent = new Intent(WelcomActivity.this,
							LocationOverlay.class);
				} else {

					mainIntent = new Intent(WelcomActivity.this,
							LandActivity.class);
				}
				startActivity(mainIntent);
				finish();
			}

		}, 1000);
	}

	/**
	 * 得到用户
	 */
	private boolean isSignup() {
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		String mobile = sharedata.getString("psMobile", "");
		String password = sharedata.getString("psPassword", "");
		if (!mobile.equals("") && !password.equals("")) {
			return true;
		}

		return false;
	}

}
