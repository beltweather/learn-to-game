package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.util.ArrayUtil;

import uk.co.carelesslabs.Enums.CardOwnerAction;

public class TurnPhaseStartTurnSystem extends TurnPhaseSystem {

	public TurnPhaseStartTurnSystem() {
		super(TurnPhaseStartTurnComp.class, TurnPhaseSelectActionsComp.class);
		all(ActivePlayerComp.class);
		all(CardOwnerComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnPhase, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity turnPhase, float deltaTime) {
		resetPlayers();
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnPhase, float deltaTime) {
		
	}
	
	private void resetPlayers() {
		ActivePlayerComp a = comp(ActivePlayerComp.class);
		setPlayer(a, 0);
		a.spentPlayers.clear();
		fillHands();
	}
	
	private void setPlayer(ActivePlayerComp a, int index) {
		if(!ArrayUtil.has(getPlayerIDs(), index)) {
			index = 0;
		}
		a.activePlayerID = getPlayerIDs().get(index);
	}
	
	private void fillHands() {
		for(CardOwnerComp c : comps(CardOwnerComp.class)) {
			c.actions.add(CardOwnerAction.FILL_HAND);
		}
	}
	
}
