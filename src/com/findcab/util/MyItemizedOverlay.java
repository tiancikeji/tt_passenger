package com.findcab.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findcab.activity.LocationOverlay;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();

	private Context mContext;
	static PopupOverlay pop = null;

	public MyItemizedOverlay(Context context, Drawable maker,
			List<OverlayItem> geoList) {
		super(maker);
		this.mContext = context;
		this.mGeoList = geoList;
		pop = new PopupOverlay(LocationOverlay.mMapView,
				new PopupClickListener() {

					@Override
					public void onClickedPopup() {
						Log.d("hjtest  ", "clickpop");
					}
				});
		populate();
		populate();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return mGeoList.get(arg0);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mGeoList.size();
	}

	public void addItem(OverlayItem item) {
		mGeoList.add(item);
		populate();
	}

	public void removeItem(int index) {
		mGeoList.remove(index);
		populate();
	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		// TODO Auto-generated method stub
		if (pop != null) {
			pop.hidePop();
		}
		return super.onTap(arg0, arg1);
	}

	boolean isShow;

	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub

		Drawable marker = this.mContext.getResources().getDrawable(
				com.findcab.R.drawable.title); // 得到需要标在地图上的资源
		BitmapDrawable bd = (BitmapDrawable) marker;
		Bitmap popbitmap = bd.getBitmap();
		Bitmap bitmap = MyBitmap.createBitmap(popbitmap,
				LocationOverlay.address);
		isShow = !isShow;

		if (index == 0) {

			Toast.makeText(this.mContext, mGeoList.get(index).getTitle(),
					Toast.LENGTH_SHORT).show();

			pop.showPopup(bitmap, mGeoList.get(index).getPoint(), 210);

		}

		return super.onTap(index);
	}
}
