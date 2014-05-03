package com.jmpmain.lvslrpg.entities;

import com.jmpmain.lvslrpg.GameSurface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Item extends Entity {

	public int x;
	public int y;
	
	public int width;
	public int height;
	
	private Bitmap icon;
	
	public enum ItemType{
		Coin,
		Potion
	}
	
	public ItemType type;
	
	public Item(ItemType t, int tx, int ty){
		type = t;
		x = tx;
		y = ty;
		
		width = 32;
		height = 32;
		
		if(type == ItemType.Coin)
			icon = GameSurface.coin;
		else if(type == ItemType.Potion)
			icon = GameSurface.potion;
	}
	
	@Override
	public void update(long time) {
		
	}

	@Override
	public void draw(Canvas canvas) {
		Paint p = new Paint();
		//Draw coin.
		canvas.drawBitmap(icon, new Rect(0, 0, 32, 32),
				new Rect(x, y, x + 32, y + 32), p);
		
	}
}
