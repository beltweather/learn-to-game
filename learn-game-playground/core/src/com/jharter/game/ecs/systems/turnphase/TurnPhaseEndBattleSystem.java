package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.TurnPhaseEndBattleTag;
import com.jharter.game.ecs.components.Components.TurnPhaseNoneTag;

public class TurnPhaseEndBattleSystem extends TurnPhaseSystem {

	public TurnPhaseEndBattleSystem() {
		super(TurnPhaseEndBattleTag.class, TurnPhaseNoneTag.class);
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
