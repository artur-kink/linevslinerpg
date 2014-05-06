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
	
	private Vector<Point> getFreeDirections(int tx, int ty){
		Vector<Point> directions = new Vector<Point>();
		
		if(xVelocity > 0){
			if(isEmpty(tx, ty+1))
				directions.add(new Point(0, 1));
			if(isEmpty(tx, ty-1))
				directions.add(new Point(0, -1));
		}else{
			if(isEmpty(tx+1, ty))
				directions.add(new Point(1, 0));
			if(isEmpty(tx-1, ty))
				directions.add(new Point(-1, 0));
		}
		
		return directions;
	}
	
	@Override
	public void update(long time) {
		
		float tx = x + getTileXVelocity(lastXCheck, lastYCheck);;
		float ty = y + getTileYVelocity(lastXCheck, lastYCheck);;
		if((int)tx != lastXCheck || (int)ty != lastYCheck){
			boolean turned = false;
			for(int i = 1; i < 4; i++){
				tx = lastXCheck + i*Math.signum(xVelocity);
				ty = lastYCheck + i*Math.signum(yVelocity);
				if(!isEmpty(tx, ty) && Math.random()*(float)i < 1){
					Vector<Point> freeDirections = getFreeDirections(lastXCheck, lastYCheck);
					if(freeDirections.size() > 0){
						int random = (int)(Math.random()*freeDirections.size());
						setDirection(freeDirections.get(random).x, freeDirections.get(random).y);
					}else{
						Vector<Point> directions = getAvailableDirections();
						int random = (int)(Math.random()*directions.size());
						setDirection(directions.get(random).x, directions.get(random).y);
					}
					turned = true;
					break;
				}
			}
			
			if(turned == false){
				//Randomly turn in a free direction.
				if(Math.random() > 0.95){
					Vector<Point> freeDirections = getFreeDirections(lastXCheck, lastYCheck);
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
