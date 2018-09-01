package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.M;

public class VelocityMovementSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public VelocityMovementSystem() {
		super(Family.all(SpriteComp.class, VelocityComp.class).exclude(InputComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		SpriteComp s = M.SpriteComp.get(entity);
		VelocityComp v = M.VelocityComp.get(entity);
		
		s.position.x += v.velocity.x * deltaTime;
		s.position.y += v.velocity.y * deltaTime;
	}

}
