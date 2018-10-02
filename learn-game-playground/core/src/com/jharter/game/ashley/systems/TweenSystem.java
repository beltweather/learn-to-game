package com.jharter.game.ashley.systems;

import com.jharter.game.ashley.systems.boilerplate.CustomEntitySystem;

public class TweenSystem extends CustomEntitySystem {
	
	public TweenSystem() {
		
	}
	
	@Override
	public void performUpdate(float deltaTime) {
		getTweenManager().update(deltaTime);
	}
	
}
