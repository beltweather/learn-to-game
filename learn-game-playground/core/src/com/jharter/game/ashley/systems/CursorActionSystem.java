package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Mapper;

public class CursorActionSystem  extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorActionSystem() {
		super(Family.all(CursorComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorComp c = Mapper.CursorComp.get(entity);
		
		TargetingComp t = c.getTargetingComp();
		if(t == null || !t.hasAllTargets()) {
			return;
		}
		
		t.performAcceptCallback();
		
		// Clear out data related to action
		c.targetingEntityID = null;
		t.targetIDs.clear();
		
	}
	
}
