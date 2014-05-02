package com.jmpmain.lvslrpg.entities;

import com.jmpmain.lvslrpg.GameSurface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Item extends Entity {

	public int tileX;
	public int tileY;
	
	protected int drawX;
	protected int drawY;
	
	public Item(int tx, int ty, int dx, int dy){
		tileX = tx;
		tileY = ty;
		
		drawX = dx;
		drawY = dy;
	}
	
	@Override
	public void update(long time) {
		
	}

	@Override
	public void draw(Canvas canvas) {
		Paint p = new Paint();
		//Draw coin.
		canvas.drawBitmap(GameSurface.coin, new Rect(0, 0, 32, 32),
				new Rect(drawX, drawY, drawX + 32, drawY + 32), p);
		
	}
}
