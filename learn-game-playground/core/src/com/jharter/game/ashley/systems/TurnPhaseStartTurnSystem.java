package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.IDUtil;

public class TurnPhaseStartTurnSystem extends TurnPhaseSystem {

	public TurnPhaseStartTurnSystem() {
		super(TurnPhaseStartTurnComp.class, TurnPhaseSelectEnemyActionsComp.class);
		add(ActivePlayerComp.class, Family.all(ActivePlayerComp.class).get());
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		ActivePlayerComp a = getFirstComponent(ActivePlayerComp.class);
		setPlayer(a, 0);
		a.spentPlayers.clear();
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
	private void setPlayer(ActivePlayerComp a, int index) {
		if(!ArrayUtil.has(IDUtil.getPlayerIDs(), index)) {
			index = 0;
		}
		a.activePlayerID = IDUtil.getPlayerIDs().get(index);
	}
	
}
