package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.PendingTurnActionTag;
import com.jharter.game.ecs.components.Components.PendingVitalsComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class ApplyPendingTurnActionsSystem extends GameIteratingSystem {

	public ApplyPendingTurnActionsSystem() {
		super(Family.all(PendingTurnActionTag.class, TurnActionComp.class).get());
		add(PendingVitalsComp.class, Family.all(PendingVitalsComp.class, VitalsComp.class).get());
	}

	@Override
	public void beforeUpdate(float deltaTime) {
		clearComps(PendingVitalsComp.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Comp.TurnActionComp.get(entity).turnAction.perform(true);
	}

}
