package com.jmpmain.lvslrpg.entities;

import android.graphics.Canvas;

public abstract class Entity {
	public abstract void update(long time);
	
	public abstract void draw(Canvas canvas);
	
}