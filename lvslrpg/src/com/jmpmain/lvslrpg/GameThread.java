package com.jmpmain.lvslrpg;

import java.util.Vector;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.jmpmain.lvslrpg.entities.*;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;

/**
 * Main game thread.
 * All game logic is managed here.
 */
public class GameThread extends Thread
	implements SensorEventListener, OnClickListener{
	
	/** Counter for updates per second(ups). */
	private int updateCallCount;
	
	/** The updates per second of the previous second. */
	public int ups;
	
	/** Last time fps second had elapsed. */
	private long lastUpdateCallReset;
	
	/** Last time game was updated. */
	private long lastUpdate;
	
	/** GameSurface Surface Holder. */
	private SurfaceHolder surfaceHolder;
	
	/** Main surface game is drawn to. */
	private GameSurface gameSurface;
	
	/** Layout for UI elements. Above the game surface. */
	public AbsoluteLayout uiLayout;
	
	/** List of screen types. */
	public enum Screen{
		START,
		BATTLE
	}
	
	/** Current screen of game. */
	public Screen currentScreen;
	
	private Button startButton;
	
	private Button leftButton;
	private Button rightButton;
	private Button resetButton;
	
	/** Thread running state. */
	public boolean running;
	
	public int touchX;
	public int touchY;

	/** Current game map. */
	public Map map;
	
	public LineEntity line;
	public Vector<LineEntity> enemies;

	/** View for ads. */
	private AdView adView;
	
	public GameThread(SurfaceHolder holder, GameSurface surface){
		gameSurface = surface;
		surfaceHolder = holder; 
		
		updateCallCount = 0;
		ups = 0;
		lastUpdateCallReset = 0;
		
		//Create UI elements.
		startButton = new Button(MainActivity.context);
		startButton.setOnClickListener(this);
		startButton.setText("Start");
		
		leftButton = new Button(MainActivity.context);
		leftButton.setOnClickListener(this);
		
		rightButton = new Button(MainActivity.context);
		rightButton.setOnClickListener(this);
		
		resetButton = new Button(MainActivity.context);
		resetButton.setOnClickListener(this);
		
		rightButton = new Button(MainActivity.context);
		rightButton.setOnClickListener(this);
		
		//Create the ad
	    adView = new AdView(MainActivity.context);
	    adView.setAdSize(AdSize.BANNER);
	    adView.setAdUnitId(MainActivity.context.getResources().getString(R.string.AdId));
	    
	    AdRequest.Builder adBuilder = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
	    for(int i = 0; i < MainActivity.context.getResources().getStringArray(R.array.TestDevices).length; i++){
	    	adBuilder.addTestDevice(MainActivity.context.getResources().getStringArray(R.array.TestDevices)[i]);
	    }
	    AdRequest adRequest = adBuilder.build();

	    //Load ad
	    if(adRequest != null){
	    	adView.loadAd(adRequest);
	    }
	    
		setRunning(false);
	}
	
	public void resetGame(){
		
		map = MapGenerator.GenerateMap(gameSurface.getWidth(), gameSurface.getHeight(), 12);
		
		line = new LineEntity(500/12, 500/12);
		line.setDirection(1, 0);
		line.setColor(128, 0, 255, 0);
		line.setMap(map);
		
		enemies = new Vector<LineEntity>();
		{
			LineEntity enemy = new AILineEntity(10/12, 500/12);
			enemy.setColor(128, 255, 255, 0);
			enemy.setMap(map);
			enemies.add(enemy);
		}
		
		{
			LineEntity enemy = new AILineEntity(500/12, 10/12);
			enemy.setColor(128, 0, 255, 255);
			enemy.setMap(map);
			enemies.add(enemy);
		}
		
		{
			LineEntity enemy = new AILineEntity(500/12, 1500/12);
			enemy.setColor(128, 0, 0, 255);
			enemy.setMap(map);
			enemies.add(enemy);
		}
	}
	
	/**
	 * Called when the game can be initialized.
	 * This means the graphics and application has been setup
	 * and ready to be used.
	 */
	public void initGame(){
		resetGame();
		
		setScreen(Screen.START);
	}
	
	/**
	 * Change screens.
	 * @param screen Screen to change to.
	 */
	public void setScreen(Screen screen){
		currentScreen = screen;
		
		//Make sure running on UI thread.
		((MainActivity)MainActivity.context).runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 uiLayout.removeAllViews();
		 		
		 		if(currentScreen == Screen.START){
		 			uiLayout.addView(adView);
		 			uiLayout.addView(startButton);
		 		}
		 		else if(currentScreen == Screen.BATTLE){
		 			uiLayout.addView(resetButton,
		 					new AbsoluteLayout.LayoutParams(100, 100,
		 						10, 10));
		 			
		 			uiLayout.addView(rightButton,
		 				new AbsoluteLayout.LayoutParams(100, 100,
		 					gameSurface.getWidth() - 100, gameSurface.getHeight() - 100));
		 			
		 			uiLayout.addView(leftButton,
		 					new AbsoluteLayout.LayoutParams(100, 100,
		 						0, gameSurface.getHeight() - 100));
		 		}
		    }
		});
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
			//line.setTarget(event.getX(), event.getY());
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {}
	
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
			
			if(currentScreen == Screen.BATTLE){
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
					enemies.get(i).update(currentTimeMillis);
					if(enemies.get(i).dead){
						enemies.remove(i);
						i--;
					}
				}
				
				if(line.health <= 0){
					setScreen(Screen.START);
				}
				
				drawCall(gameCanvas);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(currentScreen == Screen.START){
			if(v == startButton){
				initGame();
				setScreen(Screen.BATTLE);
			}
		}
		if(currentScreen == Screen.BATTLE){
			if(v == rightButton){
				if(line.getYVelocity() != 0){
					line.setDirection(1, 0);
				}else if(line.getXVelocity() != 0){
					line.setDirection(0, -1);
				}
			}else if(v == leftButton){
				if(line.getYVelocity() != 0){
					line.setDirection(-1, 0);
				}else if(line.getXVelocity() != 0){
					line.setDirection(0, 1);
				}
			}else if(v == resetButton){
				resetGame();
			}
		}
	}
}