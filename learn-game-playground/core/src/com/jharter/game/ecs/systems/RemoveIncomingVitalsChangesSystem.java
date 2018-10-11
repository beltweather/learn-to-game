package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorUntargetEvent;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class RemoveIncomingVitalsChangesSystem extends GameIteratingSystem {

	public RemoveIncomingVitalsChangesSystem() {
		super(Family.all(CursorUntargetEvent.class, VitalsComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CursorUntargetEvent u = Comp.CursorUntargetEvent.get(entity);
		VitalsComp v = Comp.VitalsComp.get(entity);
		CursorComp c = Comp.CursorComp.get(u.cursorID);
		if(c.turnActionID != null) {
			if(v.incomingDamage.containsKey(c.turnActionID)) {
				v.incomingDamage.remove(c.turnActionID);
			}
			if(v.incomingHealing.containsKey(c.turnActionID)) {
				v.incomingHealing.remove(c.turnActionID);
			}
		}
	}

}
