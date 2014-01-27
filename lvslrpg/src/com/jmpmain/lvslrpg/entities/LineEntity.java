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
	
	public LineEntity(){
		setX(y = 0);
	}
	
	@Override
	public void update(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(x, y, x + 1, y + 1, new Paint());
	}

}