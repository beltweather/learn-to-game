package com.jharter.game.ecs.systems.boilerplate;

import com.badlogic.ashley.core.Family;

public abstract class FirstOrNoneSystem extends GameIteratingSystem {

	public FirstOrNoneSystem(Family family) {
		super(family);
	}
	
	public FirstOrNoneSystem (Family family, int priority) {
		super(family, priority);
	}
	
	@Override
	public void performUpdate (float deltaTime) {
		if(entities.size() > 0) {
			processEntity(entities.first(), deltaTime);
		} else {
			processNone(deltaTime);
		}
	}
	
	protected abstract void processNone(float deltaTime);

}
