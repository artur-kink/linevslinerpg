package com.jmpmain.lvslrpg.particles;

import com.jmpmain.lvslrpg.GameSurface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Heal extends Particle {

	public long createTime;
	
	private Paint paint;
	
	private float x;
	private float y;
	
	public Heal(int x, int y, long time){
		destroy = false;
		createTime = time;

		this.x = x;
		this.y = y;
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
	}
	
	@Override
	public void update(long time) {
		if(time - createTime >= 1500){
			destroy = true;
			return;
		}
		paint.setAlpha(Math.max(0, (int)(255.0f*(1 - (float)(time - createTime)/1500.0f))));
		y -= 1.5f;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(GameSurface.health, x, y, paint);
	}
}
