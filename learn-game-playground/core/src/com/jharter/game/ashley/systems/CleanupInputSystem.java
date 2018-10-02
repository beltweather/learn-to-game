package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.systems.boilerplate.CustomIteratingSystem;

public class CleanupInputSystem extends CustomIteratingSystem {

	public CleanupInputSystem() {
		super(Family.all(InputComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputComp in = Comp.InputComp.get(entity);
		if(in.input != null) {
			in.input.reset();
		}
	}

}
