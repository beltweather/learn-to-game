package com.jharter.game.ashley.systems.boilerplate;

import com.badlogic.ashley.core.Family;

public abstract class FirstSystem extends CustomIteratingSystem {

	public FirstSystem(Family family) {
		super(family);
	}
	
	public FirstSystem (Family family, int priority) {
		super(family, priority);
	}
	
	@Override
	public void update (float deltaTime) {
		if(entities.size() > 0) {
			processEntity(entities.first(), deltaTime);
		}
	}
	
}
