package com.jmpmain.lvslrpg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Surface view on which game is drawn.
 */
public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

	/** Game thread. */
	public GameThread thread;

	/** Counter for fps. */
	private int drawCallCount;
	
	/** The fps of the previous second. */
	private int fps;
	
	/** Last time fps second had elapsed. */
	private long lastDrawCallReset;
	
	/** Default paint handle. */
	private Paint paint;
	
	public GameSurface(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		//Initialize thread.
		thread = new GameThread(getHolder(), this);
		
		//Setup fps variables.
		drawCallCount = 0;
		lastDrawCallReset = 0;
		fps = 0;
		
		paint = new Paint();
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		if(thread.map == null){
			thread.map = MapGenerator.GenerateMap(getWidth(), getHeight(), 12);
			thread.line.setMap(thread.map);
			
			for(int i = 0; i < thread.enemies.size(); i++){
				thread.enemies.get(i).setMap(thread.map);
			}
		}
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		thread.setRunning(false);
	}
	
	@Override
	/**
	 * GameSurface draw method. Game is drawn in this method.
	 */
	protected void onDraw(Canvas canvas) {
		
		//Clear screen
		paint.setARGB(255, 0, 0, 0);
		canvas.drawPaint(paint);
		
		//Draw line canvas.
		thread.line.draw(thread.map.lineCanvas);
		for(int i = 0; i < thread.enemies.size(); i++){
			thread.enemies.get(i).draw(thread.map.lineCanvas);
		}
		canvas.drawBitmap(thread.map.lineCanvas.bitmap, getMatrix(), paint);
		
		//Draw turn buttons.
		paint.setARGB(128,  255, 255,  255);
		int radius = (int)((float)this.getWidth()*0.05);
		//Draw buttons.
		canvas.drawCircle(radius + 20, this.getHeight() - (radius + 20), radius, paint);
		canvas.drawCircle(this.getWidth() - (radius + 20), this.getHeight() - (radius + 20), radius, paint);
		
		
		//Debug draw.
		if(BuildConfig.DEBUG){
			
			//Update fps
			drawCallCount++;
			if(System.currentTimeMillis() - lastDrawCallReset > 1000){
				lastDrawCallReset = System.currentTimeMillis();
				fps = drawCallCount;
				drawCallCount = 0;
			}
			
			//Draw fps
			paint.setTextSize(20);
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText("FPS: " + fps, 20, 20, paint);
			
			//Draw ups
			canvas.drawText("UPS: " + thread.ups, 20, 40, paint);
			
			//Draw player health
			canvas.drawText("Health: " + thread.line.health, 20, 60, paint);
			
			//Draw touch position
			canvas.drawRect(thread.touchX - 5,  thread.touchY - 5, thread.touchX + 5,  thread.touchY + 5, paint);
		}
	}
	
}
