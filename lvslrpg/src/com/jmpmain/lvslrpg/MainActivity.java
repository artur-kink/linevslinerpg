package com.jmpmain.lvslrpg;

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

/**
 * The main activity.
 */
public class MainActivity extends Activity {
	private GameSurface surface;
	
	public static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		//Remove title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Set as full screen and set main view to GameSurface.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		surface = new GameSurface(this);
		
		//Create frame layout to contain game surface and ui layout.
		FrameLayout baseLayout = new FrameLayout(this);
		baseLayout.addView(surface);
		
		//Create ui layout and add it above the game surface.
		AbsoluteLayout uiLayout = new AbsoluteLayout(this);
		baseLayout.addView(uiLayout);
		surface.thread.uiLayout = uiLayout;
		
		setContentView(baseLayout);
		
		//Register accelerator to record tilt of screen.
		((SensorManager)getSystemService(Context.SENSOR_SERVICE)).registerListener(
			surface.thread, ((SensorManager)getSystemService(Context.SENSOR_SERVICE))
			.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);
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