package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.ashley.components.Ent;

public class TurnPhaseStartTurnSystem extends TurnPhaseSystem {

	public TurnPhaseStartTurnSystem() {
		super(TurnPhaseStartTurnComp.class, TurnPhaseSelectEnemyActionsComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		Ent.resetActivePlayerEntity();
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
}
