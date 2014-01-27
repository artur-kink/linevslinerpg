package com.jmpmain.lvslrpg;

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
	
	private SurfaceHolder surfaceHolder;
	private GameSurface gameSurface;
	
	/**
	 * Thread running state.
	 */
	private boolean running;
	
	public GameThread(SurfaceHolder holder, GameSurface surface){
		surfaceHolder = holder;
		gameSurface = surface;
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
			drawCall(gameCanvas);
			
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
	
}