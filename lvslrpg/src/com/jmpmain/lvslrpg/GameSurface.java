package com.jmpmain.lvslrpg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Surface view on which game is drawn.
 */
public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

	private GameThread thread;

	/** Counter for fps. */
	private int drawCallCount;
	
	/** The fps of the previous second. */
	private int fps;
	
	/** Last time fps second had elapsed. */
	private long lastDrawCallReset;
	
	public GameSurface(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		thread = new GameThread(getHolder(), this);
		
		drawCallCount = 0;
		lastDrawCallReset = 0;
		fps = 0;
		
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
		drawCallCount++;
		if(System.currentTimeMillis() - lastDrawCallReset > 1000){
			lastDrawCallReset = System.currentTimeMillis();
			fps = drawCallCount;
			drawCallCount = 0;
		}
		
		canvas.drawText("" + fps, 1, 1, new Paint());
	}
	
}