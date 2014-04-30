package com.jmpmain.lvslrpg.entities;

import com.jmpmain.lvslrpg.Map;

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
	private int lastXCheck;
	/** Last y position where map collision was checked. */
	private int lastYCheck;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
		lastXCheck = (int)x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
		lastYCheck = (int)y;
	}
	
	public float velocity;
	
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
	private int lastXDraw;
	/** Last y position where line was drawn. */
	private int lastYDraw;
	
	/** Map where line entity exists. */
	private Map map;
	public void setMap(Map m){
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
		velocity = 0.50f;
		xVelocity = yVelocity = 0;
		dead = false;
		
		paint = new Paint();
	}
	
	public void setDirection(float x, float y){
		xVelocity = x*velocity;
		yVelocity = y*velocity;
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
		
		//Update entity position.
		x += xVelocity;
		y += yVelocity;
			
		//If position has changed check for collision.
		if(lastXCheck != (int)x || lastYCheck != (int)y){
			lastXCheck = (int)x;
			lastYCheck = (int)y;
			
			if(!isEmpty(x, y)){
				health--;
			}
			
			if(x >= 0 && y >= 0 && x < map.width && y < map.height)
				map.setTile(lastXCheck, lastYCheck, Map.TileType.Entity);
			
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
		if(x < 0 || y < 0 || x >= map.width || y >= map.height)
			return false;
		
		//Check if pixel clear.
		return map.getTile((int)x2, (int)y2) != Map.TileType.Entity;
	}
	
	@Override
	public void draw(Canvas canvas) {
		//Fill tile player is in.
		if(lastXDraw != (int)x || lastYDraw != (int)y){
			paint.setStrokeWidth(1);
			canvas.drawRect(lastXDraw*map.tileSize, lastYDraw*map.tileSize,
					lastXDraw*map.tileSize + map.tileSize, lastYDraw*map.tileSize + map.tileSize, paint);
			lastXDraw = (int)x;
			lastYDraw = (int)y;
		}
	}

}