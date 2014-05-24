package com.jmpmain.lvslrpg.particles;

import com.jmpmain.lvslrpg.entities.Item;
import com.jmpmain.lvslrpg.entities.Item.ItemType;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ItemParticle extends Particle {

	public long createTime;
	private Paint paint;
	private Bitmap icon;
	
	public float x;
	public float y;
	
	float xVelocity;
	float yVelocity;
	
	public ItemType type;
	
	public ItemParticle(int x, int y, ItemType t, long time){
		destroy = false;
		createTime = time;
		
		paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		icon = Item.GetItemIcon(t);
		
		this.x = x;
		this.y = y;
		
		xVelocity = (float) (Math.random() - 0.5)*10;
		yVelocity = (float) (Math.random() - 0.5)*10;
		
		type = t;
	}
	
	@Override
	public void update(long time) {
		if(time - createTime >= 750){
			destroy = true;
			return;
		}
		
		paint.setAlpha(Math.min(255, 255 - (int)(255.0f*(1 - (float)(time - createTime)/1000.0f))));
		
		x += xVelocity;
		y += yVelocity;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(icon, x, y, paint);
	}
}

