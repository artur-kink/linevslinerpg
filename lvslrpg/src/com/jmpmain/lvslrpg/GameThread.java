package com.jmpmain.lvslrpg;

import java.util.ArrayList;
import java.util.Vector;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;

import com.jmpmain.lvslrpg.Map.TileType;
import com.jmpmain.lvslrpg.entities.*;
import com.jmpmain.lvslrpg.entities.Item.ItemType;
import com.jmpmain.lvslrpg.particles.Particle;
import com.jmpmain.lvslrpg.particles.Smoke;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
import android.widget.TextView;

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
	private Button resumeButton;
	private Button highscoresButton;
	private Button achievementsButton;
	private Button optionsButton;
	
	//Options screen ui elements.
	private RelativeLayout optionsScreen;
	private Spinner controlsSpinner;
	private Button audioButton;
	
	//Menu screen ui elements.
	private RelativeLayout menuScreen;
	private Button continueButton;
	
	//Battle screen ui elements.
	private ImageButton leftButton;
	private ImageButton rightButton;
	
	/** Thread running state. */
	public boolean running;
	
	public enum Controls{
		Button_Static(0),
		Button_Clockwise(1),
		Swipe(2),
		Tap(3),
		Tilt(4);
		
		private final int value;
	    private Controls(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
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
	
	public boolean gameExists;
	public int coinCounter;
	public int enemiesKilledCounter;
	
	public int mapDamageCounter;
	public int mapEnemiesKilledCounter;
	
	/** View for ads. */
	private AdView adView;
	
	public static GameThread instance;
	
	public int level;
	
	public GameThread(SurfaceHolder holder, GameSurface surface){
		gameSurface = surface;
		surfaceHolder = holder;

		instance = this;
		
		SharedPreferences settings = MainActivity.context.getSharedPreferences("settings", 0);
		SoundOn = settings.getBoolean("sound", true);
		if(BuildConfig.DEBUG)
			gameControls = Controls.values()[settings.getInt("controls", 0)];
		else
			gameControls = Controls.values()[settings.getInt("controls", 2)];
		
		MainActivity.registerred = settings.getBoolean("registerred", false);
		if(MainActivity.registerred)
			MainActivity.context.loggedIn();
		
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
	    //Add test devices if in debug mode.
	    if(BuildConfig.DEBUG){
		    for(int i = 0; i < MainActivity.context.getResources().getStringArray(R.array.TestDevices).length; i++){
		    	adBuilder.addTestDevice(MainActivity.context.getResources().getStringArray(R.array.TestDevices)[i]);
		    }
	    }
	    AdRequest adRequest = adBuilder.build();

	    //Load ad
	    if(adRequest != null){
	    	adView.loadAd(adRequest);
	    }
	    adView.setId(1);
		
	    LayoutInflater inflater = (LayoutInflater)MainActivity.context.getSystemService(MainActivity.context.LAYOUT_INFLATER_SERVICE );
	    startScreen = (RelativeLayout) inflater.inflate(R.layout.start_screen, uiLayout);
	    
	    TextView gameTitle = (TextView) startScreen.findViewById(R.id.game_title);
	    
	    startButton = (Button) startScreen.findViewById(R.id.start_button);
	    startButton.setOnClickListener(this);
		
	    resumeButton = (Button) startScreen.findViewById(R.id.resume_button);
	    resumeButton.setOnClickListener(this);
	    
	    highscoresButton = (Button) startScreen.findViewById(R.id.highscores_button);
	    highscoresButton.setOnClickListener(this);
	    
	    achievementsButton = (Button) startScreen.findViewById(R.id.achievements_button);
	    achievementsButton.setOnClickListener(this);
	    
		optionsButton = (Button) startScreen.findViewById(R.id.options_button);
		optionsButton.setOnClickListener(this);
		
		
		optionsScreen = (RelativeLayout) inflater.inflate(R.layout.options_screen, uiLayout);
		
		controlsSpinner = (Spinner) optionsScreen.findViewById(R.id.controls_spinner);
		OptionsAdapter adapter = new OptionsAdapter(MainActivity.context, R.layout.spinner_item_layout,
				MainActivity.context.getResources().getStringArray(R.array.ControlsOptions));
		// Apply the adapter to the spinner
		controlsSpinner.setAdapter(adapter);
		controlsSpinner.setSelection(gameControls.getValue());
		controlsSpinner.setOnItemSelectedListener(this);
		
		TextView controlsLabel = (TextView) optionsScreen.findViewById(R.id.controls_label);
		
		audioButton = (Button) optionsScreen.findViewById(R.id.audio_button);
		audioButton.setOnClickListener(this);
		if(!SoundOn){
			audioButton.setText("Sound Off");
		}
		
		menuScreen = (RelativeLayout) inflater.inflate(R.layout.menu_screen, uiLayout);
		continueButton = (Button) menuScreen.findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		
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
		resumeButton.setTypeface(MainActivity.pixelFont);
		optionsButton.setTypeface(MainActivity.pixelFont);
		highscoresButton.setTypeface(MainActivity.pixelFont);
		achievementsButton.setTypeface(MainActivity.pixelFont);
		continueButton.setTypeface(MainActivity.pixelFont);
		audioButton.setTypeface(MainActivity.pixelFont);
		
		gameTitle.setTypeface(MainActivity.pixelFont);
		controlsLabel.setTypeface(MainActivity.pixelFont);
		
		gameExists = false;
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
		coinCounter = 0;
		enemiesKilledCounter = 0;
		
		setTurnButtons();
		
		newLevel();
	}
	
	/**
	 * Creates new level.
	 */
	public void newLevel(){
		gameExists = true;
		level++;
		
		if(level == 10){
			MainActivity.context.giveAchievement(R.string.achievement_adventurer);
		}else if(level == 25){
			MainActivity.context.giveAchievement(R.string.achievement_explorer);
		}else if(level == 25){
			MainActivity.context.giveAchievement(R.string.achievement_just_lucky);
		}
		
		if(mapEnemiesKilledCounter >= 4){
			MainActivity.context.giveAchievement(R.string.achievement_clearing_the_fields);
		}
		mapEnemiesKilledCounter = 0;
		
		if(mapDamageCounter > 10){
			MainActivity.context.giveAchievement(R.string.achievement_meatshield);
		}
		mapDamageCounter = 0;
		
		if(level > 1){
			MainActivity.context.submitScore(R.string.leaderboard_furthest_level, level);
			
			for(int i = 0; i < enemies.size(); i++){
				if(enemies.get(i).dead){
					enemiesKilledCounter++;
					mapEnemiesKilledCounter++;
				}
			}
		}
		
		map = MapGenerator.GenerateMap(gameSurface.getWidth(), gameSurface.getHeight(), 16);
		
		line.setMap(map);
		line.setDirection(0, -1);
		line.setX(map.playerStart.x);
		line.setY(map.playerStart.y);
		
		enemies = new Vector<LineEntity>();
		
		for(int i = 0; i < map.enemyStarts.size(); i++){
			LineEntity enemy = new AILineEntity(map.enemyStarts.get(i).x, map.enemyStarts.get(i).y);
			enemy.setColor(155, Math.max(128, (int)(Math.random()*255)), (int)(Math.random()*255), Math.max(64, (int)(Math.random()*255)));
			enemy.setDirection(0, 1);
			enemy.setMap(map);
			enemy.setMaxHealth(enemy.maxHealth + level);
			enemy.character = GameSurface.enemy;
			enemies.add(enemy);
		}
		
		items = new Vector<Item>();
		int numItems = (int) Math.max(4, Math.random()*10);
		for(int i = 0; i < numItems; i++){
			int x = 0;
			int y = 0;
			//Ensure item is reachable
			do{
				x = (int)(Math.random()*(map.width*0.9) + map.width*0.05);
				y = (int)(Math.random()*(map.height*0.9) + map.height*0.05);
			}while(map.getDamage(x, y) == true);
			x = x*map.tileSize;
			y = y*map.tileSize;
					
			if(Math.random() >= 0.25)
				items.add(new Item(ItemType.Coin, x, y));
			else
				items.add(new Item(ItemType.Potion, x, y));
		}
		
		particles.clear();
		for(int i = 0; i < 20; i++){
			particles.add(new Smoke((int) (map.city.x + 55 + Math.random()*3), (int) (map.city.y + Math.random()*3), 0));
		}
	}
	
	/**
	 * Called when the game can be initialized.
	 * This means the graphics and application has been setup
	 * and ready to be used.
	 */
	public void initGame(){
		setScreen(Screen.START);
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
			
			//Check if game state should be updated.
			if(System.currentTimeMillis() - lastUpdate <= 33){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				continue;
			}
			lastUpdate = System.currentTimeMillis();
			
			
			if(currentScreen == Screen.MENU){
				
			}
			else if(currentScreen == Screen.BATTLE){
				
				long currentTimeMillis = System.currentTimeMillis();
				
				//Update debug parameters.
				if(BuildConfig.DEBUG){				
					updateCallCount++;
					if(System.currentTimeMillis() - lastUpdateCallReset >= 1000){
						lastUpdateCallReset = System.currentTimeMillis();
						ups = updateCallCount;
						updateCallCount = 0;
					}
				}
				
				int lineHealth = line.health;
				line.update(currentTimeMillis);
				mapDamageCounter += lineHealth - line.health;
				
				//Check for item pickups
				for(int i = 0; i < items.size(); i++){
					if(new Rect((int)line.getX()*map.tileSize - 16, (int)line.getY()*map.tileSize- 16, (int)line.getX()*map.tileSize+16, (int)line.getY()*map.tileSize+16).intersect(
							new Rect(items.get(i).x, items.get(i).y, items.get(i).x + items.get(i).width, items.get(i).y + items.get(i).height))){
						
						if(items.get(i).type == ItemType.Potion){
							line.addHealth(3);
							AudioPlayer.playSound(AudioPlayer.potion);
						}else if(items.get(i).type == ItemType.Coin){
							AudioPlayer.playSound(AudioPlayer.coin);
							coinCounter++;
							
							if(coinCounter == 100){
								MainActivity.context.giveAchievement(R.string.achievement_bag_o_coin);
							}
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
					boolean dead = enemies.get(i).dead;
					enemies.get(i).update(currentTimeMillis);
					if(!dead && enemies.get(i).dead){
						items.add(new Item(ItemType.Coin, (int)enemies.get(i).getX()*map.tileSize, (int)enemies.get(i).getY()*map.tileSize));
					}
				}
				
				if(line.dead){
					
					for(int i = 0; i < enemies.size(); i++){
						if(enemies.get(i).dead){
							enemiesKilledCounter++;
							mapEnemiesKilledCounter++;
						}
					}
					
					MainActivity.context.submitScore(R.string.leaderboard_coins_collected, coinCounter);
					MainActivity.context.submitScore(R.string.leaderboard_enemies_killed, enemiesKilledCounter);
					
					gameExists = false;
					setScreen(Screen.START);
				}
				
				//Update particles.
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
		 			
		 			if(!gameExists){
		 				resumeButton.setVisibility(View.INVISIBLE);
		 			}else{
		 				resumeButton.setVisibility(View.VISIBLE);
		 			}
		 			
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
		 			
		 			uiLayout.addView(menuScreen, new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		 		}
		 		else if(currentScreen == Screen.BATTLE){
		 			if(gameControls == Controls.Button_Clockwise || gameControls == Controls.Button_Static){
		 				int buttonSize = (int) Math.max(32, ((float)gameSurface.getWidth())*0.125);
			 			LayoutParams params = new LayoutParams(buttonSize, buttonSize);
			 			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			 			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			 			uiLayout.addView(rightButton, params);
			 			
			 			params = new LayoutParams(buttonSize, buttonSize);
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
	 * Save game settings.
	 */
	public void saveSettings(){
		SharedPreferences settings = MainActivity.context.getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("sound", SoundOn);
		editor.putBoolean("registerred", MainActivity.registerred);
		editor.putInt("controls", gameControls.getValue());
		editor.commit();
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
			}else if(v == resumeButton){
				setScreen(Screen.BATTLE);
			}else if(v == highscoresButton){
				((MainActivity)MainActivity.context).openHighscores();
			}else if(v == achievementsButton){
				((MainActivity)MainActivity.context).openAchievements();
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
				saveSettings();
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
			gameControls = Controls.Tap;
		}else if(id == 4){
			gameControls = Controls.Tilt;
		}
		saveSettings();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	

	/**
	 * Screen touch handler.
	 */
	public void onTouchEvent(MotionEvent event){
		
		if(currentScreen != Screen.BATTLE)
			return;
		
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
		}else if(gameControls == Controls.Tap && event.getAction() == MotionEvent.ACTION_DOWN){
			//Tap control.
			int xDelta = (int) (line.getX()*map.tileSize) - touchX;
			int yDelta = (int) (line.getY()*map.tileSize) - touchY;
			
			//If player is moving in Y direction and tap was in x direction.
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
		
		if(currentScreen != Screen.BATTLE)
			return;
		
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