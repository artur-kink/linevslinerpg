package com.jmpmain.lvslrpg;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * The main activity.
 */
public class MainActivity extends Activity {
	private GameSurface surface;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Set as full screen and set main view to GameSurface.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		surface = new GameSurface(this);
		setContentView(surface);
		
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