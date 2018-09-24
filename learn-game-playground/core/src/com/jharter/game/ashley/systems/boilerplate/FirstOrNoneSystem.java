package com.jharter.game.ashley.systems.boilerplate;

import com.badlogic.ashley.core.Family;

public abstract class FirstOrNoneSystem extends CustomIteratingSystem {

	public FirstOrNoneSystem(Family family) {
		super(family);
	}
	
	public FirstOrNoneSystem (Family family, int priority) {
		super(family, priority);
	}
	
	@Override
	public void update (float deltaTime) {
		super.update(deltaTime);
		if(entities.size() > 0) {
			processEntity(entities.first(), deltaTime);
		} else {
			processNone(deltaTime);
		}
	}
	
	protected abstract void processNone(float deltaTime);

}
