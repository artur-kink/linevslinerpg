package com.jmpmain.lvslrpg;

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
	
	public LineEntity line;
	
	public GameThread(SurfaceHolder holder, GameSurface surface){
		surfaceHolder = holder;
		gameSurface = surface;
		
		updateCallCount = 0;
		ups = 0;
		lastUpdateCallReset = 0;
		
		line = new LineEntity();
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
		if(event.getAction() == MotionEvent.ACTION_DOWN ||
			event.getAction() == MotionEvent.ACTION_MOVE){
			touchX = (int) event.getX();
			touchY = (int) event.getY();
			
			float deltaY = (touchY - line.getY());
			float deltaX = (touchX - line.getX());
			
			float distance = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			line.xVelocity = (float) ((deltaX/distance)*5);
			line.yVelocity = (float) ((deltaY/distance)*5);
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
		if(!running)
			return;
		
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
			
			drawCall(gameCanvas);
		}
	}
}