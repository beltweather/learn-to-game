package com.jharter.game.ashley.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectFriendActionsComp;

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
