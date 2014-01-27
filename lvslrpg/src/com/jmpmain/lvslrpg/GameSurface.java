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
	
	private LineCanvas lineCanvas;
	
	
	public GameSurface(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		//Initialize thread.
		thread = new GameThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
		
		//Setup fps variables.
		drawCallCount = 0;
		lastDrawCallReset = 0;
		fps = 0;
		
		lineCanvas = new LineCanvas();
		
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		thread.setRunning(true);
	}
	
	@Override
	/**
	 * GameSurface draw method. Game is drawn in this method.
	 */
	protected void onDraw(Canvas canvas) {
		//Debug draw.
		if(BuildConfig.DEBUG){
			
			//Update fps
			drawCallCount++;
			if(System.currentTimeMillis() - lastDrawCallReset > 1000){
				lastDrawCallReset = System.currentTimeMillis();
				fps = drawCallCount;
				drawCallCount = 0;
			}
			
			Paint paint = new Paint();
			paint.setARGB(255, 0, 0, 0);
			canvas.drawPaint(paint);
			
			//Draw fps
			paint.setTextSize(20);
			paint.setARGB(255, 255, 0, 0);
			canvas.drawText("FPS: " + fps, 20, 20, paint);
			
			//Draw ups
			canvas.drawText("UPS: " + thread.ups, 20, 40, paint);
			
			thread.line.draw(lineCanvas);
			canvas.drawBitmap(lineCanvas.bitmap, getMatrix(), paint);
		}
	}
	
}
