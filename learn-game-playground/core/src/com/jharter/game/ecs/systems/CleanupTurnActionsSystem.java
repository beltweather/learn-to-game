package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CleanupTurnActionComp;
import com.jharter.game.ecs.components.Components.ToDiscardZoneComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class CleanupTurnActionsSystem extends GameIteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public CleanupTurnActionsSystem() {
		super(Family.all(TurnActionComp.class, CleanupTurnActionComp.class).get());
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Comp.TurnActionComp.get(entity).turnAction.cleanUp();
		entity.remove(CleanupTurnActionComp.class);
	}
	
}
