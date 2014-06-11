package com.jmpmain.lvslrpg.particles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Smoke extends Particle {

	public long createTime;
	public long liveTime;
	private Rect particle;
	
	private Paint paint;
	
	int spawnX;
	int spawnY;
	
	float xVelocity;
	
	float xPosition;
	
	public Smoke(int x, int y, long time){
		destroy = false;
		spawnX = x;
		xPosition = spawnX;
		spawnY = y;
		
		particle = new Rect(spawnX, spawnY, spawnX+5, spawnY+5);
		paint = new Paint();
		paint.setARGB(64, 0, 0, 0);
		liveTime = 0;
		createTime = 0;
	}
	
	@Override
	public void update(long time) {
		
		long delta = time - createTime;
		if(delta >= liveTime){
			particle = new Rect(spawnX, spawnY, spawnX+3, spawnY+3);
			xPosition = spawnX;
			createTime = time;
			xVelocity = (float) ((Math.random() - 0.5f));
			liveTime = (long) (1000 + Math.random()*500);
		}
		
		particle.top -= 1;
		particle.bottom -= 1;
		
		
		xPosition += xVelocity;
		particle.left = (int) xPosition;
		particle.right = (int) (xPosition + 5);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(particle, paint);
	}

}
