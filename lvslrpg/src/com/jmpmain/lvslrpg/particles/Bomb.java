package com.jmpmain.lvslrpg.particles;

import com.jmpmain.lvslrpg.GameSurface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Bomb extends Particle {

	Paint paint;
	
	public float x;
	public float y;
	
	public float destX;
	public float destY;
	
	private long createTime;
	private long throwTime;
	
	public Bomb(float x, float y, float dx, float dy){
		this.x = x;
		this.y = y;
		
		destX = dx;
		destY = dy;
		
		destroy = false;
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		createTime = System.currentTimeMillis();
		
		throwTime = (long) (Math.abs((x - destX)) + Math.abs((y - destY)));
	}
	
	@Override
	public void update(long time) {
		if(time - createTime > throwTime){
			destroy = true;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(GameSurface.bomb,
			x + ((float)(destX - x))/((float)throwTime /((float)((System.currentTimeMillis() - createTime)))),
			y + ((float)(destY - y))/((float)throwTime /((float)((System.currentTimeMillis() - createTime)))), paint);
	}

}
