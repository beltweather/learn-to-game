package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.VelocityComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class VelocityMovementSystem extends GameIteratingSystem {

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
