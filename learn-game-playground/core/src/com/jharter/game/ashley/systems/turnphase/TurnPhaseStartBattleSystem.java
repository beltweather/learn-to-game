package com.jharter.game.ashley.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.TurnPhaseStartBattleComp;
import com.jharter.game.ashley.components.Components.TurnPhaseStartTurnComp;

public class TurnPhaseStartBattleSystem extends TurnPhaseSystem {

	public TurnPhaseStartBattleSystem() {
		super(TurnPhaseStartBattleComp.class, TurnPhaseStartTurnComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnPhase, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity turnPhase, float deltaTime) {
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnPhase, float deltaTime) {
		
	}
	
}
