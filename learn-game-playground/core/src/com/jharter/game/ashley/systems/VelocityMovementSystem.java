package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.systems.boilerplate.CustomIteratingSystem;

public class VelocityMovementSystem extends CustomIteratingSystem {

	@SuppressWarnings("unchecked")
	public VelocityMovementSystem() {
		super(Family.all(SpriteComp.class, VelocityComp.class).exclude(InputComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		VelocityComp v = Comp.VelocityComp.get(entity);
		
		s.position.x += v.velocity.x * deltaTime;
		s.position.y += v.velocity.y * deltaTime;
	}

}
