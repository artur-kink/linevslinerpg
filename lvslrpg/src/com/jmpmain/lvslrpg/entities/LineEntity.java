package com.jmpmain.lvslrpg.entities;

import com.jmpmain.lvslrpg.AudioPlayer;
import com.jmpmain.lvslrpg.GameSurface;
import com.jmpmain.lvslrpg.GameThread;
import com.jmpmain.lvslrpg.MainActivity;
import com.jmpmain.lvslrpg.Map;
import com.jmpmain.lvslrpg.R;
import com.jmpmain.lvslrpg.Map.TileType;
import com.jmpmain.lvslrpg.particles.Blood;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * A line entity in line vs line fights.
 */
public class LineEntity extends Entity {

	/** X position. */
	protected float x;
	/** Y position. */
	protected float y;
	
	/** Last x position where map collision was checked. */
	protected int lastXCheck;
	/** Last y position where map collision was checked. */
	protected int lastYCheck;
	
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
	protected float xVelocity;
	
	public float getXVelocity(){
		return xVelocity;
	}
	
	public float getTileXVelocity(int x, int y){
		return xVelocity*getTileVelocity(x, y);
	}
	
	/** Vertical velocity. */
	protected float yVelocity;
	
	public float getYVelocity(){
		return yVelocity;
	}
	
	public float getTileYVelocity(int x, int y){
		return yVelocity*getTileVelocity(x, y);
	}
	
	private float getTileVelocity(int x, int y){
		
		if(x < 0 || y < 0 || x >= map.width || y >= map.height)
			return 1;
		
		TileType tile = map.getTile(x, y);
		switch(tile){
			case Mountain:
				return 0.5f;
			case Hill:
				return 0.6f;
			case Sand:
				return 0.9f;
			case Water:
				return 0.5f;
			case Forest:
				return 0.9f;
			default:
				return 1.0f;
			
		}
	}
	
	/** Last x position where line was drawn. */
	private int lastXDraw;
	/** Last y position where line was drawn. */
	private int lastYDraw;
	
	/** Map where line entity exists. */
	protected Map map;
	
	public void setMap(Map m){
		map = m;
	}
	
	public int health;
	public int maxHealth;
	public boolean dead;
	
	protected long lastFrameUpdate;
	protected int frame;
	
	/** Paint used to draw line. */
	private Paint paint;
	
	public Bitmap character;
	
	/** Set line color. */
	public void setColor(int a, int r, int g, int b){
		paint.setARGB(a, r, g, b);
	}
	
	public LineEntity(int pX, int pY){
		setX(pX);
		setY(pY);
		lastXDraw = pX;
		lastYDraw = pY;
		health = maxHealth = 5;
		velocity = 0.50f;
		xVelocity = yVelocity = 0;
		dead = false;
		
		paint = new Paint();
	}
	
	public void setDirection(float x, float y){
		xVelocity = x*velocity;
		yVelocity = y*velocity;
		
		x = lastXCheck;
		y = lastYCheck;
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
	
	/**
	 * Add health to player, caps at max health.
	 * @param h Amount of health to add.
	 */
	public void addHealth(int h){
		health += h;
		if(health > maxHealth)
			health = maxHealth;
	}
	
	/**
	 * Set the max health of entity.
	 * @param h Max health value to set.
	 */
	public void setMaxHealth(int h){
		maxHealth = h;
		health = h;
	}
	
	@Override
	public void update(long time) {
		
		if(dead)
			return;
		
		if(time - lastFrameUpdate > 100){
			frame++;
			if(frame > 1){
				frame = 0;
			}
			lastFrameUpdate = time;
		}
		
		//Update entity position.
		if(this == GameThread.instance.line && GameThread.instance.haveSpeedScroll){
			x += getTileXVelocity(lastXCheck, lastYCheck)*1.75;
			y += getTileYVelocity(lastXCheck, lastYCheck)*1.75;
		}else{
			x += getTileXVelocity(lastXCheck, lastYCheck);
			y += getTileYVelocity(lastXCheck, lastYCheck);
		}	
		
		//If position has changed check for collision.
		if(lastXCheck != (int)x || lastYCheck != (int)y){
			lastXCheck = (int)x;
			lastYCheck = (int)y;
			
			if(!isEmpty(x, y)){
				if((this == GameThread.instance.line && !GameThread.instance.haveShieldScroll) || this != GameThread.instance.line){
					health--;
					AudioPlayer.playSound(AudioPlayer.hit);
					for(int i = 0; i < 5; i++){
						GameThread.instance.particles.add(new Blood((int)x*map.tileSize, (int)y*map.tileSize, time));
					}
				}else{
					GameThread.instance.shieldedDamage++;
					if(GameThread.instance.shieldedDamage >= 10)
						MainActivity.context.giveAchievement(R.string.achievement_shielding_my_health);
				}
			}
			
			if(x >= 0 && y >= 0 && x < map.width && y < map.height)
				map.setTileDamage(lastXCheck, lastYCheck, true);
			
		}
		if(health <= 0){
			AudioPlayer.playSound(AudioPlayer.dead);
			dead = true;
		}
	}

	/**
	 * Check if map position at given point is clear.
	 * @param x2 X Position.
	 * @param y2 Y Position.
	 * @return True if map is empty at given point, else false.
	 */
	protected boolean isEmpty(float x2, float y2){
		//Check for out of bounds.
		if(x2 < 0 || y2 < 0 || x2 >= map.width || y2 >= map.height)
			return false;
		
		return !map.getDamage((int)x2, (int)y2);
	}
	
	@Override
	public void draw(Canvas canvas) {
		Paint p = new Paint();
		
		if(dead){
			//Draw dead body.
			canvas.drawBitmap(GameSurface.dead, new Rect(0,0, 32, 32),
					new Rect((int)(x*map.tileSize) - 16, (int)(y*map.tileSize) - 16, 
							(int)(x*map.tileSize)- 16 + 32, (int)(y*map.tileSize) - 16 + 32), p);
		}else{
			
			if(this == GameThread.instance.line && GameThread.instance.haveShieldScroll){
				p.setARGB(128, 255, 0, 0);
			}
			//Draw sprite.
			canvas.drawBitmap(character, new Rect(frame*32, 0, frame*32 + 32, 32),
					new Rect((int)(x*map.tileSize) - 16, (int)(y*map.tileSize) - 16, 
							(int)(x*map.tileSize)- 16 + 32, (int)(y*map.tileSize) - 16 + 32), p);
			
			//Draw health bar.
			p.setARGB(50, 255, 0, 0);
			//Health bar background.
			canvas.drawRect(new Rect((int)(x*map.tileSize) - 16, (int)(y*map.tileSize) - 24,
					(int)(x*map.tileSize) + 16, (int)(y*map.tileSize) - 16), p);
			p.setARGB(150, 255, 0, 0);
			//Foreground actual health.
			canvas.drawRect(new Rect((int)(x*map.tileSize) - 16, (int)(y*map.tileSize) - 24,
					(int)(x*map.tileSize) - 16 + (int)(32*((float)health/(float)maxHealth)), (int)(y*map.tileSize) - 16), p);
		}
	}
	
	public void drawBackground(Canvas canvas) {
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