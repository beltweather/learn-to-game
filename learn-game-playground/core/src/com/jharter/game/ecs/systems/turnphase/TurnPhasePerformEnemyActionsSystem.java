package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.TurnPhaseEndTurnComp;
import com.jharter.game.ecs.components.Components.TurnPhasePerformEnemyActionsComp;

public class TurnPhasePerformEnemyActionsSystem extends TurnPhaseSystem {

	public TurnPhasePerformEnemyActionsSystem() {
		super(TurnPhasePerformEnemyActionsComp.class, TurnPhaseEndTurnComp.class);
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
