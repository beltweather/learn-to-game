package com.jharter.game.ashley.systems;

import com.jharter.game.ashley.systems.boilerplate.GameEntitySystem;

public class TweenSystem extends GameEntitySystem {
	
	public TweenSystem() {
		
	}
	
	@Override
	public void performUpdate(float deltaTime) {
		getTweenManager().update(deltaTime);
	}
	
}
