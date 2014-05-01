package com.jmpmain.lvslrpg.entities;


public class AILineEntity extends LineEntity {

	public AILineEntity(int pX, int pY) {
		super(pX, pY);
		setDirection(1,0);
	}

	@Override
	public void update(long time) {
		
		float tx = x + xVelocity;
		float ty = y + yVelocity;
		if(lastXCheck != (int)tx || lastYCheck != (int)ty){
			if(!isEmpty(tx, ty)){
				if(xVelocity > 0){
					setDirection(0, (float) ((int)Math.random()*2 - 1));
				}else{
					setDirection((float) ((int)Math.random()*2 - 1), 0);
				}
			}
		}
		
		super.update(time);
	}
}
