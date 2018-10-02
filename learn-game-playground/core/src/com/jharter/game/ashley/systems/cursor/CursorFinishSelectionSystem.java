package com.jharter.game.ashley.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PendingTurnActionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;

public class CursorFinishSelectionSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorFinishSelectionSystem() {
		super();
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		TurnAction t = getTurnAction(c);
		if(t == null || !t.hasAllTargets()) {
			return;
		}
		
		ActivePlayerComp a = getActivePlayer();
		a.spentPlayers.add(a.activePlayerID);
		nextPlayer();
		
		Comp.swap(getEngine(), PendingTurnActionComp.class, ActionQueueableComp.class, Comp.Entity.get(c.turnActionID));
		c.reset();
	}
	
}
