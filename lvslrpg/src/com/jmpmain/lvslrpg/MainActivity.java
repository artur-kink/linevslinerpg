package com.jmpmain.lvslrpg;

import com.google.android.gms.ads.*;

import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * The main activity.
 */
public class MainActivity extends Activity {
	
	private GameSurface surface;
	private GameThread thread;
	
	public static Context context;
	
	public static Typeface pixelFont;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		//Remove title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
}