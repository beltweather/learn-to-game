package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartBattleComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnComp;

import uk.co.carelesslabs.Enums.CardOwnerAction;

public class TurnPhaseStartBattleSystem extends TurnPhaseSystem {
	
	public TurnPhaseStartBattleSystem() {
		super(TurnPhaseStartBattleComp.class, TurnPhaseStartTurnComp.class);
		all(CardOwnerComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnPhase, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity turnPhase, float deltaTime) {
		setupCards();
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnPhase, float deltaTime) {
		
	}
	
	protected void setupCards() {
		for(CardOwnerComp c : comps(CardOwnerComp.class)) {
			c.actions.add(CardOwnerAction.RESET_CARDS);
		}
	}
	
}
