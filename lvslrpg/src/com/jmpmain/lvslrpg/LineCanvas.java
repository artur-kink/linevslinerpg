package com.jmpmain.lvslrpg;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class LineCanvas extends Canvas {
	
	public Bitmap bitmap;
	
	public LineCanvas(int width, int height){
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		setBitmap(bitmap);
		drawARGB(0, 0, 0, 0);
	}
}
