package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;

public class ApproachTargetSystem extends IteratingSystem {
	
	private static final float ALPHA = 0.01f;
	private static final float MIN_DIFF = 1.0f;

	@SuppressWarnings("unchecked")
	public ApproachTargetSystem() {
		super(Family.all(PositionComp.class, TargetPositionComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		PositionComp p = Mapper.PositionComp.get(entity);
		TargetPositionComp t = Mapper.TargetPositionComp.get(entity);
		
		if(t.position != null) {
			float alpha = ALPHA;
	    	p.position.x = Interpolation.linear.apply(p.position.x, t.position.x, alpha);
	    	p.position.y = Interpolation.linear.apply(p.position.y, t.position.y, alpha);
	    	if(isCloseEnough(p.position.x, t.position.x) &&
			   isCloseEnough(p.position.y, t.position.y)) {	
	    		t.position = null;
	    		// Optionally, could just remove target comp here
			}
		}
	}

	private boolean isCloseEnough(float n0, float n1) {
		return Math.abs(n0 - n1) < MIN_DIFF;
	}
}
