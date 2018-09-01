package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.M;

public class InputMovementSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public InputMovementSystem() {
		super(Family.all(SpriteComp.class, 
						 VelocityComp.class, 
						 InputComp.class,
						 BodyComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		SpriteComp s = M.SpriteComp.get(entity);
		VelocityComp v = M.VelocityComp.get(entity);
		InputComp in = M.InputComp.get(entity);
		BodyComp b = M.BodyComp.get(entity);
		
		s.direction.x = 0;
		s.direction.y = 0;
		
	    if (in.input.isDown())  s.direction.y = -1;
	    if (in.input.isUp())    s.direction.y = 1;
	    if (in.input.isLeft())  s.direction.x = -1;
	    if (in.input.isRight()) s.direction.x = 1;
	    
	    b.body.setLinearVelocity(s.direction.x * v.speed, s.direction.y * v.speed);
	    b.body.setTransform(b.body.getWorldCenter(), s.angleDegrees*MathUtils.degreesToRadians);
	    s.position.x = b.body.getPosition().x - s.width/2;
	    s.position.y = b.body.getPosition().y - s.height/4;
	}
	
}
