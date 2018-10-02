package com.jharter.game.ashley.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PendingTurnActionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

public class CursorFinishSelectionSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorFinishSelectionSystem() {
		super();
	}

	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		CursorComp c = Comp.CursorComp.get(cursor);
		TurnAction t = getTurnAction(c);
		if(t == null || !t.hasAllTargets()) {
			return;
		}
		
		ID playerID = Comp.Entity.Cursor(cursor).getPlayerID();
		ActivePlayerComp a = getActivePlayer();
		a.spentPlayers.add(playerID);
		Comp.ActivePlayerComp(a).nextPlayer();
		
		Comp.swap(getEngine(), PendingTurnActionComp.class, ActionQueueableComp.class, Comp.Entity.get(c.turnActionID));
		c.reset();
	}
	
}
