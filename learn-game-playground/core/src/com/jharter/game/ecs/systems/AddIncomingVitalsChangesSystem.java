package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.CursorTargetEvent;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class AddIncomingVitalsChangesSystem extends GameIteratingSystem {

	public AddIncomingVitalsChangesSystem() {
		super(Family.all(CursorTargetEvent.class, VitalsComp.class, CursorTargetComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VitalsComp v = Comp.VitalsComp.get(entity);
		CursorComp c = Comp.CursorComp.get(Comp.CursorTargetComp.get(entity).cursorID);
		if(c.turnActionID == null) {
			return;
		}
		TurnAction t = Comp.TurnActionComp.get(c.turnActionID).turnAction;
		if(v.incomingDamage != t.incomingDamage) {
			v.incomingDamage = t.incomingDamage;
		}
		if(v.incomingHealing != t.incomingHealing) {
			v.incomingHealing = t.incomingHealing;
		}
	}

}
