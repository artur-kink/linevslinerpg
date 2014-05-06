package com.jmpmain.lvslrpg;

import java.util.ArrayList;
import java.util.Vector;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.jmpmain.lvslrpg.Map.TileType;
import com.jmpmain.lvslrpg.entities.*;
import com.jmpmain.lvslrpg.entities.Item.ItemType;
import com.jmpmain.lvslrpg.particles.Particle;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;

/**
 * Main game thread.
 * All game logic is managed here.
 */
public class GameThread extends Thread
	implements SensorEventListener, OnClickListener, OnItemSelectedListener{
	
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
		OPTIONS,
		MENU,
		BATTLE
	}
	
	/** Current screen of game. */
	public Screen currentScreen;
	
	//Start screen ui elements.
	private RelativeLayout startScreen;
	private Button startButton;
	private Button optionsButton;
	
	//Options screen ui elements.
	private RelativeLayout optionsScreen;
	private Spinner controlsSpinner;
	private Button audioButton;
	
	//Menu screen ui elements.
	private Button continueButton;
	
	//Battle screen ui elements.
	private ImageButton leftButton;
	private ImageButton rightButton;
	
	/** Thread running state. */
	public boolean running;
	
	public enum Controls{
		Button_Static,
		Button_Clockwise,
		Swipe,
		Tilt
	}
	
	public Controls gameControls;
	
	public static boolean SoundOn;
	
	public int startTouchX;
	public int startTouchY;
	
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
	
	public int level;
	
	public GameThread(SurfaceHolder holder, GameSurface surface){
		gameSurface = surface;
		surfaceHolder = holder; 
		
		instance = this;
		
		gameControls = Controls.Button_Static;
		SoundOn = true;
		
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
	    adView.setId(1);
		
	    LayoutInflater inflater = (LayoutInflater)MainActivity.context.getSystemService(MainActivity.context.LAYOUT_INFLATER_SERVICE );
	    startScreen = (RelativeLayout) inflater.inflate(R.layout.start_screen, uiLayout);
	    
	    startButton = (Button) startScreen.findViewById(R.id.start_button);
	    startButton.setOnClickListener(this);
		
		optionsButton = (Button) startScreen.findViewById(R.id.options_button);
		optionsButton.setOnClickListener(this);
		
		
		optionsScreen = (RelativeLayout) inflater.inflate(R.layout.options_screen, uiLayout);
		
		controlsSpinner = (Spinner) optionsScreen.findViewById(R.id.controls_spinner);
		OptionsAdapter adapter = new OptionsAdapter(MainActivity.context, R.layout.spinner_item_layout,
				MainActivity.context.getResources().getStringArray(R.array.ControlsOptions));
		// Apply the adapter to the spinner
		controlsSpinner.setAdapter(adapter);
		controlsSpinner.setOnItemSelectedListener(this);
		
		audioButton = (Button) optionsScreen.findViewById(R.id.audio_button);
		audioButton.setOnClickListener(this);
		
		
		continueButton = new Button(MainActivity.context);
		continueButton.setOnClickListener(this);
		continueButton.setText("Continue");
		
		leftButton = new ImageButton(MainActivity.context);
		leftButton.setOnClickListener(this);
		leftButton.setImageResource(R.drawable.arrow_l);
		leftButton.setScaleType(ScaleType.FIT_CENTER);
		leftButton.setBackgroundColor(Color.TRANSPARENT);
		
		
		rightButton = new ImageButton(MainActivity.context);
		rightButton.setOnClickListener(this);
		rightButton.setImageResource(R.drawable.arrow_r);
		rightButton.setScaleType(ScaleType.FIT_CENTER);
		rightButton.setBackgroundColor(Color.TRANSPARENT);
		
		startButton.setTypeface(MainActivity.pixelFont);
		optionsButton.setTypeface(MainActivity.pixelFont);
		continueButton.setTypeface(MainActivity.pixelFont);
		audioButton.setTypeface(MainActivity.pixelFont);
		
		setRunning(false);
	}
	
	/**
	 * Starts a brand new game.
	 */
	public void resetGame(){
		
		line = new PlayerLineEntity(0, 0);
		line.character = GameSurface.character;
		line.setColor(128, 0, 255, 0);
		
		level = 0;
		
		setTurnButtons();
		
		newLevel();
	}
	
	/**
	 * Creates new level.
	 */
	public void newLevel(){
		level++;
		
		map = MapGenerator.GenerateMap(gameSurface.getWidth(), gameSurface.getHeight(), 14);
		
		line.setMap(map);
		line.setDirection(0, -1);
		line.setX(map.playerStart.x);
		line.setY(map.playerStart.y);
		
		enemies = new Vector<LineEntity>();
		
		for(int i = 0; i < map.enemyStarts.size(); i++){
			LineEntity enemy = new AILineEntity(map.enemyStarts.get(i).x, map.enemyStarts.get(i).y);
			enemy.setColor(128, (int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
			enemy.setDirection(0, 1);
			enemy.setMap(map);
			enemy.setMaxHealth(enemy.maxHealth + level);
			enemy.character = GameSurface.enemy;
			enemies.add(enemy);
		}
		
		items = new Vector<Item>();
		for(int i = 0; i < 10; i++){
			int x = (int)(Math.random()*map.width*map.tileSize);
			int y = (int)(Math.random()*map.height*map.tileSize);
			if(Math.random() > 0.5)
				items.add(new Item(ItemType.Coin, x, y));
			else
				items.add(new Item(ItemType.Potion, x, y));
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
		 			LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 			uiLayout.addView(adView, params);
		 			
		 			uiLayout.addView(startScreen, new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		 		}
		 		else if(currentScreen == Screen.OPTIONS){
		 			
		 			LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 			uiLayout.addView(adView, params);
		 			
		 			uiLayout.addView(optionsScreen, new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
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
		 			if(gameControls == Controls.Button_Clockwise || gameControls == Controls.Button_Static){
			 			LayoutParams params = new LayoutParams(150, 150);
			 			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			 			uiLayout.addView(rightButton, params);
			 			
			 			params = new LayoutParams(150, 150);
			 			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			 			uiLayout.addView(leftButton, params);
			 			setTurnButtons();
		 			}
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
				
				//Check for item pickups
				for(int i = 0; i < items.size(); i++){
					if(new Rect((int)line.getX()*map.tileSize - 16, (int)line.getY()*map.tileSize- 16, (int)line.getX()*map.tileSize+16, (int)line.getY()*map.tileSize+16).intersect(
							new Rect(items.get(i).x, items.get(i).y, items.get(i).x + items.get(i).width, items.get(i).y + items.get(i).height))){
						
						if(items.get(i).type == ItemType.Potion){
							line.addHealth(5);
							AudioPlayer.playSound(AudioPlayer.potion);
						}else if(items.get(i).type == ItemType.Coin){
							AudioPlayer.playSound(AudioPlayer.coin);
						}
						
						items.remove(i);
						i--;
					}
				}
				
				//Check if player entered city.
				if(new Rect((int)line.getX()*map.tileSize - 16, (int)line.getY()*map.tileSize - 16, (int)line.getX()*map.tileSize+16, (int)line.getY()*map.tileSize+16).intersect(
						new Rect(map.city.x, map.city.y, map.city.x + 64, map.city.y + 64))){
					AudioPlayer.playSound(AudioPlayer.city);
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

	/*************************
	 * Input Methods.
	 ************************/
	
	/**
	 * Set turning buttons to show correct turning
	 * images depending on player movement.
	 */
	public void setTurnButtons(){
		//Reset all previous transforms.
		leftButton.setRotation(0);
		rightButton.setRotation(0);
		leftButton.setScaleX(1);
		rightButton.setScaleX(1);
		leftButton.setScaleY(1);
		rightButton.setScaleY(1);
		
		if(gameControls == Controls.Button_Static){
			if(line.getYVelocity() > 0){
				leftButton.setRotation(0);
				leftButton.setScaleY(-1);
				rightButton.setRotation(0);
				rightButton.setScaleY(-1);
			}else if(line.getYVelocity() < 0){
				rightButton.setRotation(0);
				leftButton.setRotation(0);
			}else if(line.getXVelocity() > 0){
				leftButton.setScaleY(-1);
				rightButton.setScaleY(-1);
				leftButton.setRotation(270);
				rightButton.setRotation(270);
			}else if(line.getXVelocity() < 0){
				rightButton.setRotation(270);
				leftButton.setRotation(270);
			}
		}else if(gameControls == Controls.Button_Clockwise){
			if(line.getYVelocity() > 0){
				leftButton.setRotation(180);
				rightButton.setRotation(180);
			}else if(line.getYVelocity() < 0){
				rightButton.setRotation(0);
				leftButton.setRotation(0);
			}else if(line.getXVelocity() > 0){
				leftButton.setRotation(90);
				rightButton.setRotation(90);
			}else if(line.getXVelocity() < 0){
				rightButton.setRotation(270);
				leftButton.setRotation(270);
			}
		}
	}
	
	/**
	 * Back button pressed handler.
	 * @return true if back was handled, else false.
	 */
	public boolean onBackPressed(){
		if(currentScreen == Screen.START){
			return false;
		}else if(currentScreen == Screen.OPTIONS){
			setScreen(Screen.START);
		}else if(currentScreen == Screen.BATTLE){
			setScreen(Screen.START);
		}else if(currentScreen == Screen.MENU){
			setScreen(Screen.START);
		}
		return true;
	}
	
	@Override
	/**
	 * Button clicked handler.
	 */
	public void onClick(View v) {
		if(currentScreen == Screen.START){
			if(v == startButton){
				resetGame();
				setScreen(Screen.BATTLE);
			}else if(v == optionsButton){
				setScreen(Screen.OPTIONS);
			}
		}
		else if(currentScreen == Screen.OPTIONS){
			if(v == audioButton){
				SoundOn = !SoundOn;
				if(SoundOn){
					audioButton.setText("Sound On");
					AudioPlayer.playSound(AudioPlayer.coin);
				}else{
					audioButton.setText("Sound Off");
				}
			}
		}
		else if(currentScreen == Screen.MENU){
			if(v == continueButton){
				newLevel();
				setScreen(Screen.BATTLE);
			}
		}
		else if(currentScreen == Screen.BATTLE){
			if(v == rightButton){
				if(gameControls == Controls.Button_Static){
					if(line.getYVelocity() != 0){
						line.setDirection(1, 0);
					}else if(line.getXVelocity() != 0){
						line.setDirection(0, -1);
					}
				}else if(gameControls == Controls.Button_Clockwise){
					if(line.getYVelocity() > 0){
						line.setDirection(-1, 0);
					}else if(line.getYVelocity() < 0){
						line.setDirection(1, 0);
					}else if(line.getXVelocity() > 0){
						line.setDirection(0, 1);
					}else if(line.getXVelocity() < 0){
						line.setDirection(0, -1);
					}
				}
				setTurnButtons();
			}else if(v == leftButton){
				if(gameControls == Controls.Button_Static){
					if(line.getYVelocity() != 0){
						line.setDirection(-1, 0);
					}else if(line.getXVelocity() != 0){
						line.setDirection(0, 1);
					}
				}else if(gameControls == Controls.Button_Clockwise){
					if(line.getYVelocity() > 0){
						line.setDirection(1, 0);
					}else if(line.getYVelocity() < 0){
						line.setDirection(-1, 0);
					}else if(line.getXVelocity() > 0){
						line.setDirection(0, -1);
					}else if(line.getXVelocity() < 0){
						line.setDirection(0, 1);
					}
				}
				setTurnButtons();
			}
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if(id == 0){
			gameControls = Controls.Button_Static;
		}else if(id == 1){
			gameControls = Controls.Button_Clockwise;
		}else if(id == 2){
			gameControls = Controls.Swipe;
		}else if(id == 3){
			gameControls = Controls.Tilt;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	

	/**
	 * Screen touch handler.
	 */
	public void onTouchEvent(MotionEvent event){
		
		//Record where touch began, used for swiping.
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			startTouchX = (int) event.getX();
			startTouchY = (int) event.getY();
		}
		
		//Record current touch location.
		if(event.getAction() == MotionEvent.ACTION_DOWN ||
			event.getAction() == MotionEvent.ACTION_MOVE){
			touchX = (int) event.getX();
			touchY = (int) event.getY();
		}
		
		//Handle swipe input if playing with swipe controls.
		if(gameControls == Controls.Swipe && event.getAction() == MotionEvent.ACTION_UP){
			
			int xDelta = startTouchX - (int) event.getX();
			int yDelta = startTouchY - (int) event.getY();
			
			//If player is moving in Y direction and swipe was in x direction.
			if(line.getYVelocity() != 0 && Math.abs(xDelta) > Math.abs(yDelta)){
				line.setDirection(-Math.signum(xDelta), 0);
			}else if(line.getXVelocity() != 0 && Math.abs(yDelta) > Math.abs(xDelta)){
				//If player is moving in X direction and swipe was in y direction.
				line.setDirection(0, -Math.signum(yDelta));
			}
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	/**
	 * Handle tilt input if playing with tilt controls.
	 */
	public void onSensorChanged(SensorEvent event) {
		if(gameControls == Controls.Tilt){
			float x = event.values[0];
			float y = event.values[1];
			
			if(Math.abs(x) >= 0.5 && line.getYVelocity() != 0 && Math.abs(x) > Math.abs(y)){
				line.setDirection(-Math.signum(x), 0);
			}else if(Math.abs(y) >= 0.5 && line.getXVelocity() != 0 && Math.abs(x) < Math.abs(y)){
				line.setDirection(0, Math.signum(y));
			}
			
		}
	}
}