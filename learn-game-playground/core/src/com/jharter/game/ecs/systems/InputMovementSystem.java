package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.jharter.game.ecs.components.Components.BodyComp;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.VelocityComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class InputMovementSystem extends GameIteratingSystem {

	@SuppressWarnings("unchecked")
	public InputMovementSystem() {
		super(Family.all(SpriteComp.class, 
						 VelocityComp.class, 
						 InputComp.class,
						 BodyComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		VelocityComp v = Comp.VelocityComp.get(entity);
		InputComp in = Comp.InputComp.get(entity);
		BodyComp b = Comp.BodyComp.get(entity);
		
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
