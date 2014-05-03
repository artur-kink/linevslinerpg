package com.jmpmain.lvslrpg.particles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * A blood particle.
 */
public class Blood extends Particle {

	public long createTime;
	
	private static Paint paint;
	private Rect particle;
	
	private float xVelocity;
	private float yVelocity;
	
	public Blood(int x, int y, long time){
		destroy = false;
		createTime = time;
		
		particle = new Rect(x, y, x+3, y+3);
		
		xVelocity = (float)(Math.random()-0.5)*10;
		yVelocity = (float)(Math.random()-0.5)*10;
		
		if(paint == null){
			paint = new Paint();
			paint.setARGB(128, 255, 0, 0);
		}
	}
	
	@Override
	public void update(long time) {
		if(time - createTime > 1000){
			destroy = true;
			return;
		}
		
		particle.top += yVelocity;
		particle.bottom += yVelocity;
		
		particle.left += xVelocity;
		particle.right += xVelocity;
		
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(particle, paint);
	}

}
