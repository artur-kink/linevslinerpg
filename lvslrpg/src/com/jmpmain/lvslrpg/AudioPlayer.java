package com.jmpmain.lvslrpg;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Audio playing class. Loads and plays audio files.
 */
public class AudioPlayer {

	/** Program context. */
	public static Context context;

	/** Sound pool containing and playing audio files. */
	private static SoundPool soundPool;

	public static int city;
	public static int coin;
	public static int dead;
	public static int hit;
	public static int potion;

	/**
	 * Initializes audio and sound player.
	 * Must be called before AudioPlayer can be used.
	 */
	public static void initSounds() {
	     soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

	     city = soundPool.load(context, R.raw.city, 1);
	     coin = soundPool.load(context, R.raw.coin, 1);
	     dead = soundPool.load(context, R.raw.dead, 1);
	     hit = soundPool.load(context, R.raw.hit, 1);
	     potion = soundPool.load(context, R.raw.potion, 1);
	}

	public static void playSound(int id) {
		if(GameThread.SoundOn){
		    AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
		    float volume = streamVolumeCurrent / streamVolumeMax;
	
		    //Play sound.
		    soundPool.play(id, volume, volume, 1, 0, 1f);
		}
	}
}