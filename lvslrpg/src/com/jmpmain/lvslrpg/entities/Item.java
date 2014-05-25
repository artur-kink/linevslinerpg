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
		
		Coin(0),
		Potion(1),
		Teleport_Scroll(2),
		Speed_Scroll(3),
		Chest(4);
		
		public final int value;
		
		private ItemType(int v){
			value = v;
		}
	}
	
	public ItemType type;
	
	public Item(ItemType t, int tx, int ty){
		type = t;
		x = tx;
		y = ty;
		
		width = 32;
		height = 32;
		
		icon = GetItemIcon(t);
	}
	
	public static Bitmap GetItemIcon(ItemType type){
		if(type == ItemType.Coin)
			return GameSurface.coin;
		else if(type == ItemType.Potion)
			return GameSurface.potion;
		else if(type == ItemType.Teleport_Scroll)
			return GameSurface.teleport_scroll;
		else if(type == ItemType.Speed_Scroll)
			return GameSurface.speed_scroll;
		else if(type == ItemType.Chest)
			return GameSurface.chest;
		return null;
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
