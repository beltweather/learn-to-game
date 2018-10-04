package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.ActiveTurnActionComp;
import com.jharter.game.ecs.components.Components.CleanupTurnActionComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class CleanupTurnActionsSystem extends GameIteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public CleanupTurnActionsSystem() {
		super(Family.all(TurnActionComp.class, CleanupTurnActionComp.class).get());
	}
	
	@Override
	public void processEntity(Entity turnActionEntity, float deltaTime) {
		cleanUp(turnActionEntity);
		turnActionEntity.remove(CleanupTurnActionComp.class);
	}
	
	private void cleanUp(Entity turnActionEntity) {
		TurnAction t = Comp.TurnActionComp.get(turnActionEntity).turnAction;
		t.multiplicity = t.defaultMultiplicity;
		t.all = t.defaultAll;
		t.targetIDs.clear();
		Entity owner = t.getOwnerEntity();
		Comp.remove(ActiveTurnActionComp.class, owner);
		Comp.remove(PendingTurnActionComp.class, t.getEntity());
	}
	
}
