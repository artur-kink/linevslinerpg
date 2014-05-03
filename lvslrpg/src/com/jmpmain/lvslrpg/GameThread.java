package com.jmpmain.lvslrpg;

import java.util.Vector;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.jmpmain.lvslrpg.Map.TileType;
import com.jmpmain.lvslrpg.entities.*;
import com.jmpmain.lvslrpg.particles.Particle;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

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
	public RelativeLayout uiLayout;
	
	/** List of screen types. */
	public enum Screen{
		START,
		MENU,
		BATTLE
	}
	
	/** Current screen of game. */
	public Screen currentScreen;
	
	//Start screen ui elements.
	private Button startButton;
	
	//Menu screen ui elements.
	private Button continueButton;
	
	//Battle screen ui elements.
	private ImageButton leftButton;
	private ImageButton rightButton;
	private Button resetButton;
	
	/** Thread running state. */
	public boolean running;
	
	public int touchX;
	public int touchY;

	/** Current game map. */
	public Map map;
	
	public LineEntity line;
	public Vector<LineEntity> enemies;
	public Vector<Item> items;
	
	public Vector<Item> playerItems;
	
	public Vector<Particle> particles;
	
	/** View for ads. */
	private AdView adView;
	
	public static GameThread instance;
	
	public GameThread(SurfaceHolder holder, GameSurface surface){
		gameSurface = surface;
		surfaceHolder = holder; 
		
		instance = this;
		
		updateCallCount = 0;
		ups = 0;
		lastUpdateCallReset = 0;
		
		particles = new Vector<Particle>();
		
		//Create UI elements.
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
		
		startButton = new Button(MainActivity.context);
		startButton.setOnClickListener(this);
		startButton.setText("Start");
		
		continueButton = new Button(MainActivity.context);
		continueButton.setOnClickListener(this);
		continueButton.setText("Continue");
		
		leftButton = new ImageButton(MainActivity.context);
		leftButton.setOnClickListener(this);
		leftButton.setImageResource(R.drawable.arrow);
		
		rightButton = new ImageButton(MainActivity.context);
		rightButton.setOnClickListener(this);
		rightButton.setImageResource(R.drawable.arrow);
		
		resetButton = new Button(MainActivity.context);
		resetButton.setOnClickListener(this);

		setRunning(false);
	}
	
	public void resetGame(){
		
		map = MapGenerator.GenerateMap(gameSurface.getWidth(), gameSurface.getHeight(), 14);
		
		line = new PlayerLineEntity(map.playerStart.x, map.playerStart.y);
		line.setDirection(0, -1);
		line.setColor(128, 0, 255, 0);
		line.setMap(map);
		
		setTurnButtons();
		
		enemies = new Vector<LineEntity>();
		
		for(int i = 0; i < map.enemyStarts.size(); i++){
			LineEntity enemy = new AILineEntity(map.enemyStarts.get(i).x, map.enemyStarts.get(i).y);
			enemy.setColor(128, (int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
			enemy.setMap(map);
			enemies.add(enemy);
		}
		
		items = new Vector<Item>();
		for(int i = 0; i < 10; i++){
			int x = (int)(Math.random()*map.width*map.tileSize);
			int y = (int)(Math.random()*map.height*map.tileSize);
			items.add(new Item(x, y));
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
		     @SuppressWarnings("deprecation")
			 @Override
		     public void run() {
		    	 uiLayout.removeAllViews();
		 		
		 		if(currentScreen == Screen.START){
		 			LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 			uiLayout.addView(adView, params);
		 			
		 			params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 			params.addRule(RelativeLayout.CENTER_VERTICAL);
		 			uiLayout.addView(startButton, params);
		 			
		 		}
		 		else if(currentScreen == Screen.MENU){
		 			LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 			uiLayout.addView(adView, params);
		 			
		 			params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 			params.addRule(RelativeLayout.CENTER_VERTICAL);
		 			uiLayout.addView(continueButton, params);
		 		}
		 		else if(currentScreen == Screen.BATTLE){
		 			LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 			uiLayout.addView(resetButton, params);
		 			
		 			params = new LayoutParams(150, 150);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 			uiLayout.addView(rightButton, params);
		 			
		 			params = new LayoutParams(150, 150);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 			uiLayout.addView(leftButton, params);
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
			
			if(currentScreen == Screen.MENU){
				
			}
			else if(currentScreen == Screen.BATTLE){
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
				
				for(int i = 0; i < items.size(); i++){
					if(new Rect((int)line.getX()*map.tileSize - 16, (int)line.getY()*map.tileSize- 16, (int)line.getX()*map.tileSize+16, (int)line.getY()*map.tileSize+16).intersect(
							new Rect(items.get(i).x, items.get(i).y, items.get(i).x + items.get(i).width, items.get(i).y + items.get(i).height))){
						items.remove(i);
						i--;
					}
				}
				
				//Check if player entered city.
				if(new Rect((int)line.getX()*map.tileSize, (int)line.getY()*map.tileSize, (int)line.getX()*map.tileSize+32, (int)line.getY()*map.tileSize+32).intersect(
						new Rect(map.city.x, map.city.y, map.city.x + 32, map.city.y + 32))){
					setScreen(Screen.MENU);
				}
				
				for(int i = 0; i < enemies.size(); i++){
					enemies.get(i).update(currentTimeMillis);
				}
				
				if(line.dead){
					setScreen(Screen.START);
				}
				
				for(int i = 0; i < particles.size(); i++){
					if(particles.get(i).destroy == true){
						particles.remove(i);
						i--;
					}
				}
				for(int i = 0; i < particles.size(); i++){
					particles.get(i).update(currentTimeMillis);
				}
				
				drawCall(gameCanvas);
			}
		}
	}

	public void setTurnButtons(){
		if(line.getYVelocity() != 0){
			leftButton.setRotation(180);
			rightButton.setRotation(0);
		}else if(line.getXVelocity() != 0){
			rightButton.setRotation(270);
			leftButton.setRotation(90);
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
		else if(currentScreen == Screen.MENU){
			if(v == continueButton){
				initGame();
				setScreen(Screen.BATTLE);
			}
		}
		else if(currentScreen == Screen.BATTLE){
			if(v == rightButton){
				if(line.getYVelocity() != 0){
					line.setDirection(1, 0);
				}else if(line.getXVelocity() != 0){
					line.setDirection(0, -1);
				}
				setTurnButtons();
			}else if(v == leftButton){
				if(line.getYVelocity() != 0){
					line.setDirection(-1, 0);
				}else if(line.getXVelocity() != 0){
					line.setDirection(0, 1);
				}
				setTurnButtons();
			}else if(v == resetButton){
				resetGame();
			}
		}
	}
}