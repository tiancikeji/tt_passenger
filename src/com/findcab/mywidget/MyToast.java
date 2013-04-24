package com.findcab.mywidget;

import com.findcab.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {

	private Context context;
	private Toast toast;
	private String text = "";
	public MyToast(Context context,String text){
		this.context = context;
		this.text = text;
	}
	
//	public void initMyToast1(String text){
//		LayoutInflater inflater = context.getLayoutInflater();
//		View layout = inflater.inflate(R.layout.mywidget_toast1,null);
//			   TextView title = (TextView) layout.findViewById(R.id.mywidget_toast1_textview);
//			   title.setText(text);
//			   toast = new Toast(context);
//			   toast.setGravity(Gravity.CENTER, 0, 0);
//			   toast.setDuration(Toast.LENGTH_LONG);
//			   toast.setView(layout);
//			   toast.show();
//	}


}
