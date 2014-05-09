package com.jmpmain.lvslrpg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
	
	/** Application Context. */
	private Context context;
	
	public static Bitmap city;
	public static Bitmap character;
	public static Bitmap coin;
	public static Bitmap dead;
	public static Bitmap enemy;
	public static Bitmap potion;
	public static Bitmap tileset;
	
	public GameSurface(Context context) {
		super(context);
		this.context = context;
		getHolder().addCallback(this);
		
		//Initialize thread.
		thread = new GameThread(getHolder(), this);
		
		//Setup fps variables.
		drawCallCount = 0;
		lastDrawCallReset = 0;
		fps = 0;
		
		paint = new Paint();
		
		dead = BitmapFactory.decodeResource(getResources(), R.drawable.dead);
		city = BitmapFactory.decodeResource(getResources(), R.drawable.city);
		character = BitmapFactory.decodeResource(getResources(), R.drawable.c1);
		coin = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
		enemy = BitmapFactory.decodeResource(getResources(), R.drawable.c2);
		potion = BitmapFactory.decodeResource(getResources(), R.drawable.potion);
		tileset = BitmapFactory.decodeResource(getResources(), R.drawable.tileset);
				
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		if(!thread.isAlive()){
			thread.initGame();
			thread.setRunning(true);
			thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
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
		thread.line.drawBackground(thread.map.lineCanvas);

		for(int i = 0; i < thread.enemies.size(); i++){
			thread.enemies.get(i).drawBackground(thread.map.lineCanvas);
		}
		canvas.drawBitmap(thread.map.lineCanvas.bitmap, getMatrix(), paint);

		//Draw city.
		canvas.drawBitmap(city, new Rect(0, 0, 64, 64),
				new Rect(thread.map.city.x, thread.map.city.y, thread.map.city.x + 64, thread.map.city.y + 64), paint);
		
		thread.line.draw(canvas);
		
		for(int i = 0; i < thread.enemies.size(); i++){
			thread.enemies.get(i).draw(canvas);
		}
		
		for(int i = 0; i < thread.items.size(); i++){
			thread.items.get(i).draw(canvas);
		}
		
		for(int i = 0; i < thread.particles.size(); i++){
			thread.particles.get(i).draw(canvas);
		}
		
		canvas.drawBitmap(coin, 0, 0, paint);
		paint.setTextSize(32);
		paint.setTypeface(MainActivity.pixelFont);
		paint.setARGB(255, 255, 217, 00);
		canvas.drawText("Coins: " + thread.coinCounter, 32, 32, paint);
		
		canvas.drawText("Level: " + thread.level, 32 + paint.measureText("Coins: " + thread.coinCounter + " "), 32, paint);
		
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
			paint.setARGB(128, 255, 0, 0);
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
