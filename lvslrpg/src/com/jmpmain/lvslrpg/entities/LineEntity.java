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
	
	/** Horizontal velocity. */
	private float xVelocity;
	
	public float getXVelocity(){
		return xVelocity;
	}
	
	/** Vertical velocity. */
	private float yVelocity;
	
	public float getYVelocity(){
		return yVelocity;
	}
	
	/** Last x position where line was drawn. */
	private float lastXDraw;
	/** Last y position where line was drawn. */
	private float lastYDraw;
	
	/** Bitmap where line entity exists. */
	private Bitmap map;
	public void setMap(Bitmap m){
		map = m;
	}
	
	public int health;
	public int maxHealth;
	public boolean dead;
	
	/** Paint used to draw line. */
	private Paint paint;
	
	/** Set line color. */
	public void setColor(int a, int r, int g, int b){
		paint.setARGB(a, r, g, b);
	}
	
	public LineEntity(int pX, int pY){
		setX(pX);
		setY(pY);
		lastXDraw = pX;
		lastYDraw = pY;
		health = maxHealth = 20;
		xVelocity = yVelocity = 0;
		dead = false;
		
		paint = new Paint();
	}
	
	public void setDirection(float x, float y){
		xVelocity = x;
		yVelocity = y;
	}
	
	public void setTarget(float tX, float tY){
		float deltaY = (tY - getY());
		float deltaX = (tX - getX());
		
		if(Math.abs(deltaX) > Math.abs(deltaY)){
			if(xVelocity == 0){
				setDirection(xVelocity = Math.signum(deltaX), 0);
			}
		}else{
			if(yVelocity == 0){
				setDirection(0 , yVelocity = Math.signum(deltaY));
			}
		}
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
						health--;
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
				health--;
			}
		}
		if(health < 0){
			dead = true;
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
		//Draw line between last draw position and current position.
		canvas.drawLine(lastXDraw, lastYDraw, x, y, paint);
		paint.setStrokeWidth(1);
		lastXDraw = x;
		lastYDraw = y;
	}

}