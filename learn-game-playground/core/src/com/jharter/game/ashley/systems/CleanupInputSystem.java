package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.InputComp;

public class CleanupInputSystem extends IteratingSystem {

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
