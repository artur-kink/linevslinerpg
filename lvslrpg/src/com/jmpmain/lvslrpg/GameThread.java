package com.jmpmain.lvslrpg;

import java.util.Vector;

import com.jmpmain.lvslrpg.entities.LineEntity;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Main game thread.
 * All game logic is managed here.
 */
public class GameThread extends Thread implements SensorEventListener{
	
	/** Counter for updates per second(ups). */
	private int updateCallCount;
	
	/** The updates per second of the previous second. */
	public int ups;
	
	/** Last time fps second had elapsed. */
	private long lastUpdateCallReset;
	
	/**
	 * Last time game was updated.
	 */
	private long lastUpdate;
	
	/**
	 * GameSurface Surface Holder.
	 */
	private SurfaceHolder surfaceHolder;
	
	/**
	 * Main surface game is drawn to.
	 */
	private GameSurface gameSurface;
	
	/**
	 * Thread running state.
	 */
	private boolean running;
	
	public int touchX;
	public int touchY;
	
	/** Current game map. */
	public Map map;
	
	public LineEntity line;
	public Vector<LineEntity> enemies;

	public GameThread(SurfaceHolder holder, GameSurface surface){
		surfaceHolder = holder;
		gameSurface = surface;
		
		
		
		updateCallCount = 0;
		ups = 0;
		lastUpdateCallReset = 0;
		
		line = new LineEntity(500/12, 500/12);
		line.setDirection(1, 0);
		line.setColor(128, 0, 255, 0);
		
		enemies = new Vector<LineEntity>();
		{
			LineEntity enemy = new LineEntity(10/12, 500/12);
			enemy.setColor(128, 255, 255, 0);
			enemies.add(enemy);
		}
		
		{
			LineEntity enemy = new LineEntity(500/12, 10/12);
			enemy.setColor(128, 0, 255, 255);
			enemies.add(enemy);
		}
		
		{
			LineEntity enemy = new LineEntity(500/12, 1500/12);
			enemy.setColor(128, 0, 0, 255);
			enemies.add(enemy);
		}
		
		{
			LineEntity enemy = new LineEntity(1000/12, 500/12);
			enemy.setColor(128, 255, 0, 0);
			enemies.add(enemy);
		}
		
		setRunning(false);
	}
	
	/**
	 * Set thread running state.
	 */
	public void setRunning(boolean r){
		running = r;
	}
	
	/**
	 * Screen touch handler.
	 */
	public void onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_UP){
			touchX = (int) event.getX();
			touchY = (int) event.getY();
			int radius = (int)((float)gameSurface.getWidth()*0.05);
			
			if(touchY > gameSurface.getHeight() - (radius + 20) && touchY < gameSurface.getHeight() - 20){
				//Left turn button pressed.
				if(touchX > 20 && touchX < radius + 20){
					if(line.getYVelocity() != 0){
						line.setDirection(-1, 0);
					}else if(line.getXVelocity() != 0){
						line.setDirection(0, 1);
					}
				}
				//Right turn button pressed.
				if(touchX > gameSurface.getWidth() - (radius + 20) && touchX < gameSurface.getWidth() - 20){
					if(line.getYVelocity() != 0){
						line.setDirection(1, 0);
					}else if(line.getXVelocity() != 0){
						line.setDirection(0, -1);
					}
				}
			}
			
		}else if(event.getAction() == MotionEvent.ACTION_DOWN ||
			event.getAction() == MotionEvent.ACTION_MOVE){
			touchX = (int) event.getX();
			touchY = (int) event.getY();
			//line.setTarget(event.getX(), event.getY());
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Handle sensor accuracy change.	
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Handle sensor value changes.
	}
	
	@SuppressLint("WrongCall")
	private void drawCall(Canvas gameCanvas){
		//Draw game state.
		gameCanvas = surfaceHolder.lockCanvas();
		if(gameCanvas != null){
			synchronized (surfaceHolder) {
				gameSurface.onDraw(gameCanvas);
			}
			surfaceHolder.unlockCanvasAndPost(gameCanvas);
		}
	}
	
	@Override
	/**
	 * Main game loop.
	 */
	public void run() {

		Canvas gameCanvas = null;
		//Game loop.
		while (running) {
			
			//Check if game state should be updated.
			if(System.currentTimeMillis() - lastUpdate < 25){
				drawCall(gameCanvas);
				continue;
			}
			lastUpdate = System.currentTimeMillis();
			
			long currentTimeMillis = System.currentTimeMillis();
			
			//Update debug parameters.
			if(BuildConfig.DEBUG){				
				updateCallCount++;
				if(System.currentTimeMillis() - lastUpdateCallReset > 1000){
					lastUpdateCallReset = System.currentTimeMillis();
					ups = updateCallCount;
					updateCallCount = 0;
				}
			}
			
			line.update(currentTimeMillis);
			for(int i = 0; i < enemies.size(); i++){
				enemies.get(i).setTarget(line.getX(), line.getY());
				enemies.get(i).update(currentTimeMillis);
				if(enemies.get(i).dead){
					enemies.remove(i);
					i--;
				}
			}drawCall(gameCanvas);
		}
	}
}