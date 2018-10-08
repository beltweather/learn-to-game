package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.TurnPhaseEndTurnTag;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnTag;

public class TurnPhaseEndTurnSystem extends TurnPhaseSystem {

	public TurnPhaseEndTurnSystem() {
		super(TurnPhaseEndTurnTag.class, TurnPhaseStartTurnTag.class);
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
