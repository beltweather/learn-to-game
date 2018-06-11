package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.Mapper;

public class InputMovementSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public InputMovementSystem() {
		super(Family.all(PositionComp.class, 
						 VelocityComp.class, 
						 InputComp.class,
						 BodyComp.class,
						 SizeComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		PositionComp p = Mapper.PositionComp.get(entity);
		VelocityComp v = Mapper.VelocityComp.get(entity);
		InputComp in = Mapper.InputComp.get(entity);
		BodyComp b = Mapper.BodyComp.get(entity);
		SizeComp s = Mapper.SizeComp.get(entity);
		
		p.direction.x = 0;
		p.direction.y = 0;
		
	    if (in.input.isDown())  p.direction.y = -1;
	    if (in.input.isUp())    p.direction.y = 1;
	    if (in.input.isLeft())  p.direction.x = -1;
	    if (in.input.isRight()) p.direction.x = 1;
	    
	    b.body.setLinearVelocity(p.direction.x * v.speed, p.direction.y * v.speed);
	    p.position.x = b.body.getPosition().x - s.width/2;
	    p.position.y = b.body.getPosition().y - s.height/4;
	}
	
}
