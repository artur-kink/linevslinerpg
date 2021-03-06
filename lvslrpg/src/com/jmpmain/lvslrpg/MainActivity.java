package com.jmpmain.lvslrpg;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.jmpmain.lvslrpg.GameThread.Screen;

/**
 * The main activity.
 */
public class MainActivity extends BaseGameActivity {
	
	private GameSurface surface;
	private GameThread thread;
	
	public static MainActivity context;
	
	public static Typeface pixelFont;
	
	public static boolean registerred;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		context = this;
		
		
		//Do not log in on startup.
		getGameHelper().setConnectOnStart(false);
		
		//Set as full screen and set main view to GameSurface.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		pixelFont = Typeface.createFromAsset(MainActivity.context.getAssets(), "font/pixelart.ttf");
		
		//Create frame layout to contain game surface and ui layout.
		FrameLayout baseLayout = new FrameLayout(this);
		//Create ui layout.
		RelativeLayout uiLayout = new RelativeLayout(this);
		

		surface = new GameSurface(this);
		thread = surface.thread;
		baseLayout.addView(surface);
		baseLayout.addView(uiLayout);
		
		setContentView(baseLayout);
		
		thread.uiLayout = uiLayout;
		
		//Register accelerator to record tilt of screen.
		((SensorManager)getSystemService(Context.SENSOR_SERVICE)).registerListener(
			surface.thread, ((SensorManager)getSystemService(Context.SENSOR_SERVICE))
			.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);
		
		AudioPlayer.context = this;
		AudioPlayer.initSounds();
	}

	@Override
	public void onPause(){
		super.onPause();
		thread.setScreen(Screen.START);
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onBackPressed (){
		if(thread.onBackPressed() == false){
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    surface.thread.onTouchEvent(event);
	    return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean loggedIn(){
		if(!isSignedIn()){
			beginUserInitiatedSignIn();
		}
		return isSignedIn();
	}
	
	public void giveAchievement(int achievementId){
		if(registerred && isSignedIn())
			Games.Achievements.unlock(getApiClient(), getResources().getString(achievementId));
		
	}
	
	public void submitScore(int scoreId, int score){
		if(registerred && isSignedIn())
			Games.Leaderboards.submitScore(getApiClient(), getResources().getString(scoreId), score);
	}
	
	public void openAchievements(){
		if(loggedIn()){
			startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 0);
		}
	}
	
	public void openHighscores(){
		if(loggedIn()){
			startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), 1);
		}
	}
	
	@Override
	public void onSignInFailed() {
		registerred = false;
		thread.saveSettings();
	}

	@Override
	public void onSignInSucceeded() {
		registerred = true;
		thread.saveSettings();
	}
}