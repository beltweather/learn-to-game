package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.jharter.game.tween.TweenUtil;

public class TweenSystem extends EntitySystem {
	
	public TweenSystem() {
		TweenUtil.init();
	}
	
	@Override
	public void update(float deltaTime) {
		TweenUtil.update(deltaTime);
	}
	
}
