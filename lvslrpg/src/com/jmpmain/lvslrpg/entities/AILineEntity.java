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
					if(Math.random() > 0.5)
						setDirection(0, 1);
					else
						setDirection(0, -1);
				}else{
					if(Math.random() > 0.5)
						setDirection(1, 0);
					else
						setDirection(-1, 0);
				}
			}
		}
		
		super.update(time);
	}
}
