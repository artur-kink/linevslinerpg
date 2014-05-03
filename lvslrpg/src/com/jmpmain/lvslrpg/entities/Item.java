package com.jmpmain.lvslrpg.entities;

import com.jmpmain.lvslrpg.GameSurface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Item extends Entity {

	public int x;
	public int y;
	
	public int width;
	public int height;
	
	public Item(int tx, int ty){
		x = tx;
		y = ty;
	}
	
	@Override
	public void update(long time) {
		
	}

	@Override
	public void draw(Canvas canvas) {
		Paint p = new Paint();
		//Draw coin.
		canvas.drawBitmap(GameSurface.coin, new Rect(0, 0, 32, 32),
				new Rect(x, y, x + 32, y + 32), p);
		
	}
}
