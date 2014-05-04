package com.jmpmain.lvslrpg.entities;

import java.util.Vector;

import com.jmpmain.lvslrpg.GameThread;

import android.graphics.Point;


public class AILineEntity extends LineEntity {

	public AILineEntity(int pX, int pY) {
		super(pX, pY);
		setDirection(1,0);
	}

	private Vector<Point> getAvailableDirections(){
		Vector<Point> directions = new Vector<Point>();
		
		if(xVelocity > 0){
			directions.add(new Point(0, 1));
			directions.add(new Point(0, -1));
		}else{
			directions.add(new Point(1, 0));
			directions.add(new Point(-1, 0));
		}
		
		return directions;
	}
	
	private float playerDistanceHeuristic(int tx, int ty){
		return Math.abs(GameThread.instance.line.x - tx) + Math.abs(GameThread.instance.line.y - ty);
	}
	
	private Vector<Point> getFreeDirections(){
		Vector<Point> directions = new Vector<Point>();
		
		if(xVelocity > 0){
			if(isEmpty(lastXCheck, lastYCheck+1))
				directions.add(new Point(0, 1));
			if(isEmpty(lastXCheck, lastYCheck-1))
				directions.add(new Point(0, -1));
		}else{
			if(isEmpty(lastXCheck+1, lastYCheck))
				directions.add(new Point(1, 0));
			if(isEmpty(lastXCheck-1, lastYCheck))
				directions.add(new Point(-1, 0));
		}
		
		return directions;
	}
	
	@Override
	public void update(long time) {
		
		float tx = x + xVelocity;
		float ty = y + yVelocity;
		if(lastXCheck != (int)tx || lastYCheck != (int)ty){
			if(!isEmpty(tx, ty)){
				Vector<Point> freeDirections = getFreeDirections();
				if(freeDirections.size() > 0){
					int random = (int)(Math.random()*freeDirections.size());
					setDirection(freeDirections.get(random).x, freeDirections.get(random).y);
				}else{
					Vector<Point> directions = getAvailableDirections();
					int random = (int)(Math.random()*directions.size());
					setDirection(directions.get(random).x, directions.get(random).y);
				}
			}else{
				//Randomly turn in a free direction.
				if(Math.random()> 0.98){
					Vector<Point> freeDirections = getFreeDirections();
					if(freeDirections.size() > 0){
						int random = (int)(Math.random()*freeDirections.size());
						setDirection(freeDirections.get(random).x, freeDirections.get(random).y);
					}
				}
			}
		}
		
		super.update(time);
	}
}
