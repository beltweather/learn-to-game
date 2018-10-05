package com.jharter.game.tween.machine;

import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.U;

public class CombatMachine extends TweenMachine<CombatMachine> {

	public CombatMachine(IEntityHandler handler) {
		super(handler);
	}
	
	public CombatMachine spinAndDissapear() {
		 return moveX(-U.u12(75))
			.setScaleX(0)
			.setScaleY(0)
			.setAngle(3600/2)
			.setAlpha(0)
			.setDuration(1f);
	}
	
	public CombatMachine rockForward() {
		return moveX(-U.u12(10))
			.moveY(U.u12(4))
			.moveAngle(20)
			.setDuration(0.25f);
	}
	
	public CombatMachine rockBackward() {
		return moveX(U.u12(10))
			.moveY(-U.u12(4))
			.moveAngle(-20)
			.setDuration(0.25f);
	}
	
}
