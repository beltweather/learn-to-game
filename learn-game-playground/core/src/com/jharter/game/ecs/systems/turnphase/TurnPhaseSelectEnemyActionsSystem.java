package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectFriendActionsComp;

public class TurnPhaseSelectEnemyActionsSystem extends TurnPhaseSystem {

	public TurnPhaseSelectEnemyActionsSystem() {
		super(TurnPhaseSelectEnemyActionsComp.class, TurnPhaseSelectFriendActionsComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		return isDoneAnimating();
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
}
