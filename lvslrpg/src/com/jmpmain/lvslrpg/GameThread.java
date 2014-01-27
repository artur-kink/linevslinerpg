package com.jmpmain.lvslrpg;

import java.util.Vector;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Main game thread.
 * All game logic is managed here.
 */
public class GameThread extends Thread{
	private SurfaceHolder surfaceHolder;
	private GameSurface gameSurface;
	
	public GameThread(SurfaceHolder holder, GameSurface surface){
		surfaceHolder = holder;
		gameSurface = surface;
	}
	
	/**
	 * Screen touch handler.
	 */
	public static void onTouchEvent(MotionEvent event){
	}
	
	private boolean running;

	public void setRunning(boolean r){
		running = r;
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
}