package com.findcab.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class MyBitmap {
	/**
	 * 图片合成
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createBitmap(Bitmap src, String text) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();

		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);

		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(32);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);

		float width = paint.measureText(text);

		String array[] = new String[3];

		if (text.length() > 19) {

			array[0] = text.substring(0, 19);
			array[1] = text.substring(19);
			cv.drawText(array[0],
					(cv.getWidth() - paint.measureText(array[0])) / 2, (cv
							.getHeight() - 33) >> 1, paint);
			cv.drawText(array[1],
					(cv.getWidth() - paint.measureText(array[0])) / 2, (cv
							.getHeight() + 33) >> 1, paint);
		} else {

			cv.drawText(text, (cv.getWidth() - width) / 2, cv.getHeight() >> 1,
					paint);
		}

		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		return newb;
	}
}
