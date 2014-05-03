package com.jmpmain.lvslrpg.particles;
import android.graphics.Canvas;

/**
 * Base class for a particle.
 */
public abstract class Particle {
	
	/** Should this particle be destroyed. */
	public boolean destroy;
	
	
	public abstract void update(long time);
	public abstract void draw(Canvas canvas);
}
