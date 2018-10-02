package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.TurnPhaseEndBattleComp;
import com.jharter.game.ecs.components.Components.TurnPhaseNoneComp;

public class TurnPhaseEndBattleSystem extends TurnPhaseSystem {

	public TurnPhaseEndBattleSystem() {
		super(TurnPhaseEndBattleComp.class, TurnPhaseNoneComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
}
