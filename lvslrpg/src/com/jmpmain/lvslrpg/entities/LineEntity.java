package com.jmpmain.lvslrpg.entities;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A line entity in line vs line fights.
 */
public class LineEntity extends Entity {

	/** X position. */
	private float x;
	/** Y position. */
	private float y;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public float xVelocity;
	public float yVelocity;
	
	private float lastXDraw;
	private float lastYDraw;
	
	public LineEntity(){
		setX(300);
		setY(300);
		lastXDraw = 300;
		lastYDraw = 300;
	}
	
	@Override
	public void update(long time) {
		x += xVelocity;
		y += yVelocity;
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setARGB(255, 0, 255, 255);
		canvas.drawLine(lastXDraw, lastYDraw, x, y, paint);
		lastXDraw = x;
		lastYDraw = y;
	}

}