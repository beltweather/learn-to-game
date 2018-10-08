package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CursorUntargetEvent;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class RemoveIncomingVitalsChangesSystem extends GameIteratingSystem {

	public RemoveIncomingVitalsChangesSystem() {
		super(Family.all(CursorUntargetEvent.class, VitalsComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VitalsComp v = Comp.VitalsComp.get(entity);
		if(v.incomingDamage != 0) {
			v.incomingDamage = 0;
		}
		if(v.incomingHealing != 0) {
			v.incomingHealing = 0;
		}
	}

}
