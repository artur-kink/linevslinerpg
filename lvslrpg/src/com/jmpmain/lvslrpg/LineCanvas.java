package com.jmpmain.lvslrpg;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class LineCanvas extends Canvas {
	
	public Bitmap bitmap;
	
	public LineCanvas(){
		bitmap = Bitmap.createBitmap(1280, 1920, Bitmap.Config.ARGB_8888);
		setBitmap(bitmap);
		drawARGB(0, 0, 0, 0);
	}
}
