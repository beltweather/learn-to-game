package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.M;

public class ApproachTargetSystem extends IteratingSystem {
	
	private static final float ALPHA = 0.01f;
	private static final float MIN_DIFF = 1.0f;

	@SuppressWarnings("unchecked")
	public ApproachTargetSystem() {
		super(Family.all(SpriteComp.class, TargetPositionComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		SpriteComp s = M.SpriteComp.get(entity);
		TargetPositionComp t = M.TargetPositionComp.get(entity);
		
		if(t.position != null) {
			float alpha = ALPHA;
			float newX = Interpolation.linear.apply(s.position.x, t.position.x, alpha);
	    	float newY = Interpolation.linear.apply(s.position.y, t.position.y, alpha);
	    	if(isCloseEnough(s.position.x, t.position.x) &&
			   isCloseEnough(s.position.y, t.position.y)) {	
	    		t.position = null;
	    		// Optionally, could just remove target comp here
			} else {
				BodyComp b = M.BodyComp.get(entity);
				setPosition(b.body, newX, newY, s.width, s.height, s.position);
			}
		}
	}
	
	public void setPosition(Body body, float x, float y, float width, float height, Vector3 pos) {
		if(body == null) {
			pos.x = x;
			pos.y = y;
		} else {
			body.setTransform(x+width/2, y+height/4, body.getAngle());
			pos.x = body.getPosition().x - width/2;
			pos.y = body.getPosition().y - height/4;
		}
    }

	private boolean isCloseEnough(float n0, float n1) {
		return Math.abs(n0 - n1) < MIN_DIFF;
	}
}
