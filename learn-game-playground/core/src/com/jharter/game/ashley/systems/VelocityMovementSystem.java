package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.VelocityComp;

public class VelocityMovementSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public VelocityMovementSystem() {
		super(Family.all(PositionComp.class, VelocityComp.class).exclude(InputComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		PositionComp p = Mapper.PositionComp.get(entity);
		VelocityComp v = Mapper.VelocityComp.get(entity);
		
		p.position.x += v.velocity.x * deltaTime;
		p.position.y += v.velocity.y * deltaTime;
	}

}
