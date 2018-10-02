package com.jharter.game.ecs.systems;

import com.jharter.game.ecs.systems.boilerplate.GameEntitySystem;

public class TweenSystem extends GameEntitySystem {
	
	public TweenSystem() {
		
	}
	
	@Override
	public void performUpdate(float deltaTime) {
		getTweenManager().update(deltaTime);
	}
	
}
