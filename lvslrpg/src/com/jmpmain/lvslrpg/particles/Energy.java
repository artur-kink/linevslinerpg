package com.jmpmain.lvslrpg.particles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Energy extends Particle {

	public long createTime;
	
	private Paint paint;
	private Rect particle;
	
	public Energy(int x, int y, long time){
		destroy = false;
		createTime = time;
		
		particle = new Rect(x, y, x+8, y+8);

		paint = new Paint();
		paint.setARGB(255, 0, 128, 150);
	}
	
	@Override
	public void update(long time) {
		if(time - createTime >= 1500){
			destroy = true;
			return;
		}
		
		paint.setAlpha(Math.max(0, (int)(180.0f*(1 - (float)(time - createTime)/1500.0f))));
		
		particle.top -= 1;
		particle.bottom -= 1;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(particle, paint);
	}

}
