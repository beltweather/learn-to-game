package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseStartTurnComp;

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
		Mapper.resetActivePlayer();
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
}
