package com.jmpmain.lvslrpg.entities;

import android.graphics.Bitmap;
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
	
	/** Last x position where map collision was checked. */
	private float lastXCheck;
	/** Last y position where map collision was checked. */
	private float lastYCheck;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
		lastXCheck = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
		lastYCheck = y;
	}
	
	public float xVelocity;
	public float yVelocity;
	
	/** Last x position where line was drawn. */
	private float lastXDraw;
	/** Last y position where line was drawn. */
	private float lastYDraw;
	
	/** Bitmap where line entity exists. */
	private Bitmap map;
	public void setMap(Bitmap m){
		map = m;
	}
	
	public LineEntity(){
		setX(300);
		setY(300);
		lastXDraw = 300;
		lastYDraw = 300;
		
		xVelocity = yVelocity = 0;
	}
	
	@Override
	public void update(long time) {
		
		//Update position pixel by pixel to check all pixel
		//collisions, This check is done when speed is greater than 1,
		//to avoid skipping pixels.
		if(Math.abs(xVelocity) > 1 || Math.abs(yVelocity) > 1){
			float incrementXAmount = 1.0f;
			if(xVelocity < 0)
				incrementXAmount = -1.0f;
			
			float incrementYAmount = 1.0f;
			if(yVelocity < 0)
				incrementYAmount = -1.0f;
			
			float xAmount = Math.abs(xVelocity);
			float yAmount = Math.abs(yVelocity);
			//Increment x and y positions pixel by pixel.
			while(xAmount > 1 || yAmount > 1){
				if(xAmount > 1){
					x += incrementXAmount;
					xAmount--;
				}
				if(yAmount > 1){
					y += incrementYAmount;
					yAmount--;
				}
				
				//If position has changed by a pixel, check for collision.
				if(lastXCheck != x || lastYCheck != y){
					lastXCheck = x;
					lastYCheck = y;
					if(!isEmpty(x, y)){
						x = y = 300;
						break;
					}
				}
			}
			//Move any remaining amount, < 1.
			x += incrementXAmount*xAmount;
			y += incrementYAmount*yAmount;
		}else{
			x += xVelocity;
			y += yVelocity;
		}
		
		//If position has changed check for collision.
		if(lastXCheck != x || lastYCheck != y){
			lastXCheck = x;
			lastYCheck = y;
			if(!isEmpty(x, y)){
				x = y = 300;
			}
		}
	}

	/**
	 * Check if map position at given point is clear.
	 * @param x2 X Position.
	 * @param y2 Y Position.
	 * @return True if map is empty at given point, else false.
	 */
	private boolean isEmpty(float x2, float y2){
		//Check for out of bounds.
		if(x < 0 || y < 0 || x >= map.getWidth() || y >= map.getHeight())
			return false;
		
		//Check if pixel clear.
		return map.getPixel((int)x2, (int)y2) == 0;
	}
	
	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setARGB(255, 0, 255, 255);
		//Draw line between last draw position and current position.
		canvas.drawLine(lastXDraw, lastYDraw, x, y, paint);
		lastXDraw = x;
		lastYDraw = y;
	}

}